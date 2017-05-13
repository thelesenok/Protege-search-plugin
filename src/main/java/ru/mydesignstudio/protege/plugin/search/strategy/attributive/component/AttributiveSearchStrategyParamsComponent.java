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
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.AttributiveProcessorParams;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.renderer.JComboboxIconRenderer;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainClass;
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
public class AttributiveSearchStrategyParamsComponent extends JPanel implements SearchStrategyComponent<AttributiveProcessorParams> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttributiveSearchStrategyParamsComponent.class);
    @Inject
    private OWLService owlService;
    @Inject
    private ExceptionWrapperService wrapperService;

    private EventBus eventBus = EventBus.getInstance();

    private SelectQuery selectQuery;

    private JPanel targetContainer = new JPanel(new GridLayout(1, 3));
    private JPanel criteriaContainer = new JPanel(new BorderLayout());
    private final JComboBox<OWLDomainClass> targetTypeSelector = new JComboBox<OWLDomainClass>();

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
        final AttributiveSearchParamsTable criteriaTable = new AttributiveSearchParamsTable(
                selectQuery,
                owlService,
                wrapperService
        );
        final JScrollPane scrollPane = new JScrollPane(criteriaTable);
        criteriaTable.setFillsViewportHeight(true);
        //
        criteriaContainer.add(scrollPane, BorderLayout.CENTER);
    }

    private void fillTargetTypeSelector() throws ApplicationException {
        targetTypeSelector.setRenderer(new JComboboxIconRenderer());
        CollectionUtils.forEach(CollectionUtils.map(owlService.getClasses(), new Transformer<OWLClass, OWLDomainClass>() {
                    @Override
                    public OWLDomainClass transform(OWLClass item) {
                        return new OWLDomainClass(item);
                    }
                }), new Action<OWLDomainClass>() {
                    @Override
                    public void run(OWLDomainClass uiClass) {
                        targetTypeSelector.addItem(uiClass);
                    }
                });
        //
        final JButton addCriteriaButton = new JButton("+");
        //
        targetContainer.add(new Label("Target type"));
        targetContainer.add(targetTypeSelector);
        targetContainer.add(addCriteriaButton);
        //
        targetTypeSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    final OWLDomainClass item = (OWLDomainClass) e.getItem();
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
    public AttributiveProcessorParams getSearchParams() {
        return new AttributiveProcessorParams(selectQuery.clone());
    }

    @Override
    public void setSearchParams(AttributiveProcessorParams params) {
        final SelectQuery loadedQuery = params.getSelectQuery();
        /**
         * выберем целевой объект
         */
        final OWLDomainClass targetClass = new OWLDomainClass(
                loadedQuery.getFrom().getOwlClass()
        );
        targetTypeSelector.setSelectedItem(targetClass);
        /**
         * удалим старые условия поиска
         */
        selectQuery.emptyWhereParts();
        /**
         * добавим новые условия
         */
        for (WherePart wherePart : loadedQuery.getWhereParts()) {
            selectQuery.addWherePart(wherePart);
        }
    }
}
