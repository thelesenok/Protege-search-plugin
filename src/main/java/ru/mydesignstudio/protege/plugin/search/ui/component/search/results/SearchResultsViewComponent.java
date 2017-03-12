package ru.mydesignstudio.protege.plugin.search.ui.component.search.results;

import com.google.common.eventbus.Subscribe;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.service.EventBus;
import ru.mydesignstudio.protege.plugin.search.ui.event.LookupInstancesEvent;
import ru.mydesignstudio.protege.plugin.search.ui.model.table.ResultSetModel;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrappedCallback;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;

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
    private final EventBus eventBus = EventBus.getInstance();
    @Inject
    private OWLService owlService;
    @Inject
    private ExceptionWrapperService wrapperService;

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
        wrapperService.invokeWrapped(this, new ExceptionWrappedCallback<Void>() {
            @Override
            public Void run() throws ApplicationException {
                final ResultSet resultSet = owlService.search(event.getLookupParams());
                createResultsTable(resultSet);
                return null;
            }
        });
    }

    private void createResultsTable(ResultSet resultSet) {
        final ResultSetModel resultSetModel = new ResultSetModel(resultSet);
        resultsTable.setModel(resultSetModel);
    }
}
