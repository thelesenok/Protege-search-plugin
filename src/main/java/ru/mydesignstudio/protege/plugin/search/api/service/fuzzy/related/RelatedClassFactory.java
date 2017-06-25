package ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.related;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.related.FuzzySimilarClass;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.xml.FuzzyOWL2;

import java.util.Collection;

/**
 * Created by abarmin on 25.06.17.
 *
 * Фабрика для построения информации о сходных классах на основе информации из аннотаций fuzzyLabel
 */
public interface RelatedClassFactory {
    /**
     * На основе результатов парсинга аннотации сформировать коллекцию сходных классов с информацией о схожести
     * @param fuzzyData - результаты парсинга аннотации
     * @return - коллекция с информацией о схожих классах
     * @throws ApplicationException
     */
    Collection<FuzzySimilarClass> build(FuzzyOWL2 fuzzyData) throws ApplicationException;
}
