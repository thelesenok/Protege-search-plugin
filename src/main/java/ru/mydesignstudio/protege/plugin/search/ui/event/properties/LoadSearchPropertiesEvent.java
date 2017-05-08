package ru.mydesignstudio.protege.plugin.search.ui.event.properties;

import ru.mydesignstudio.protege.plugin.search.api.Event;

import java.io.File;

/**
 * Created by abarmin on 08.05.17.
 *
 * Событие загрузки параметров поиск из файла
 */
public class LoadSearchPropertiesEvent implements Event {
    /**
     * Файл из которого загружаем параметры поиска
     */
    private final File targetFile;

    public LoadSearchPropertiesEvent(File targetFile) {
        this.targetFile = targetFile;
    }

    public File getTargetFile() {
        return targetFile;
    }
}
