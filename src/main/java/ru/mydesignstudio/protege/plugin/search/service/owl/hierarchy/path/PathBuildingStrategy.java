package ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy.path;

import java.util.Collection;

import org.semanticweb.owlapi.model.OWLClass;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;

/**
 * Strategy for building paths between designated classes
 * @author abarmin
 */
public interface PathBuildingStrategy {
	/**
	 * Build path from source to destination classes
	 * @param sourceClass class to start from
	 * @param destinationClass class to search for
	 * @return collection of nodes between source and destination
	 * @throws ApplicationException if there is no way from source to destination
	 */
	Collection<OWLClass> build(OWLClass sourceClass, OWLClass destinationClass) throws ApplicationException;
}
