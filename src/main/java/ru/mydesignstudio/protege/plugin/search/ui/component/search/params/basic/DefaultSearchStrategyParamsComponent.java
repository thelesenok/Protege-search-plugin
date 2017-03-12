package ru.mydesignstudio.protege.plugin.search.ui.component.search.params.basic;

import org.semanticweb.owlapi.model.OWLClass;
import ru.mydesignstudio.protege.plugin.search.api.SearchStrategyComponent;
import ru.mydesignstudio.protege.plugin.search.api.query.FromType;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.service.EventBus;
import ru.mydesignstudio.protege.plugin.search.ui.component.renderer.JComboboxIconRenderer;
import ru.mydesignstudio.protege.plugin.search.ui.model.OWLUIClass;
import ru.mydesignstudio.protege.plugin.search.utils.Action;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
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
public class DefaultSearchStrategyParamsComponent extends JPanel implements SearchStrategyComponent {
    @Inject
    private OWLService owlService;

    private EventBus eventBus = EventBus.getInstance();

    private SelectQuery selectQuery;

    private JPanel targetContainer = new JPanel(new GridLayout(1, 3));
    private JPanel criteriaContainer = new JPanel(new BorderLayout());

    public DefaultSearchStrategyParamsComponent() {
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

        fillTargetTypeSelector();
        fillCriteriaTable();
    }

    private void fillCriteriaTable() {
        final DefaultSearchParamsTable criteriaTable = new DefaultSearchParamsTable(selectQuery, owlService);
        final JScrollPane scrollPane = new JScrollPane(criteriaTable);
        criteriaTable.setFillsViewportHeight(true);
        //
        criteriaContainer.add(scrollPane, BorderLayout.CENTER);
    }

    private void fillTargetTypeSelector() {
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
    public SelectQuery getSelectQuery() {
        return selectQuery;
    }
}
