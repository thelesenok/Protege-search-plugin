package ru.mydesignstudio.protege.plugin.search.ui.component;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import ru.mydesignstudio.protege.plugin.search.api.SearchStrategyComponent;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.service.EventBus;
import ru.mydesignstudio.protege.plugin.search.ui.model.OWLUIClass;
import ru.mydesignstudio.protege.plugin.search.ui.model.OWLUIIndividual;
import ru.mydesignstudio.protege.plugin.search.ui.model.OWLUIProperty;
import ru.mydesignstudio.protege.plugin.search.api.query.FromType;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.ui.model.table.CriteriaTableModel;
import ru.mydesignstudio.protege.plugin.search.utils.Action;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Transformer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;

/**
 * Created by abarmin on 03.01.17.
 */
public class DefaultSearchStrategyParamsComponent extends JPanel implements SearchStrategyComponent {
    @Inject
    private OWLService owlService;
    @Inject
    private LogicalOperationHelper operationHelper;

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
        final JTable criteriaTable = new JTable(new CriteriaTableModel(selectQuery));
        //
        final JComboBox<OWLUIClass> classColumnSelector = new JComboBox<>();
        final JComboBox<OWLUIProperty> propertyColumnSelector = new JComboBox<>();
        final JComboBox<LogicalOperation> logicalOperationSelector = new JComboBox<>();
        CollectionUtils.forEach(owlService.getClasses(), new Action<OWLClass>() {
            @Override
            public void run(OWLClass owlClass) {
                classColumnSelector.addItem(new OWLUIClass(owlClass));
            }
        });
        classColumnSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    final OWLUIClass owlUiClass = (OWLUIClass) e.getItem();
                    propertyColumnSelector.removeAllItems();
                    CollectionUtils.forEach(owlService.getObjectProperties(owlUiClass.getOwlClass()), new Action<OWLObjectProperty>() {
                        @Override
                        public void run(OWLObjectProperty owlObjectProperty) {
                            propertyColumnSelector.addItem(new OWLUIProperty(owlObjectProperty));
                        }
                    });
                    CollectionUtils.forEach(owlService.getDataProperties(owlUiClass.getOwlClass()), new Action<OWLDataProperty>() {
                        @Override
                        public void run(OWLDataProperty owlDataProperty) {
                            propertyColumnSelector.addItem(new OWLUIProperty(owlDataProperty));
                        }
                    });
                }
            }
        });
        propertyColumnSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // доступные логические операции
                    logicalOperationSelector.removeAllItems();
                    final OWLUIProperty property = (OWLUIProperty) e.getItem();
                    final Collection<OWLPropertyRange> ranges = owlService.getPropertyRanges(property.getOwlProperty());
                    final Collection<LogicalOperation> logicalOperations = operationHelper.getAvailableOperations(ranges);
                    for (LogicalOperation logicalOperation : logicalOperations) {
                        logicalOperationSelector.addItem(logicalOperation);
                    }
                    // доступные для выбора варианты
                    if (operationHelper.hasClassExpression(ranges)) {
                        final JComboBox<OWLUIIndividual> individualsSelector = new JComboBox<>();
                        final OWLUIClass selectedUIClass = (OWLUIClass) classColumnSelector.getSelectedItem();
                        final Collection<OWLNamedIndividual> individuals = owlService.getIndividuals(selectedUIClass.getOwlClass());
                        for (OWLNamedIndividual individual : individuals) {
                            individualsSelector.addItem(new OWLUIIndividual(individual));
                        }
                        criteriaTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(individualsSelector));
                    } else {
                        final JTextField textField = new JTextField();
                        criteriaTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(textField));
                    }
                }
            }
        });
        //
        //
        final TableColumn classColumn = criteriaTable.getColumnModel().getColumn(0);
        classColumn.setCellEditor(new DefaultCellEditor(classColumnSelector));
        //
        final TableColumn propertyColumn = criteriaTable.getColumnModel().getColumn(1);
        propertyColumn.setCellEditor(new DefaultCellEditor(propertyColumnSelector));
        //
        final TableColumn logicalOperationColumn = criteriaTable.getColumnModel().getColumn(2);
        logicalOperationColumn.setCellEditor(new DefaultCellEditor(logicalOperationSelector));
        //
        final JScrollPane scrollPane = new JScrollPane(criteriaTable);
        criteriaTable.setFillsViewportHeight(true);
        //
        criteriaContainer.add(scrollPane, BorderLayout.CENTER);
    }

    private void fillTargetTypeSelector() {
        final JComboBox<OWLUIClass> typeSelector = new JComboBox<OWLUIClass>();
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
