package ru.mydesignstudio.protege.plugin.search.api.service.fuzzy;

import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.function.FuzzyFunction;

/**
 * Created by abarmin on 13.05.17.
 *
 * Сервис по работе с нечеткой онтологией.
 * Не хочу засорять этим OWLService, потому вытащил сюда
 */
public interface FuzzyOWLService {
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
}
