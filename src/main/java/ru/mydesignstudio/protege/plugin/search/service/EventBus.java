package ru.mydesignstudio.protege.plugin.search.service;

import ru.mydesignstudio.protege.plugin.search.api.Event;

/**
 * Created by abarmin on 07.01.17.
 */
public class EventBus {
    private static EventBus instance;
    private com.google.common.eventbus.EventBus delegate;

    public static final EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    protected EventBus() {
        delegate = new com.google.common.eventbus.EventBus();
    }

    public void publish(Event event) {
        delegate.post(event);
    }

    public void register(Object listener) {
        delegate.register(listener);
    }

    public void unregister(Object listener) {
        delegate.unregister(listener);
    }
}
