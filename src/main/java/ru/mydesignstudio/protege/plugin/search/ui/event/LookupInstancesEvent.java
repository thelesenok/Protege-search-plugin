package ru.mydesignstudio.protege.plugin.search.ui.event;

import ru.mydesignstudio.protege.plugin.search.api.Event;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;

/**
 * Created by abarmin on 07.01.17.
 */
public class LookupInstancesEvent implements Event {
    private final SelectQuery selectQuery;

    public LookupInstancesEvent(SelectQuery selectQuery) {
        this.selectQuery = selectQuery;
    }

    public SelectQuery getSelectQuery() {
        return selectQuery;
    }
}
