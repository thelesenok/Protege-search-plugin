package ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy;

import org.semanticweb.owlapi.model.OWLClass;
import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.common.Validation;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy.path.PathBuildingStrategy;

import javax.inject.Inject;
import java.util.Collection;

@Component
public class OwlClassHierarchyBuilderImpl implements OwlClassHierarchyBuilder {
	private final OWLService owlService;
	private final PathBuildingStrategy buildingStrategy;
	
	@Inject
	public OwlClassHierarchyBuilderImpl(OWLService owlService, PathBuildingStrategy buildingStrategy) {
		this.owlService = owlService;
		this.buildingStrategy = buildingStrategy;
	}
	
	@Override
    public Collection<OWLClass> build(OWLClass currentClass) throws ApplicationException {
        Validation.assertNotNull("Source class is not defined", currentClass);
        //
        final OWLClass thingClass = owlService.getTopClass();
        Validation.assertNotNull("Can't get Thing class", thingClass);
        return buildingStrategy.build(currentClass, thingClass);
    }
}
