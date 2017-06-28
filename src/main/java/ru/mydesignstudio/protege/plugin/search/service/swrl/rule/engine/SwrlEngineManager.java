package ru.mydesignstudio.protege.plugin.search.service.swrl.rule.engine;

import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swrlapi.core.SWRLRuleEngine;
import org.swrlapi.factory.SWRLAPIFactory;
import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;

import javax.inject.Inject;

/**
 * Created by abarmin on 28.06.17.
 *
 * Синглтон для ленивой инициализации движка swrl. Инициализация занимает много времени, поэтому инициализируем
 * его только один раз
 */
@Component
public class SwrlEngineManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SwrlEngineManager.class);

    private final OWLService owlService;

    @Inject
    public SwrlEngineManager(OWLService owlService) {
        this.owlService = owlService;
    }

    private SWRLRuleEngine ruleEngineHolder = null;

    /**
     * Получить движок SWRL-языка
     * @return - инстанс движка
     * @throws ApplicationException
     */
    public SWRLRuleEngine getSwrlEngine() throws ApplicationException {
        if (ruleEngineHolder == null) {
            LOGGER.warn("Initializing SWRL Engine");
            final OWLOntology currentOntology = owlService.getOntology();
            LOGGER.warn("... ontology acquired, creating engine");
            ruleEngineHolder = SWRLAPIFactory.createSWRLRuleEngine(currentOntology);
            LOGGER.warn("... engine created. Inferring");
            ruleEngineHolder.infer();
            LOGGER.warn("... inferred");
        }
        return ruleEngineHolder;
    }
}
