package ru.mydesignstudio.protege.plugin.search.strategy.attributive.component;

import com.google.common.eventbus.Subscribe;
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
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationRuntimeException;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.service.EventBus;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.editor.ButtonCellEditor;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.editor.ButtonCellRenderer;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.event.ChangeClassEvent;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.event.ChangePropertyEvent;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.event.RemoveRowEvent;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.model.CriteriaTableModel;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.renderer.CellRendererWithIcon;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.renderer.JComboboxIconRenderer;
import ru.mydesignstudio.protege.plugin.search.ui.model.OWLUIClass;
import ru.mydesignstudio.protege.plugin.search.ui.model.OWLUIDataProperty;
import ru.mydesignstudio.protege.plugin.search.ui.model.OWLUIIndividual;
import ru.mydesignstudio.protege.plugin.search.ui.model.OWLUILiteral;
import ru.mydesignstudio.protege.plugin.search.ui.model.OWLUIObjectProperty;
import ru.mydesignstudio.protege.plugin.search.ui.model.OWLUIProperty;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrappedCallback;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;
import ru.mydesignstudio.protege.plugin.search.utils.LogicalOperationHelper;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by abarmin on 10.01.17.
 */
public class AttributiveSearchParamsTable extends JTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttributiveSearchParamsTable.class);

    private final SelectQuery selectQuery;
    private final OWLService owlService;
    private final ExceptionWrapperService wrapperService;

    private Map<Integer, DefaultCellEditor> propertyEditors = new HashMap<>();
    private Map<Integer, DefaultCellEditor> operationEditors = new HashMap<>();
    private Map<Integer, DefaultCellEditor> valueEditors = new HashMap<>();

    private EventBus eventBus = EventBus.getInstance();

    public AttributiveSearchParamsTable(SelectQuery selectQuery, OWLService owlService, ExceptionWrapperService wrapperService) {
        super(new CriteriaTableModel(selectQuery));
        //
        eventBus.register(this);
        this.selectQuery = selectQuery;
        this.owlService = owlService;
        this.wrapperService = wrapperService;
        //
        try {
            getColumnModel().getColumn(0).setCellEditor(createEditorForClassColumn());
            getColumnModel().getColumn(0).setCellRenderer(createCellRendererWithIcons());
            getColumnModel().getColumn(1).setCellRenderer(createCellRendererWithIcons());
            getColumnModel().getColumn(3).setCellRenderer(createCellRendererWithIcons());
            getColumnModel().getColumn(4).setCellEditor(new ButtonCellEditor());
            getColumnModel().getColumn(4).setCellRenderer(new ButtonCellRenderer());
        } catch (ApplicationException e) {
            LOGGER.warn("Can't initialize attributes table {}", e);
            throw new ApplicationRuntimeException(e);
        }
    }

    private TableCellRenderer createCellRendererWithIcons() {
        return new CellRendererWithIcon();
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        if (column == 1) {
            return getEditorForPropertyColumn(row);
        } else if (column == 2) {
            return getEditorForOperationColumn(row);
        } else if (column == 3) {
            return getEditorForValueColumn(row);
        }
        return super.getCellEditor(row, column);
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
            final JComboBox<OWLUIProperty> propertyEditor = new JComboBox<>();
            propertyEditor.setRenderer(createComboboxRenderer());
            propertyEditor.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (ItemEvent.SELECTED == e.getStateChange()) {
                        final JComboBox<OWLUIClass> classSelector = (JComboBox<OWLUIClass>) ((DefaultCellEditor) getCellEditor(row, 0)).getComponent();
                        final OWLUIProperty uiProperty = (OWLUIProperty) e.getItem();
                        final OWLUIClass uiClass = (OWLUIClass) classSelector.getSelectedItem();
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

    private DefaultCellEditor createEditorForClassColumn() throws ApplicationException {
        final JComboBox<OWLUIClass> classColumnSelector = new JComboBox<>();
        classColumnSelector.setRenderer(createComboboxRenderer());
        final Collection<OWLClass> classes = owlService.getClasses();
        for (OWLClass owlClass : classes) {
            classColumnSelector.addItem(new OWLUIClass(owlClass));
        }
        classColumnSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (ItemEvent.SELECTED == e.getStateChange()) {
                    final OWLUIClass owluiClass = (OWLUIClass) e.getItem();
                    eventBus.publish(new ChangeClassEvent(
                            owluiClass.getOwlClass(),
                            getEditingRow()
                    ));
                }
            }
        });
        return new DefaultCellEditor(classColumnSelector);
    }

    @Subscribe
    public void changeClassEventListener(ChangeClassEvent event) {
        try {
            final int editingRow = event.getEditingRow();
            final DefaultCellEditor cellEditor = (DefaultCellEditor) getCellEditor(editingRow, 1);
            final JComboBox<OWLUIProperty> propertyEditor = (JComboBox<OWLUIProperty>) cellEditor.getComponent();
            final OWLClass owlClass = event.getOwlClass();
            final Collection<OWLObjectProperty> objectProperties = owlService.getObjectProperties(owlClass);
            propertyEditor.removeAllItems();
            propertyEditor.setSelectedItem(null);
            for (OWLObjectProperty property : objectProperties) {
                propertyEditor.addItem(createOWLUIProperty(property));
            }
            final Collection<OWLDataProperty> dataProperties = owlService.getDataProperties(owlClass);
            for (OWLDataProperty property : dataProperties) {
                propertyEditor.addItem(createOWLUIProperty(property));
            }
        } catch (ApplicationException e) {
            LOGGER.warn("Can't process event {}", e);
        }
    }

    private OWLUIProperty createOWLUIProperty(OWLProperty property) throws ApplicationException {
        if (property instanceof OWLObjectProperty) {
            return new OWLUIObjectProperty(property);
        } else if (property instanceof OWLDataProperty) {
            return new OWLUIDataProperty(property);
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
                final Collection<LogicalOperation> operations = LogicalOperationHelper.getAvailableOperations(ranges);
                for (LogicalOperation operation : operations) {
                    operationEditor.addItem(operation);
                }
                //
                if (LogicalOperationHelper.hasClassExpression(ranges)) {
                    final JComboBox<OWLUIIndividual> individualSelector = new JComboBox<>();
                    individualSelector.setRenderer(createComboboxRenderer());
                    valueEditors.put(editingRow, new DefaultCellEditor(individualSelector));
                    for (OWLPropertyRange range : ranges) {
                        for (OWLClass owlClass : range.getClassesInSignature()) {
                            final Collection<OWLNamedIndividual> individuals = owlService.getIndividuals(owlClass);
                            for (OWLNamedIndividual individual : individuals) {
                                individualSelector.addItem(new OWLUIIndividual(individual));
                            }
                        }
                    }
                } else if (LogicalOperationHelper.hasEnumerationExpression(ranges)) {
                    final JComboBox<OWLUILiteral> literalSelector = new JComboBox<>();
                    literalSelector.setRenderer(createComboboxRenderer());
                    valueEditors.put(editingRow, new DefaultCellEditor(literalSelector));
                    for (OWLPropertyRange range : ranges) {
                        final OWLDataOneOf enumerationRange = (OWLDataOneOf) range;
                        for (OWLLiteral owlLiteral : enumerationRange.getValues()) {
                            literalSelector.addItem(new OWLUILiteral(owlLiteral));
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
    public void removeRowEvent(RemoveRowEvent event) {
        selectQuery.removeWherePart(event.getCurrentRow());
    }
}
