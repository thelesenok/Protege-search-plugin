package ru.mydesignstudio.protege.plugin.search.api.service;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.search.params.LookupParam;

import java.io.File;
import java.util.Collection;

/**
 * Created by abarmin on 08.05.17.
 *
 * Сервис сериализации и десериализации параметров поиска
 */
public interface SearchStrategySerializationService {
    /**
     * Сохранить параметры в файл
     * @param targetFile - в этот файл сохраняем
     * @param properties - вот эти параметры
     * @throws ApplicationException
     */
    void save(File targetFile, Collection<LookupParam> properties) throws ApplicationException;

    /**
     * Загрузить параметры из файла
     * @param sourceFile - файл, из которого загружаем
     * @return - загруженные параметры
     * @throws ApplicationException
     */
    Collection<LookupParam> load(File sourceFile) throws ApplicationException;
}
