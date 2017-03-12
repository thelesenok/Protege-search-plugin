package ru.mydesignstudio.protege.plugin.search.strategy.attributive.collector;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.search.collector.SearchCollector;
import ru.mydesignstudio.protege.plugin.search.config.OntologyConfig;
import ru.mydesignstudio.protege.plugin.search.reasoner.sparql.BasicSparqlReasonerFactory;
import ru.mydesignstudio.protege.plugin.search.reasoner.sparql.SparqlInferenceFactory;
import ru.mydesignstudio.protege.plugin.search.reasoner.sparql.SparqlReasoner;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.collector.sparql.query.SparqlQueryConverter;

import javax.inject.Inject;

/**
 * Created by abarmin on 12.03.17.
 */
public class AttributiveCollector implements SearchCollector<AttributiveCollectorParams> {
    @Inject
    private SparqlQueryConverter queryConverter;

    @Override
    public ResultSet collect(ResultSet initialResultSet, AttributiveCollectorParams strategyParams) throws ApplicationException {
        try {
            final SparqlInferenceFactory factory = new BasicSparqlReasonerFactory();
            final SparqlReasoner reasoner = factory.createReasoner(OntologyConfig.getModelManager().getOWLOntologyManager());
            reasoner.precalculate();
            //
            final String sparqlQuery = queryConverter.convert(strategyParams.getSelectQuery());
            return reasoner.executeQuery(sparqlQuery);
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }
}
