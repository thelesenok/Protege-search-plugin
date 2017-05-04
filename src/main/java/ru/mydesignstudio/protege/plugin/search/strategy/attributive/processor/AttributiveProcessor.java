package ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.search.collector.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.config.OntologyConfig;
import ru.mydesignstudio.protege.plugin.search.reasoner.sparql.BasicSparqlReasonerFactory;
import ru.mydesignstudio.protege.plugin.search.reasoner.sparql.SparqlInferenceFactory;
import ru.mydesignstudio.protege.plugin.search.reasoner.sparql.SparqlReasoner;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query.SparqlQueryConverter;

import javax.inject.Inject;

/**
 * Created by abarmin on 12.03.17.
 *
 * Процессор для поиска по атрибутам
 */
public class AttributiveProcessor implements SearchProcessor<AttributiveProcessorParams> {
    @Inject
    private SparqlQueryConverter queryConverter;

    @Override
    public SelectQuery prepareQuery(SelectQuery initialQuery, AttributiveProcessorParams strategyParams) throws ApplicationException {
        return strategyParams.getSelectQuery();
    }

    @Override
    public ResultSet collect(ResultSet initialResultSet, SelectQuery selectQuery, AttributiveProcessorParams strategyParams) throws ApplicationException {
        try {
            final SparqlInferenceFactory factory = new BasicSparqlReasonerFactory();
            final SparqlReasoner reasoner = factory.createReasoner(OntologyConfig.getModelManager().getOWLOntologyManager());
            reasoner.precalculate();
            //
            final String sparqlQuery = queryConverter.convert(selectQuery);
            return reasoner.executeQuery(sparqlQuery);
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }
}
