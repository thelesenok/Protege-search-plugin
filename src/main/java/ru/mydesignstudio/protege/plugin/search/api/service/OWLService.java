package ru.mydesignstudio.protege.plugin.search.api.service;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.search.params.LookupParam;

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
    Collection<OWLClass> getClasses() throws ApplicationException;

    /**
     * Свойства указанного класса
     *
     * @param owlClass
     * @return
     */
    Collection<OWLObjectProperty> getObjectProperties(OWLClass owlClass) throws ApplicationException;

    /**
     * Свойтсва указанного класса
     *
     * @param owlClass
     * @return
     */
    Collection<OWLDataProperty> getDataProperties(OWLClass owlClass) throws ApplicationException;

    /**
     * Типы, поддерживаемые указанным свойством
     *
     * @param owlProperty
     * @return
     */
    Collection<OWLPropertyRange> getPropertyRanges(OWLProperty owlProperty) throws ApplicationException;

    /**
     * Экземпляры указанного класса
     *
     * @param owlClass
     * @return
     */
    Collection<OWLNamedIndividual> getIndividuals(OWLClass owlClass) throws ApplicationException;

    /**
     * Искать экземпляры по набору указанных параметров
     * @param params
     * @return
     */
    ResultSet search(Collection<LookupParam> params) throws ApplicationException;

    /**
     * Родительский класс для указанного
     * @param child
     * @return
     * @throws ApplicationException
     */
    OWLClass getParentClass(OWLClass child) throws ApplicationException;

    /**
     * Дочерние классы для указанного родительского
     * @param parent
     * @return
     * @throws ApplicationException
     */
    Collection<OWLClass> getChildrenClasses(OWLClass parent) throws ApplicationException;

    /**
     * Класс экземпляра
     * @param individual - экземпляр
     * @return - его класс
     * @throws ApplicationException
     */
    OWLClass getIndividualClass(OWLIndividual individual) throws ApplicationException;

    /**
     * Элемент по идентификатору
     * @param uri - длинный идентификатор объекта
     * @return
     * @throws ApplicationException
     */
    OWLIndividual getIndividual(IRI uri) throws ApplicationException;

    /**
     * Класс по идентификатору
     * @param iri - идентификатор объекта
     * @return - класс
     * @throws ApplicationException
     */
    OWLClass getOWLClass(IRI iri) throws ApplicationException;

    /**
     * Свойство по идентификатору
     * @param iri - идентификатор
     * @return - свойство
     * @throws ApplicationException
     */
    OWLProperty getProperty(IRI iri) throws ApplicationException;

    /**
     * Значение свойства
     * @param individual - объект, у которого пытаемся получить значение свойства
     * @param property - объект свойства
     * @return - значение свойства
     * @throws ApplicationException
     */
    Object getPropertyValue(OWLIndividual individual, OWLProperty property) throws ApplicationException;
}
