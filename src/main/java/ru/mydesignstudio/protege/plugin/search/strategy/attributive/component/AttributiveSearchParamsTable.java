package ru.mydesignstudio.protege.plugin.search.strategy.attributive.component;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationRuntimeException;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainClass;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainDataProperty;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainIndividual;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainLiteral;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainObjectProperty;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainProperty;
import ru.mydesignstudio.protege.plugin.search.service.EventBus;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrappedCallback;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.editor.ButtonCellEditor;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.editor.ButtonCellRenderer;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.event.ChangeClassEvent;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.event.ChangePropertyEvent;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.event.CleanConditionsEvent;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.event.RemoveRowEvent;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.model.CriteriaTableModel;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.renderer.CellRendererWithIcon;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.renderer.JComboboxIconRenderer;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.attributive.FuzzyAttributiveSearchStrategy;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology.FuzzyOntologySearchStrategy;
import ru.mydesignstudio.protege.plugin.search.strategy.relational.RelationalSearchStrategy;
import ru.mydesignstudio.protege.plugin.search.ui.event.StrategyChangeEvent;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.LogicalOperationHelper;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;

/**
 * Created by abarmin on 10.01.17.
 */
public class AttributiveSearchParamsTable extends JTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttributiveSearchParamsTable.class);

    private final SelectQuery selectQuery;
    private final OWLService owlService;
    private final ExceptionWrapperService wrapperService;

    private Map<Integer, DefaultCellEditor> classEditors = new HashMap<>();
    private Map<Integer, DefaultCellEditor> propertyEditors = new HashMap<>();
    private Map<Integer, DefaultCellEditor> operationEditors = new HashMap<>();
    private Map<Integer, DefaultCellEditor> valueEditors = new HashMap<>();

    private EventBus eventBus = EventBus.getInstance();
    private final CriteriaTableModel paramsTableModel;
    /**
     * Признак включенности поиска по связям
     */
    private boolean isRelationalLookupEnabled = false;
    /**
     * Признак включенности нечеткого поиска по атрибутам
     */
    private boolean isFuzzyAttributiveLookupEnabled = false;
    /**
     * Признак включенности нечеткого поиска по онтологии
     */
    private boolean isFuzzyOntologyLookupEnabled = false;

    public AttributiveSearchParamsTable(SelectQuery selectQuery, OWLService owlService, ExceptionWrapperService wrapperService) {
        eventBus.register(this);
        this.selectQuery = selectQuery;
        this.owlService = owlService;
        this.wrapperService = wrapperService;
        //
        paramsTableModel = new CriteriaTableModel(selectQuery);
        this.setModel(paramsTableModel);
        //
        getColumnModel().getColumn(0).setCellRenderer(createCellRendererWithIcons());
        getColumnModel().getColumn(1).setCellRenderer(createCellRendererWithIcons());
        getColumnModel().getColumn(3).setCellRenderer(createCellRendererWithIcons());
        getColumnModel().getColumn(4).setCellEditor(new ButtonCellEditor());
        getColumnModel().getColumn(4).setCellRenderer(new ButtonCellRenderer());
    }

    private TableCellRenderer createCellRendererWithIcons() {
        return new CellRendererWithIcon();
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        if (column == 0) {
            return getEditorForClassColumn(row);
        } else if (column == 1) {
            return getEditorForPropertyColumn(row);
        } else if (column == 2) {
            return getEditorForOperationColumn(row);
        } else if (column == 3) {
            return getEditorForValueColumn(row);
        }
        return super.getCellEditor(row, column);
    }

    private Collection<OWLClass> getAvailableClassesForLookup() {
        try {
            final Collection<OWLClass> classes = owlService.getClasses();
            if (isRelationalLookupEnabled) {
                return classes;
            }
            return CollectionUtils.filter(classes, new Specification<OWLClass>() {
                @Override
                public boolean isSatisfied(OWLClass owlClass) {
                    return owlClass == selectQuery.getFrom().getOwlClass();
                }
            });
        } catch (ApplicationException e) {
            throw new ApplicationRuntimeException(e);
        }
    }

    private TableCellEditor getEditorForClassColumn(int row) {
        if (!classEditors.containsKey(row)) {
            final JComboBox<OWLDomainClass> classColumnSelector = new JComboBox<>();
            classColumnSelector.setRenderer(createComboboxRenderer());
            final Collection<OWLClass> classes = getAvailableClassesForLookup();
            for (OWLClass owlClass : classes) {
                classColumnSelector.addItem(new OWLDomainClass(owlClass));
            }
            classColumnSelector.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (ItemEvent.SELECTED == e.getStateChange()) {
                        final OWLDomainClass owluiClass = (OWLDomainClass) e.getItem();
                        eventBus.publish(new ChangeClassEvent(
                                owluiClass.getOwlClass(),
                                getEditingRow()
                        ));
                    }
                }
            });
            final DefaultCellEditor cellEditor = new DefaultCellEditor(classColumnSelector);
            classEditors.put(row, cellEditor);
        }
        return classEditors.get(row);
    }

    private TableCellEditor getEditorForValueColumn(int row) {
        if (!valueEditors.containsKey(row)) {
            final JTextField valueEditor = new JTextField();
            valueEditors.put(row, new DefaultCellEditor(valueEditor));
        }
        return valueEditors.get(row);
    }

    private TableCellEditor getEditorForPropertyColumn(int row) {
        if (!propertyEditors.containsKey(row)) {
            final JComboBox<OWLDomainProperty> propertyEditor = new JComboBox<>();
            propertyEditor.setRenderer(createComboboxRenderer());
            propertyEditor.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (ItemEvent.SELECTED == e.getStateChange()) {
                        final JComboBox<OWLDomainClass> classSelector = (JComboBox<OWLDomainClass>) ((DefaultCellEditor) getCellEditor(row, 0)).getComponent();
                        final OWLDomainProperty uiProperty = (OWLDomainProperty) e.getItem();
                        final OWLDomainClass uiClass = (OWLDomainClass) classSelector.getSelectedItem();
                        eventBus.publish(new ChangePropertyEvent(uiClass.getOwlClass(), uiProperty.getOwlProperty(), row));
                    }
                }
            });
            propertyEditors.put(row, new DefaultCellEditor(propertyEditor));
        }
        return propertyEditors.get(row);
    }

    private TableCellEditor getEditorForOperationColumn(int row) {
        if (!operationEditors.containsKey(row)) {
            final JComboBox<LogicalOperation> logicalOperationSelector = new JComboBox<>();
            operationEditors.put(row, new DefaultCellEditor(logicalOperationSelector));
        }
        return operationEditors.get(row);
    }

    private JComboboxIconRenderer createComboboxRenderer() {
        return new JComboboxIconRenderer();
    }

    private Collection<OWLProperty> getAvailablePropertiesForLookup(OWLClass owlClass) {
        final Collection<OWLProperty> properties = new ArrayList<>();
        try {
            Collection<OWLDataProperty> dataProperties = owlService.getDataProperties(owlClass);
            /**
             * если не включен поиск по нечеткой онтологии,
             * то убираем нечеткие свойства
             */
            if (!isFuzzyOntologyLookupEnabled) {
                dataProperties = CollectionUtils.filter(dataProperties, new Specification<OWLDataProperty>() {
                    @Override
                    public boolean isSatisfied(OWLDataProperty property) {
                        final Collection<OWLPropertyRange> ranges = wrapperService.invokeWrapped(new ExceptionWrappedCallback<Collection<OWLPropertyRange>>() {
                            @Override
                            public Collection<OWLPropertyRange> run() throws ApplicationException {
                                return owlService.getPropertyRanges(property);
                            }
                        });
                        return !LogicalOperationHelper.hasFuzzyExpression(ranges);
                    }
                });
            }
            properties.addAll(dataProperties);
            if (isRelationalLookupEnabled) {
                properties.addAll(owlService.getObjectProperties(owlClass));
            }
        } catch (ApplicationException e) {
            throw new ApplicationRuntimeException(e);
        }
        return properties;
    }

    @Subscribe
    public void changeClassEventListener(ChangeClassEvent event) {
        try {
            final int editingRow = event.getEditingRow();
            final DefaultCellEditor cellEditor = (DefaultCellEditor) getCellEditor(editingRow, 1);
            final JComboBox<OWLDomainProperty> propertyEditor = (JComboBox<OWLDomainProperty>) cellEditor.getComponent();
            final OWLClass owlClass = event.getOwlClass();
            final Collection<OWLProperty> properties = getAvailablePropertiesForLookup(owlClass);
            propertyEditor.removeAllItems();
            propertyEditor.setSelectedItem(null);
            for (OWLProperty property : properties) {
                propertyEditor.addItem(createOWLUIProperty(property));
            }
        } catch (ApplicationException e) {
            LOGGER.warn("Can't process event {}", e);
        }
    }

    private OWLDomainProperty createOWLUIProperty(OWLProperty property) throws ApplicationException {
        if (property instanceof OWLObjectProperty) {
            return new OWLDomainObjectProperty(property);
        } else if (property instanceof OWLDataProperty) {
            return new OWLDomainDataProperty(property);
        }
        throw new ApplicationException(String.format(
                "Unknown property type %s",
                property.getClass()
        ));
    }

    @Subscribe
    public void changePropertyEventListener(ChangePropertyEvent event) {
        wrapperService.invokeWrapped(this, new ExceptionWrappedCallback<Void>() {
            @Override
            public Void run() throws ApplicationException {
                final int editingRow = event.getEditingRow();
                final DefaultCellEditor cellEditor = (DefaultCellEditor) getCellEditor(editingRow, 2);
                final JComboBox<LogicalOperation> operationEditor = (JComboBox<LogicalOperation>) cellEditor.getComponent();
                operationEditor.removeAllItems();
                operationEditor.setSelectedItem(null);
                //
                final Collection<OWLPropertyRange> ranges = owlService.getPropertyRanges(event.getProperty());
                final Collection<LogicalOperation> operations = getAvailableOperationsForLookup(event.getProperty());
                for (LogicalOperation operation : operations) {
                    operationEditor.addItem(operation);
                }
                //
                if (LogicalOperationHelper.hasClassExpression(ranges)) {
                    final JComboBox<OWLDomainIndividual> individualSelector = new JComboBox<>();
                    individualSelector.setRenderer(createComboboxRenderer());
                    valueEditors.put(editingRow, new DefaultCellEditor(individualSelector));
                    for (OWLPropertyRange range : ranges) {
                        for (OWLClass owlClass : range.getClassesInSignature()) {
                            final Collection<OWLNamedIndividual> individuals = owlService.getIndividuals(owlClass);
                            for (OWLNamedIndividual individual : individuals) {
                                individualSelector.addItem(new OWLDomainIndividual(individual));
                            }
                        }
                    }
                } else if (LogicalOperationHelper.hasBooleanExpression(ranges)) {
                		final JComboBox<Boolean> booleanSelector = new JComboBox<>();
                		booleanSelector.setRenderer(createComboboxRenderer());
                		valueEditors.put(editingRow, new DefaultCellEditor(booleanSelector));
                		booleanSelector.addItem(Boolean.TRUE);
                		booleanSelector.addItem(Boolean.FALSE);
                } else if (LogicalOperationHelper.hasEnumerationExpression(ranges)) {
                    final JComboBox<OWLDomainLiteral> literalSelector = new JComboBox<>();
                    literalSelector.setRenderer(createComboboxRenderer());
                    valueEditors.put(editingRow, new DefaultCellEditor(literalSelector));
                    for (OWLPropertyRange range : ranges) {
                        final OWLDataOneOf enumerationRange = (OWLDataOneOf) range;
                        for (OWLLiteral owlLiteral : enumerationRange.getValues()) {
                            literalSelector.addItem(new OWLDomainLiteral(owlLiteral));
                        }
                    }
                } else {
                    final JTextField textEditor = new JTextField();
                    valueEditors.put(editingRow, new DefaultCellEditor(textEditor));
                }
                return null;
            }
        });
    }

    @Subscribe
    public void onStrategyToggleEvent(StrategyChangeEvent event) {
        try {
            if (RelationalSearchStrategy.class.equals(event.getStrategy().getClass())) {
                isRelationalLookupEnabled = event.isSelected();
                // надо обновить все выбиралки классов, чтобы
                // там на выбор был только текущий класс
                updateClassEditors();
                // обновим выбиралки свойств - там должны остаться
                // только data properties
                updatePropertyEditors();
            } else if (FuzzyAttributiveSearchStrategy.class.equals(event.getStrategy().getClass())) {
                isFuzzyAttributiveLookupEnabled = event.isSelected();
                // обновим выбиралки логических операций
                // в строкове поля добавляем fuzzy
                updateLogicalOperationEditors();
            } else if (FuzzyOntologySearchStrategy.class.equals(event.getStrategy().getClass())) {
                isFuzzyOntologyLookupEnabled = event.isSelected();
                // обновим выбиралки свойств - показываем или
                // не показываем нечеткие свойства
                updatePropertyEditors();
            }
        } catch (ApplicationException e) {
            throw new ApplicationRuntimeException(e);
        }
    }

    private Collection<LogicalOperation> getAvailableOperationsForLookup(OWLProperty property) throws ApplicationException {
        final Collection<OWLPropertyRange> ranges = owlService.getPropertyRanges(property);
        final Collection<LogicalOperation> operations = LogicalOperationHelper.getAvailableOperations(ranges);
        if (isFuzzyAttributiveLookupEnabled) {
            if (LogicalOperationHelper.hasStringExpression(ranges)) {
                operations.add(LogicalOperation.FUZZY_LIKE);
            }
        }
        return operations;
    }

    private void updateLogicalOperationEditors() throws ApplicationException {
        for (Map.Entry<Integer, DefaultCellEditor> entry : operationEditors.entrySet()) {
            final TableCellEditor propertyEditor = getCellEditor(entry.getKey(), 1);
            final OWLDomainProperty selectedProperty = (OWLDomainProperty) propertyEditor.getCellEditorValue();
            if (selectedProperty == null) {
                continue;
            }
            final JComboBox<LogicalOperation> operationsEditor = (JComboBox<LogicalOperation>) entry.getValue().getComponent();
            operationsEditor.removeAllItems();
            //
            for (LogicalOperation logicalOperation : getAvailableOperationsForLookup(selectedProperty.getOwlProperty())) {
                operationsEditor.addItem(logicalOperation);
            }
        }
    }

    private void updatePropertyEditors() throws ApplicationException {
        for (Map.Entry<Integer, DefaultCellEditor> entry : propertyEditors.entrySet()) {
            final TableCellEditor classEditor = getCellEditor(entry.getKey(), 0);
            final OWLDomainClass uiClass = (OWLDomainClass) classEditor.getCellEditorValue();
            if (uiClass == null) {
                continue;
            }
            final OWLClass selectedClass = uiClass.getOwlClass();
            //
            final JComboBox<OWLDomainProperty> component = (JComboBox<OWLDomainProperty>) entry.getValue().getComponent();
            component.removeAllItems();
            //
            for (OWLProperty property : getAvailablePropertiesForLookup(selectedClass)) {
                component.addItem(createOWLUIProperty(property));
            }
        }
    }

    private void updateClassEditors() throws ApplicationException {
        for (DefaultCellEditor editor : classEditors.values()) {
            final JComboBox<OWLDomainClass> component = (JComboBox<OWLDomainClass>) editor.getComponent();
            component.removeAllItems();
            for (OWLClass owlClass : getAvailableClassesForLookup()) {
                component.addItem(new OWLDomainClass(owlClass));
            }
        }
    }

    @Subscribe
    public void onRemoveRowEvent(RemoveRowEvent event) {
        selectQuery.removeWherePart(event.getCurrentRow());
    }
    
    @Subscribe
    public void onCleanConditionsEvent(CleanConditionsEvent event) {
    		/** We need just update view */
    		this.paramsTableModel.fireTableDataChanged();
    }
}
