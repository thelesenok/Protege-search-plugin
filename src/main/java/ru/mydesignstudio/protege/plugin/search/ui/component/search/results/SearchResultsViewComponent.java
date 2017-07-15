package ru.mydesignstudio.protege.plugin.search.ui.component.search.results;

import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mydesignstudio.protege.plugin.search.api.common.FieldConstants;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationRuntimeException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.search.params.LookupParam;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.service.EventBus;
import ru.mydesignstudio.protege.plugin.search.ui.component.search.results.action.ApplyUsagesAction;
import ru.mydesignstudio.protege.plugin.search.ui.component.search.results.action.ConvertToSwrlAction;
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
import java.util.Collection;

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
            createResultsTable(resultSet, event.getLookupParams());
        } catch (ApplicationException e) {
            LOGGER.error("Can't execute lookup request", e);
            throw new ApplicationRuntimeException(e);
        }
    }

    private void createResultsTable(ResultSet resultSet, Collection<LookupParam> lookupParams) {
        final ResultSetModel resultSetModel = new ResultSetModel(resultSet);
        resultsTable.setModel(resultSetModel);
        //
        final Action<Integer> acceptAction = createAcceptAction(resultSet);
        final Action<Integer> declineAction = createDeclineAction(resultSet);
        final Action<Integer> convertToSwrlAction = createConvertToSwrlAction(resultSet, lookupParams);
        //
        resultsTable.getColumnModel().getColumn(getAcceptButtonColumnIndex(resultSetModel)).setCellRenderer(new ResultCellRenderer("Accept"));
        resultsTable.getColumnModel().getColumn(getAcceptButtonColumnIndex(resultSetModel)).setCellEditor(new ResultCellEditor("Accept", acceptAction));
        resultsTable.getColumnModel().getColumn(getDeclineButtonColumnIndex(resultSetModel)).setCellRenderer(new ResultCellRenderer("Decline"));
        resultsTable.getColumnModel().getColumn(getDeclineButtonColumnIndex(resultSetModel)).setCellEditor(new ResultCellEditor("Decline", declineAction));
        resultsTable.getColumnModel().getColumn(getConvertToSwrlButtonColumnIndex(resultSetModel)).setCellRenderer(new ResultCellRenderer("To SWRL"));
        resultsTable.getColumnModel().getColumn(getConvertToSwrlButtonColumnIndex(resultSetModel)).setCellEditor(new ResultCellEditor("To SWRL", convertToSwrlAction));
    }

    /**
     * Построить действие, которое конвертирует текущий запрос и выбранный результат в SWRL-правило
     * @param resultSet - модель данных результатов
     * @param lookupParams - все параметры, по которым выполняется поиск
     * @return - действие для {@link ResultCellEditor}
     */
    private Action<Integer> createConvertToSwrlAction(ResultSet resultSet, Collection<LookupParam> lookupParams) {
        return new ConvertToSwrlAction(resultSet, lookupParams);
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
     * Порядковый номер колонки для кнопки "To SWRL"
     * @param resultSetModel - модель данный для компонента
     * @return - порядковый номер
     * @deprecated - ненадежный способ, придумать получше
     */
    @Deprecated
    private int getConvertToSwrlButtonColumnIndex(ResultSetModel resultSetModel) {
        return resultSetModel.getColumnCount() - 1;
    }

    /**
     * Порядковый номер колонки для кнопки "Использовать"
     * @param resultSetModel
     * @return
     * @deprecated - ненадежный способ, придумать получше
     */
    @Deprecated
    private int getAcceptButtonColumnIndex(ResultSetModel resultSetModel) {
        return resultSetModel.getColumnCount() - 3;
    }

    /**
     * Порядковый номер колонки для кнопки "Отклонить"
     * @param resultSetModel
     * @return
     * @deprecated - ненадежный способ, придумать получше
     */
    @Deprecated
    private int getDeclineButtonColumnIndex(ResultSetModel resultSetModel) {
        return resultSetModel.getColumnCount() - 2;
    }
}
