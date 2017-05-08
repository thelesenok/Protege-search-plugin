package ru.mydesignstudio.protege.plugin.search.ui.event.properties;

import ru.mydesignstudio.protege.plugin.search.api.Event;
import ru.mydesignstudio.protege.plugin.search.api.search.params.LookupParam;

import java.util.Collection;

/**
 * Created by abarmin on 08.05.17.
 *
 * Событие "Параметры поиска загружены"
 */
public class LoadedSearchPropertiesEvent implements Event {
    /**
     * Параметры поиска по стратегиям
     */
    private final Collection<LookupParam> lookupParams;

    public LoadedSearchPropertiesEvent(Collection<LookupParam> lookupParams) {
        this.lookupParams = lookupParams;
    }

    public Collection<LookupParam> getLookupParams() {
        return lookupParams;
    }
}
