package ru.mydesignstudio.protege.plugin.search.strategy.attributive.component;

import org.semanticweb.owlapi.model.OWLClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.FromType;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchStrategyComponent;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.service.EventBus;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.collector.AttributiveCollectorParams;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.renderer.JComboboxIconRenderer;
import ru.mydesignstudio.protege.plugin.search.ui.model.OWLUIClass;
import ru.mydesignstudio.protege.plugin.search.utils.Action;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;
import ru.mydesignstudio.protege.plugin.search.utils.Transformer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by abarmin on 03.01.17.
 */
public class AttributiveSearchStrategyParamsComponent extends JPanel implements SearchStrategyComponent<AttributiveCollectorParams> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttributiveSearchStrategyParamsComponent.class);
    @Inject
    private OWLService owlService;
    @Inject
    private ExceptionWrapperService wrapperService;

    private EventBus eventBus = EventBus.getInstance();

    private SelectQuery selectQuery;

    private JPanel targetContainer = new JPanel(new GridLayout(1, 3));
    private JPanel criteriaContainer = new JPanel(new BorderLayout());

    public AttributiveSearchStrategyParamsComponent() {
        setLayout(new BorderLayout());
        add(targetContainer, BorderLayout.NORTH);
        add(criteriaContainer, BorderLayout.CENTER);
        //
        selectQuery = new SelectQuery();
        selectQuery.addWherePart(new WherePart());
    }

    @PostConstruct
    public void init() {
        eventBus.register(this);

        try {
            fillTargetTypeSelector();
            fillCriteriaTable();
        } catch (ApplicationException e) {
            LOGGER.warn("Can't initialize attributive search component {}", e);
        }
    }

    private void fillCriteriaTable() {
        final AttributiveSearchParamsTable criteriaTable = new AttributiveSearchParamsTable(selectQuery, owlService, wrapperService);
        final JScrollPane scrollPane = new JScrollPane(criteriaTable);
        criteriaTable.setFillsViewportHeight(true);
        //
        criteriaContainer.add(scrollPane, BorderLayout.CENTER);
    }

    private void fillTargetTypeSelector() throws ApplicationException {
        final JComboBox<OWLUIClass> typeSelector = new JComboBox<OWLUIClass>();
        typeSelector.setRenderer(new JComboboxIconRenderer());
        CollectionUtils.forEach(CollectionUtils.map(owlService.getClasses(), new Transformer<OWLClass, OWLUIClass>() {
                    @Override
                    public OWLUIClass transform(OWLClass item) {
                        return new OWLUIClass(item);
                    }
                }), new Action<OWLUIClass>() {
                    @Override
                    public void run(OWLUIClass uiClass) {
                        typeSelector.addItem(uiClass);
                    }
                });
        //
        final JButton addCriteriaButton = new JButton("+");
        //
        targetContainer.add(new Label("Target type"));
        targetContainer.add(typeSelector);
        targetContainer.add(addCriteriaButton);
        //
        typeSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    final OWLUIClass item = (OWLUIClass) e.getItem();
                    selectQuery.setFrom(new FromType(item.getOwlClass()));
                }
            }
        });
        //
        addCriteriaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectQuery.addWherePart(new WherePart());
            }
        });
    }

    @Override
    public AttributiveCollectorParams getSearchParams() {
        return new AttributiveCollectorParams(selectQuery);
    }
}
