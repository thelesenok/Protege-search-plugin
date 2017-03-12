package ru.mydesignstudio.protege.plugin.search.ui.event;

import ru.mydesignstudio.protege.plugin.search.api.Event;
import ru.mydesignstudio.protege.plugin.search.api.search.params.LookupParam;

import java.util.Collection;

/**
 * Created by abarmin on 07.01.17.
 */
public class LookupInstancesEvent implements Event {
    private final Collection<LookupParam> lookupParams;

    public LookupInstancesEvent(Collection<LookupParam> lookupParams) {
        this.lookupParams = lookupParams;
    }

    public Collection<LookupParam> getLookupParams() {
        return lookupParams;
    }

}
