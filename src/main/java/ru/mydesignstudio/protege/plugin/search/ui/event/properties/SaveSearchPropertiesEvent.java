package ru.mydesignstudio.protege.plugin.search.ui.event.properties;

import ru.mydesignstudio.protege.plugin.search.api.Event;
import ru.mydesignstudio.protege.plugin.search.api.search.params.LookupParam;

import java.io.File;
import java.util.Collection;

/**
 * Created by abarmin on 08.05.17.
 *
 * Событие сохранения параметров поиска
 */
public class SaveSearchPropertiesEvent implements Event {
    /**
     * В этот файл все сохраняем
     */
    private final File targetFile;
    /**
     * Параметры
     */
    private final Collection<LookupParam> lookupParams;

    public SaveSearchPropertiesEvent(File targetFile, Collection<LookupParam> lookupParams) {
        this.targetFile = targetFile;
        this.lookupParams = lookupParams;
    }

    public File getTargetFile() {
        return targetFile;
    }

    public Collection<LookupParam> getLookupParams() {
        return lookupParams;
    }
}
