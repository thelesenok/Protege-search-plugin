package ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.FuzzyOWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.function.FuzzyFunction;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.related.RelatedClassFactory;
import ru.mydesignstudio.protege.plugin.search.service.owl.OWLServiceImpl;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.function.FuzzyFunctionFactory;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.property.PropertyWeightFactory;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.property.binding.DataWeightFactory;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.property.binding.ObjectWeightFactory;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.related.FuzzySimilarClass;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.xml.Concept;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.xml.Datatype;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.xml.FuzzyOWL2;
import ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy.OwlClassHierarchyBuilder;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.OWLUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Created by abarmin on 13.05.17.
 */
@Component
public class FuzzyOWLServiceImpl extends OWLServiceImpl implements FuzzyOWLService {
    private static final int DEFAULT_PROPERTY_WEIGHT = 1;

    private final FuzzyFunctionFactory functionFactory;
    private final RelatedClassFactory relatedClassFactory;
    private final PropertyWeightFactory dataPropertyWeightFactory;
    private final PropertyWeightFactory objectPropertyWeightFactory;

    @Inject
    public FuzzyOWLServiceImpl(OwlClassHierarchyBuilder hierarchyBuilder,
                               FuzzyFunctionFactory functionFactory,
                               RelatedClassFactory relatedClassFactory,
                               @DataWeightFactory PropertyWeightFactory dataPropertyWeightFactory,
                               @ObjectWeightFactory PropertyWeightFactory objectPropertyWeightFactory) {
		super(hierarchyBuilder);
		this.functionFactory = functionFactory;
		this.relatedClassFactory = relatedClassFactory;
		this.dataPropertyWeightFactory = dataPropertyWeightFactory;
		this.objectPropertyWeightFactory = objectPropertyWeightFactory;
	}

	private Collection<OWLAnnotationAssertionAxiom> getAnnotations(OWLEntity owlEntity) throws ApplicationException {
        final Set<OWLAnnotationAssertionAxiom> axioms = getOntology().getAxioms(AxiomType.ANNOTATION_ASSERTION);
        return CollectionUtils.filter(axioms, new Specification<OWLAnnotationAssertionAxiom>() {
            @Override
            public boolean isSatisfied(OWLAnnotationAssertionAxiom axiom) {
                final IRI subject = (IRI) axiom.getSubject();
                return OWLUtils.equals(subject, owlEntity.getIRI());
            }
        });
    }

    /**
     * Get property weight from annotation
     * @param propertyAxiom - annotation to extract weight
     * @param factory - factory that convert xml and extract weight
     * @return - weight, if annotation object is not present, default weight will be returned
     * @throws ApplicationException
     */
    private double getPropertyWeight(OWLAnnotationAssertionAxiom propertyAxiom, PropertyWeightFactory factory) throws ApplicationException {
        if (propertyAxiom == null) {
            /**
             * Нет информации о весе свойства, используем вес по умолчанию
             */
            return DEFAULT_PROPERTY_WEIGHT;
        }
        /**
         * Получим xml и пропустим его через фабрику
         */
        final String xmlString = ((OWLLiteral) propertyAxiom.getValue()).getLiteral();
        return dataPropertyWeightFactory.build(xmlString);
    }

    /**
     * Get weight of data property
     * @param property - property to check
     * @param factory - factory that convert xml and extract weight
     * @return weight, if there is no weight annotation, default weight will be returned
     * @throws ApplicationException
     */
    private double getDataPropertyWeight(OWLDataProperty property, PropertyWeightFactory factory) throws ApplicationException {
        final Collection<OWLAnnotationAssertionAxiom> annotations = getAnnotations(property);
        final OWLAnnotationAssertionAxiom propertyAxiom = getFuzzyAxiom(annotations, FuzzyAnnotationType.PROPERTY);
        return getPropertyWeight(propertyAxiom, factory);
    }

    /**
     * Get weight of object property
     * @param property - property to check
     * @param factory - factory that convert xml and extract weight
     * @return - weight, if there is no weight annotation, default weight will be returned
     * @throws ApplicationException
     */
    private double getObjectPropertyWeight(OWLObjectProperty property, PropertyWeightFactory factory) throws ApplicationException {
        final Collection<OWLAnnotationAssertionAxiom> annotations = getAnnotations(property);
        final OWLAnnotationAssertionAxiom propertyAxiom = getFuzzyAxiom(annotations, FuzzyAnnotationType.CLASS_OR_DATATYPE);
        return getPropertyWeight(propertyAxiom, factory);
    }

	@Override
    public double getPropertyWeight(OWLProperty property) throws ApplicationException {
        if (property instanceof OWLDataProperty) {
            return getDataPropertyWeight((OWLDataProperty) property, dataPropertyWeightFactory);
        }
        return getObjectPropertyWeight((OWLObjectProperty) property, objectPropertyWeightFactory);
    }

    private OWLAnnotationAssertionAxiom getFuzzyAxiom(Collection<OWLAnnotationAssertionAxiom> axioms, FuzzyAnnotationType annotationType) throws ApplicationException {
        return CollectionUtils.findFirst(axioms, new Specification<OWLAnnotationAssertionAxiom>() {
            @Override
            public boolean isSatisfied(OWLAnnotationAssertionAxiom axiom) {
                return  StringUtils.equalsIgnoreCase(axiom.getProperty().getIRI().getFragment(), annotationType.getType());
            }
        });
    }

    @Override
    public Collection<FuzzySimilarClass> getFuzzySimilarClasses(OWLClass owlClass) throws ApplicationException {
        final Collection<OWLAnnotationAssertionAxiom> annotations = getAnnotations(owlClass);
        final OWLAnnotationAssertionAxiom fuzzyAxiom = getFuzzyAxiom(annotations, FuzzyAnnotationType.CLASS_OR_DATATYPE);
        if (fuzzyAxiom == null) {
            return Collections.emptyList();
        }
        final String xmlString = ((OWLLiteral) fuzzyAxiom.getValue()).getLiteral();
        return parseSimilarClasses(xmlString);
    }

    public Collection<FuzzySimilarClass> parseSimilarClasses(String xmlString) throws ApplicationException {
        try {
            final JAXBContext context = JAXBContext.newInstance(FuzzyOWL2.class, Concept.class);
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            final FuzzyOWL2 fuzzyData = (FuzzyOWL2) unmarshaller.unmarshal(new StringReader(xmlString));
            return relatedClassFactory.build(fuzzyData);
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public boolean isFuzzyDatatype(OWLDatatype datatype) throws ApplicationException {
        final Collection<OWLAnnotationAssertionAxiom> annotationAxioms = getAnnotations(datatype);
        if (CollectionUtils.isEmpty(annotationAxioms)) {
            return false;
        }
        return getFuzzyAxiom(annotationAxioms, FuzzyAnnotationType.CLASS_OR_DATATYPE) != null;
    }

    @Override
    public FuzzyFunction getFuzzyFunction(OWLDatatype datatype) throws ApplicationException {
        final OWLAnnotationAssertionAxiom axiom = getFuzzyAxiom(getAnnotations(datatype), FuzzyAnnotationType.CLASS_OR_DATATYPE);
        if (axiom == null) {
            throw new ApplicationException(String.format(
                    "Datatype %s is not fuzzy",
                    datatype
            ));
        }
        final String xmlString = ((OWLLiteral) axiom.getValue()).getLiteral();
        return parseFunction(xmlString);
    }

    public FuzzyFunction parseFunction(String xmlString) throws ApplicationException {
        try {
            final JAXBContext context = JAXBContext.newInstance(Datatype.class, FuzzyOWL2.class);
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            final FuzzyOWL2 target = (FuzzyOWL2) unmarshaller.unmarshal(new StringReader(xmlString));
            return functionFactory.createFunction(target);
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public OWLDatatype getPropertyDatatype(OWLIndividual individual, OWLProperty property) throws ApplicationException {
        final Set<OWLDataPropertyAssertionAxiom> propertyAssertionAxioms = getOntology().getAxioms(AxiomType.DATA_PROPERTY_ASSERTION);
        /**
         * Отберем аксиомы, относящиеся к переданному объекту и нужному свойству
         */
        final OWLDataPropertyAssertionAxiom propertyAxiom = CollectionUtils.findFirst(propertyAssertionAxioms, new Specification<OWLDataPropertyAssertionAxiom>() {
            @Override
            public boolean isSatisfied(OWLDataPropertyAssertionAxiom axiom) {
                return OWLUtils.equals(axiom.getSubject(), individual)
                        && OWLUtils.equals((OWLProperty) axiom.getProperty(), property);
            }
        });
        if (propertyAxiom == null) {
            return null;
        }
        return propertyAxiom.getObject().getDatatype();
    }

    @Override
    public boolean isFuzzyOntology(OWLOntology ontology) throws ApplicationException {
        final Set<OWLAnnotation> annotations = ontology.getAnnotations();
        final OWLAnnotation labelAnnotation = CollectionUtils.findFirst(annotations, new Specification<OWLAnnotation>() {
            @Override
            public boolean isSatisfied(OWLAnnotation owlAnnotation) {
                return StringUtils.equalsIgnoreCase(
                        owlAnnotation.getProperty().getIRI().getFragment(),
                        "fuzzyLabel"
                );
            }
        });
        return labelAnnotation != null;
    }
}
