package ru.mydesignstudio.protege.plugin.search.ui.event.concat;

import ru.mydesignstudio.protege.plugin.search.api.Event;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;

/**
 * Created by abarmin on 29.05.17.
 *
 * Событие смены оператора объединения условий поиска
 */
public class ConcatOperationChangeEvent implements Event {
    private final LogicalOperation logicalOperation;

    public ConcatOperationChangeEvent(LogicalOperation logicalOperation) {
        this.logicalOperation = logicalOperation;
    }

    public LogicalOperation getLogicalOperation() {
        return logicalOperation;
    }
}
