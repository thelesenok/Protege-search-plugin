package ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.related;

import org.semanticweb.owlapi.model.OWLClass;
import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.common.Validation;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.related.RelatedClassFactory;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.xml.Concept;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.xml.FuzzyOWL2;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by abarmin on 25.06.17.
 */
@Component
public class RelatedClassFactoryImpl implements RelatedClassFactory {
    @Inject
    private OWLService owlService;

    @Override
    public Collection<FuzzySimilarClass> build(FuzzyOWL2 fuzzyData) throws ApplicationException {
        final Concept concept = fuzzyData.getConcept();
        Validation.assertNotNull("Fuzzy data doesn't contains information about related classes", concept);
        //
        final Collection<FuzzySimilarClass> similarClasses = new ArrayList<>();
        similarClasses.addAll(build(concept));
        return similarClasses;
    }

    private Collection<FuzzySimilarClass> build(Concept concept) throws ApplicationException {
        if (ConceptType.WEIGHTED.equals(concept.getType())) {
            final String baseClassString = concept.getBase();
            final Collection<OWLClass> classes = owlService.getClasses();
            final OWLClass owlClass = CollectionUtils.findFirst(classes, new Specification<OWLClass>() {
                @Override
                public boolean isSatisfied(OWLClass owlClass) {
                    return StringUtils.equalsIgnoreCase(
                            owlClass.getIRI().getFragment(),
                            baseClassString
                    );
                }
            });
            return Collections.singleton(new FuzzySimilarClass(owlClass, concept.getValue()));
        } else if (ConceptType.WEIGHTED_SUM.equals(concept.getType())) {
            final Collection<FuzzySimilarClass> classes = new ArrayList<>();
            for (Concept childConcept : concept.getChildren()) {
                classes.addAll(build(childConcept));
            }
            return classes;
        }
        throw new ApplicationException(String.format(
                "Unsupported concept type %s",
                concept.getType()
        ));
    }
}
