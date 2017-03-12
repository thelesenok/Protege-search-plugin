package ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.event;

import ru.mydesignstudio.protege.plugin.search.api.Event;

/**
 * Created by abarmin on 15.01.17.
 */
public class RemoveRowEvent implements Event {
    private final int currentRow;

    public RemoveRowEvent(int currentRow) {
        this.currentRow = currentRow;
    }

    public int getCurrentRow() {
        return currentRow;
    }
}
