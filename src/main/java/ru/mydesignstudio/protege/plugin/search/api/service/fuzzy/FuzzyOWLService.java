package ru.mydesignstudio.protege.plugin.search.api.service.fuzzy;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.function.FuzzyFunction;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.related.FuzzySimilarClass;

import java.util.Collection;

/**
 * Created by abarmin on 13.05.17.
 *
 * Сервис по работе с нечеткой онтологией
 */
public interface FuzzyOWLService extends OWLService {
    /**
     * Получить вес атрибута. Вес указывается в аннотации
     * @param property - атрибут
     * @return - вес атрибута
     * @throws ApplicationException
     */
    double getPropertyWeight(OWLProperty property) throws ApplicationException;
    /**
     * Классы, которые в аннотации указаны как сходные с определенной степенью
     * @param owlClass - класс, для которого ищем связанные
     * @return - коллекция схожих классов
     * @throws ApplicationException
     */
    Collection<FuzzySimilarClass> getFuzzySimilarClasses(OWLClass owlClass) throws ApplicationException;
    /**
     * Является ли указанный тип данных нечетким
     * @param datatype - этот тип проверяем
     * @return
     * @throws ApplicationException
     */
    boolean isFuzzyDatatype(OWLDatatype datatype) throws ApplicationException;

    /**
     * Получить функцию принадлежности для указанного нечеткого типа данных
     * @param datatype - тип данных
     * @return - функция принадлежности
     * @throws ApplicationException
     */
    FuzzyFunction getFuzzyFunction(OWLDatatype datatype) throws ApplicationException;

    /**
     * Тип свойства у конкретной записи
     * @param individual - запись
     * @param property - свойство
     * @return
     * @throws ApplicationException
     */
    OWLDatatype getPropertyDatatype(OWLIndividual individual, OWLProperty property) throws ApplicationException;

    /**
     * Does ontology support fuzzy properties
     * @param ontology - ontology to check
     * @return
     * @throws ApplicationException
     */
    boolean isFuzzyOntology(OWLOntology ontology) throws ApplicationException;
}
