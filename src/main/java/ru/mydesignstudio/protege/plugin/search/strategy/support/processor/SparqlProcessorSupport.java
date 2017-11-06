package ru.mydesignstudio.protege.plugin.search.strategy.support.processor;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mydesignstudio.protege.plugin.search.api.common.Validation;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSetRow;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.WeighedResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.config.OntologyConfig;
import ru.mydesignstudio.protege.plugin.search.reasoner.sparql.BasicSparqlReasonerFactory;
import ru.mydesignstudio.protege.plugin.search.reasoner.sparql.SparqlInferenceFactory;
import ru.mydesignstudio.protege.plugin.search.reasoner.sparql.SparqlReasoner;
import ru.mydesignstudio.protege.plugin.search.reasoner.sparql.SparqlReasonerException;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrappedCallback;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query.SparqlQueryConverter;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.OWLUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by abarmin on 05.05.17.
 *
 * Абстрактный процессор с поддержкой поиска по sparql запросу
 */
public abstract class SparqlProcessorSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(SparqlProcessorSupport.class);

    private final OWLService owlService;
    private final ExceptionWrapperService wrapperService;
    private final SparqlQueryConverter queryConverter;
    private SparqlReasoner reasonerHolder;

    @Inject
    public SparqlProcessorSupport(OWLService owlService,
                                  ExceptionWrapperService wrapperService,
                                  SparqlQueryConverter queryConverter) {
        this.owlService = owlService;
        this.wrapperService = wrapperService;
        this.queryConverter = queryConverter;
    }

    public SparqlReasoner getReasoner() throws ApplicationException {
        if (reasonerHolder == null) {
            final SparqlInferenceFactory factory = new BasicSparqlReasonerFactory();
            reasonerHolder = factory.createReasoner(OntologyConfig.getOntologyManager());
            try {
                reasonerHolder.precalculate();
            } catch (SparqlReasonerException e) {
                throw new ApplicationException(e);
            }
        }
        return reasonerHolder;
    }

    /**
     * Выполнить поиск по запросу
     * @param selectQuery - по какому запросу ищем
     * @return - коллекция с результатами
     * @throws ApplicationException
     */
    public ResultSet collect(SelectQuery selectQuery) throws ApplicationException {
        Validation.assertNotNull("Select Query was not provided", selectQuery);

        final String sparqlQuery = queryConverter.convert(selectQuery);
        try {
            final SparqlReasoner reasoner = getReasoner();
            return reasoner.executeQuery(sparqlQuery);
        } catch (SparqlReasonerException e) {
            LOGGER.error("Invalid SPARQL Query {}", sparqlQuery);
            throw new ApplicationException(e);
        }
    }

    /**
     * Является ли запись подходящей под исходные условия поиска, то есть, содержит ли свойства, по которым
     * в запросе происходил отбор. Нужно для проверки записей, полученных после поиска по упрощенным запросам.
     * Упрощенные запросы могут быть получены после формирования на основе какой-либо стратегии
     * @param row - строка для проверки
     * @param selectQuery - исходные условия отбора записей
     * @return - признак того, что запись подходит
     * @throws ApplicationException - если не удается получить запись
     */
    public boolean containsSelectQueryConditionFields(ResultSetRow row, SelectQuery selectQuery) throws ApplicationException {
        /**
         * пока сделаем проверку самым простым образом, что экземпляр записи из конкретной строки содержит
         * свойства, по которым мы пытались искать
         */
        final OWLIndividual individual = owlService.getIndividual(row.getObjectIRI());
        final Collection<OWLClass> classes = Collections.singletonList(owlService.getIndividualClass(individual));
        /**
         * в это месте надо придумать, как быть с условиями, которые объединены через OR, а не через AND
         */
        return CollectionUtils.every(selectQuery.getWhereParts(), new Specification<WherePart>() {
            @Override
            public boolean isSatisfied(WherePart wherePart) {
                final OWLProperty property = wherePart.getProperty();
                return CollectionUtils.some(classes, new Specification<OWLClass>() {
                    @Override
                    public boolean isSatisfied(OWLClass owlClass) {
                        final Collection<OWLDataProperty> dataProperties = wrapperService.invokeWrapped(new ExceptionWrappedCallback<Collection<OWLDataProperty>>() {
                            @Override
                            public Collection<OWLDataProperty> run() throws ApplicationException {
                                return owlService.getDataProperties(owlClass);
                            }
                        });
                        final Collection<OWLObjectProperty> objectProperties = wrapperService.invokeWrapped(new ExceptionWrappedCallback<Collection<OWLObjectProperty>>() {
                            @Override
                            public Collection<OWLObjectProperty> run() throws ApplicationException {
                                return owlService.getObjectProperties(owlClass);
                            }
                        });
                        return CollectionUtils.some(dataProperties, new Specification<OWLDataProperty>() {
                            @Override
                            public boolean isSatisfied(OWLDataProperty dataProperty) {
                                return OWLUtils.equals(property, dataProperty);
                            }
                        }) || CollectionUtils.some(objectProperties, new Specification<OWLObjectProperty>() {
                            @Override
                            public boolean isSatisfied(OWLObjectProperty objectProperty) {
                                return OWLUtils.equals(property, objectProperty);
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Convert result set no weighted. If provided result set is already weighted and <code>forceWeight</code>
     * equals to false, it will returned as is. On other hand it will be forced weighted.
     * @param resultSet result set to weight
     * @param weightCalculator weight calculator
     * @param forceWeight force mark
     * @return weighted result set
     */
    protected WeighedResultSet toWeightedResultSet(ResultSet resultSet,
                                                   WeighedRowWeightCalculator weightCalculator,
                                                   boolean forceWeight) {

        Validation.assertNotNull("Result set not provided", resultSet);
        Validation.assertNotNull("Row weight calculator not provided", weightCalculator);

        if ((resultSet instanceof WeighedResultSet) && !forceWeight) {
            return (WeighedResultSet) resultSet;
        }
        return new WeighedResultSet(resultSet, weightCalculator);
    }

    /**
     * Convert non-weighted result set to weighted. If provided result set is already weighted, it will
     * be returned as is.
     * @param dataResultSet result set to weight
     * @param weightCalculator row weight calculator
     * @return weighted result set
     */
    protected WeighedResultSet toWeightedResultSet(ResultSet dataResultSet,
                                                   WeighedRowWeightCalculator weightCalculator) {

        Validation.assertNotNull("Result set not provided", dataResultSet);
        Validation.assertNotNull("Row weight calculator not provided", weightCalculator);

        if (dataResultSet instanceof WeighedResultSet) {
            return (WeighedResultSet) dataResultSet;
        }
        return new WeighedResultSet(dataResultSet, weightCalculator);
    }
}
