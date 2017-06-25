package ru.mydesignstudio.protege.plugin.search.strategy.support.processor;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.config.OntologyConfig;
import ru.mydesignstudio.protege.plugin.search.reasoner.sparql.BasicSparqlReasonerFactory;
import ru.mydesignstudio.protege.plugin.search.reasoner.sparql.SparqlInferenceFactory;
import ru.mydesignstudio.protege.plugin.search.reasoner.sparql.SparqlReasoner;
import ru.mydesignstudio.protege.plugin.search.reasoner.sparql.SparqlReasonerException;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query.SparqlQueryConverter;

import javax.inject.Inject;

/**
 * Created by abarmin on 05.05.17.
 *
 * Абстрактный процессор с поддержкой поиска по sparql запросу
 */
public abstract class SparqlProcessorSupport {
    @Inject
    private SparqlQueryConverter queryConverter;
    private SparqlReasoner reasonerHolder;

    public SparqlReasoner getReasoner() throws ApplicationException {
        if (reasonerHolder == null) {
            final SparqlInferenceFactory factory = new BasicSparqlReasonerFactory();
            reasonerHolder = factory.createReasoner(OntologyConfig.getModelManager().getOWLOntologyManager());
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
        final String sparqlQuery = queryConverter.convert(selectQuery);
        try {
            final SparqlReasoner reasoner = getReasoner();
            return reasoner.executeQuery(sparqlQuery);
        } catch (SparqlReasonerException e) {
            throw new ApplicationException(e);
        }
    }
}
