package ru.mydesignstudio.protege.plugin.search.api.service;

import org.semanticweb.owlapi.model.IRI;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.search.params.LookupParam;

import java.util.Collection;

/**
 * Created by abarmin on 26.06.17.
 *
 * Сервис по работе со SWRL
 */
public interface SwrlService {
    /**
     * Конвертировать параметры и результаты поиска в SWRL-правило
     * @param individualIri - полученный результат
     * @param lookupParams - параметры, по которым результат получен
     * @return - строка SWRL-запроса
     * @throws ApplicationException
     */
    String convertToSwrl(IRI individualIri, Collection<LookupParam> lookupParams) throws ApplicationException;
}
