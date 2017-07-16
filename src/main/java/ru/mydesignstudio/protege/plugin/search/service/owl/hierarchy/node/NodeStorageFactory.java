package ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy.node;

import java.util.Collection;

import javax.inject.Inject;

import org.semanticweb.owlapi.model.OWLClass;

import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;

/**
 * Factory for building nodes storage hierarchy
 * @author abarmin
 */
@Component
public class NodeStorageFactory {
	private final OWLService owlService;
	
	@Inject
	public NodeStorageFactory(OWLService owlService) {
		this.owlService = owlService;
	}

	public NodeStorage build() throws ApplicationException {
		final NodeStorage storage = new NodeStorage();
		/** Adding all classes to storage */
		for (OWLClass parentClass : owlService.getClasses()) {
			final Collection<OWLClass> children = owlService.getChildrenClasses(parentClass);
			for (OWLClass childClass : children) {
				storage.addChildren(parentClass, childClass);
			}
		}
		return storage;
	}
}
