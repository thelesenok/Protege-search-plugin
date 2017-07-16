package ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy;

import java.util.Collection;

import org.semanticweb.owlapi.model.OWLClass;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;

/**
 * Created by abarmin on 25.06.17.
 * Строит иерархию от указанного класса до вершины иерархии
 */
public interface OwlClassHierarchyBuilder {
	/**
     * Построить иерархию от указанного класса до Thing
     * @param currentClass - от какого класса начинаем строить иерархию
     * @return - коллекция
     * @throws ApplicationException
     */
    Collection<OWLClass> build(OWLClass currentClass) throws ApplicationException;
}
