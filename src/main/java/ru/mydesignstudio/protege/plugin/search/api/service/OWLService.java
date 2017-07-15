package ru.mydesignstudio.protege.plugin.search.api.service;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.search.params.LookupParam;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Created by abarmin on 03.01.17.
 */
public interface OWLService {
    /**
     * Онтология, с которой в данной момент работаем
     * @return - объект онтологии
     * @throws ApplicationException
     * @deprecated - онтологий может быть больше одной одновременно
     */
    @Deprecated
    OWLOntology getOntology() throws ApplicationException;

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
     * Все классы, к которым принадлежит экземпляр
     * @param individual - экземпляр
     * @return - классы экземпляра
     * @throws ApplicationException
     */
    Collection<OWLClass> getIndividualClasses(OWLIndividual individual) throws ApplicationException;

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

    /**
     * Значение свойства
     * @param individual - объект, у которого пытаемся получить значение свойства
     * @param propertyName - название свойства
     * @return - значение свойства
     * @throws ApplicationException
     */
    Object getPropertyValue(OWLIndividual individual, String propertyName) throws ApplicationException;

    /**
     * Установить значение свойства
     * @param individual - объект, у которого устанавливаем свойство
     * @param property - объект свойства
     * @param value - новое значение
     * @throws ApplicationException
     */
    void setPropertyValue(OWLIndividual individual, OWLProperty property, Object value) throws ApplicationException;

    /**
     * Создать свойство и привязать его к указанному классу
     * @param targetClasses - к каким классам привязываем свойство
     * @param propertyName - название свойства
     * @param propertyType - тип данных свойства
     * @return - созданное свойство
     * @throws ApplicationException
     */
    OWLProperty createDataProperty(Collection<OWLClass> targetClasses, String propertyName, Type propertyType) throws ApplicationException;

    /**
     * Элемент перечисления
     * @param value - значение перечисления
     * @return
     * @throws ApplicationException
     */
    OWLLiteral getLiteral(String value) throws ApplicationException;

    /**
     * Сохранить текущие изменения в онтологии
     * @throws ApplicationException
     */
    void saveOntology() throws ApplicationException;

    /**
     * Классы, которые указаны эквивалентными у переданного
     * @param owlClass - от этого класса вычисляем
     * @return - коллекция эквивалентных классов
     * @throws ApplicationException
     */
    Collection<OWLClass> getEqualClasses(OWLClass owlClass) throws ApplicationException;
}
