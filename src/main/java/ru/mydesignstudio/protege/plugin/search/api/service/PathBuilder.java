package ru.mydesignstudio.protege.plugin.search.api.service;

import java.util.Collection;

import org.semanticweb.owlapi.model.OWLClass;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectField;

/**
 * Строит путь между двумя классами по их свойствам
 * @author Aleksandr_Barmin
 *
 */
public interface PathBuilder {
	/**
	 * Найти путь от класса sourceClass к классу destinationClass через их свойства
	 * @param sourceClass - от какого класса ищем путь
	 * @param destinationClass - к какому классу ищем путь
	 * @return - коллекция связей, которые неоходимо установить между классами чтобы дойти от source до destination
	 * @throws ApplicationException - если не удается найти путь между классами
	 */
	Collection<SelectField> buildPath(OWLClass sourceClass, OWLClass destinationClass) throws ApplicationException;
}
