package ru.mydesignstudio.protege.plugin.search.api.service;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import ru.mydesignstudio.protege.plugin.search.api.query.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;

import java.util.Collection;

/**
 * Created by abarmin on 03.01.17.
 */
public interface OWLService {
    /**
     * Зарегистрированные классы
     *
     * @return
     */
    Collection<OWLClass> getClasses();

    /**
     * Свойства указанного класса
     *
     * @param owlClass
     * @return
     */
    Collection<OWLObjectProperty> getObjectProperties(OWLClass owlClass);

    /**
     * Свойтсва указанного класса
     *
     * @param owlClass
     * @return
     */
    Collection<OWLDataProperty> getDataProperties(OWLClass owlClass);

    /**
     * Типы, поддерживаемые указанным свойством
     *
     * @param owlProperty
     * @return
     */
    Collection<OWLPropertyRange> getPropertyRanges(OWLProperty owlProperty);

    /**
     * Экземпляры указанного класса
     *
     * @param owlClass
     * @return
     */
    Collection<OWLNamedIndividual> getIndividuals(OWLClass owlClass);

    /**
     * Искать экземпляры по объекту запроса
     *
     * @param selectQuery
     * @return
     */
    ResultSet search(SelectQuery selectQuery);
}
