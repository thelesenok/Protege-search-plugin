package ru.mydesignstudio.protege.plugin.search.ui.component.search.results;

import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mydesignstudio.protege.plugin.search.api.common.FieldConstants;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationRuntimeException;
import ru.mydesignstudio.protege.plugin.search.api.query.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.service.EventBus;
import ru.mydesignstudio.protege.plugin.search.ui.component.search.results.action.ApplyUsagesAction;
import ru.mydesignstudio.protege.plugin.search.ui.component.search.results.cell.ResultCellEditor;
import ru.mydesignstudio.protege.plugin.search.ui.component.search.results.cell.ResultCellRenderer;
import ru.mydesignstudio.protege.plugin.search.ui.event.LookupInstancesEvent;
import ru.mydesignstudio.protege.plugin.search.ui.model.table.ResultSetModel;
import ru.mydesignstudio.protege.plugin.search.utils.Action;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;

/**
 * Created by abarmin on 07.01.17.
 */
public class SearchResultsViewComponent extends JPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchResultsViewComponent.class);
    private final EventBus eventBus = EventBus.getInstance();
    @Inject
    private OWLService owlService;

    private JTable resultsTable = new JTable();
    private JScrollPane scrollPane = new JScrollPane(resultsTable);

    public SearchResultsViewComponent() {
        setLayout(new BorderLayout());
        resultsTable.setFillsViewportHeight(true);
        add(scrollPane, BorderLayout.CENTER);
    }

    @PostConstruct
    public void init() {
        eventBus.register(this);
    }

    @Subscribe
    public void onInstanceLookupEventHandler(LookupInstancesEvent event) {
        try {
            final ResultSet resultSet = owlService.search(event.getLookupParams());
            createResultsTable(resultSet);
        } catch (ApplicationException e) {
            LOGGER.error("Can't execute lookup request", e);
            throw new ApplicationRuntimeException(e);
        }
    }

    private void createResultsTable(ResultSet resultSet) {
        final ResultSetModel resultSetModel = new ResultSetModel(resultSet);
        resultsTable.setModel(resultSetModel);
        //
        final Action<Integer> acceptAction = createAcceptAction(resultSet);
        final Action<Integer> declineAction = createDeclineAction(resultSet);
        //
        resultsTable.getColumnModel().getColumn(getAcceptButtonColumnIndex(resultSetModel)).setCellRenderer(new ResultCellRenderer("Accept"));
        resultsTable.getColumnModel().getColumn(getAcceptButtonColumnIndex(resultSetModel)).setCellEditor(new ResultCellEditor("Accept", acceptAction));
        resultsTable.getColumnModel().getColumn(getDeclineButtonColumnIndex(resultSetModel)).setCellRenderer(new ResultCellRenderer("Decline"));
        resultsTable.getColumnModel().getColumn(getDeclineButtonColumnIndex(resultSetModel)).setCellEditor(new ResultCellEditor("Decline", declineAction));
    }

    /**
     * Построить действие, которое увеличивает счетчик отказов записи
     * @param resultSet
     * @return
     */
    private Action<Integer> createDeclineAction(ResultSet resultSet) {
        return new ApplyUsagesAction(resultSet, FieldConstants.DECLINES_COUNT);
    }

    /**
     * Построить дейсвтие, которое увеличивает счетчик использований записи
     * @param resultSet
     * @return
     */
    private Action<Integer> createAcceptAction(ResultSet resultSet) {
        return new ApplyUsagesAction(resultSet, FieldConstants.USAGES_COUNT);
    }


    /**
     * Порядковый номер колонки для кнопки "Использовать"
     * @param resultSetModel
     * @return
     */
    private int getAcceptButtonColumnIndex(ResultSetModel resultSetModel) {
        return resultSetModel.getColumnCount() - 2;
    }

    /**
     * Порядковый номер колонки для кнопки "Отклонить"
     * @param resultSetModel
     * @return
     */
    private int getDeclineButtonColumnIndex(ResultSetModel resultSetModel) {
        return resultSetModel.getColumnCount() - 1;
    }
}
