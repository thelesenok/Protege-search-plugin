package ru.mydesignstudio.protege.plugin.search.service.swrl;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.swrlapi.core.SWRLAPIRule;
import org.swrlapi.core.SWRLRuleEngine;
import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.common.Validation;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;
import ru.mydesignstudio.protege.plugin.search.api.search.params.LookupParam;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.SwrlService;
import ru.mydesignstudio.protege.plugin.search.service.swrl.converter.SelectQueryToSwrlConverter;
import ru.mydesignstudio.protege.plugin.search.service.swrl.rule.engine.SwrlEngineManager;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.AttributiveProcessorParams;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;
import ru.mydesignstudio.protege.plugin.search.utils.Transformer;

import javax.inject.Inject;
import java.util.Collection;

/**
 * Created by abarmin on 26.06.17.
 */
@Component
public class SwrlServiceImpl implements SwrlService {
    private final SelectQueryToSwrlConverter queryToSwrlConverter;
    private final OWLService owlService;
    private final SwrlEngineManager engineManager;

    @Inject
    public SwrlServiceImpl(SelectQueryToSwrlConverter queryToSwrlConverter, OWLService owlService,
                           SwrlEngineManager engineManager) {
        this.queryToSwrlConverter = queryToSwrlConverter;
        this.owlService = owlService;
        this.engineManager = engineManager;
    }

    @Override
    public String convertToSwrl(IRI individualIri, Collection<LookupParam> lookupParams) throws ApplicationException {
        /**
         * По IRI получим объект, который нашелся
         */
        final OWLIndividual individual = owlService.getIndividual(individualIri);
        /**
         * Получим параметры запроса
         */
        final AttributiveProcessorParams attributiveProcessorParams = (AttributiveProcessorParams) CollectionUtils.findFirst(CollectionUtils.map(lookupParams, new Transformer<LookupParam, SearchProcessorParams>() {
            @Override
            public SearchProcessorParams transform(LookupParam item) {
                return item.getStrategyParams();
            }
        }), new Specification<SearchProcessorParams>() {
            @Override
            public boolean isSatisfied(SearchProcessorParams params) {
                return params instanceof AttributiveProcessorParams;
            }
        });
        Validation.assertNotNull("There is not attributive params", attributiveProcessorParams);
        /**
         * Все передаем фабрике
         */
        return queryToSwrlConverter.covert(individual, attributiveProcessorParams.getSelectQuery());
    }

    @Override
    public SWRLAPIRule createSwrlRule(String ruleName, String swrlRule) throws ApplicationException {
        /**
         * Получаем фабрику SWRL-ов
         */
        final SWRLRuleEngine engine = engineManager.getSwrlEngine();
        /**
         * Создаем правило
         */
        try {
            return engine.createSWRLRule(ruleName, swrlRule);
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }
}
