package ru.mydesignstudio.protege.plugin.search.config;

import java.lang.reflect.Method;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.WeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.WeightCalculatorDefault;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.PathBuilder;
import ru.mydesignstudio.protege.plugin.search.api.service.SearchStrategyRegistry;
import ru.mydesignstudio.protege.plugin.search.api.service.SearchStrategySerializationService;
import ru.mydesignstudio.protege.plugin.search.api.service.SearchStrategyService;
import ru.mydesignstudio.protege.plugin.search.api.service.SwrlService;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.FuzzyOWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.related.RelatedClassFactory;
import ru.mydesignstudio.protege.plugin.search.service.owl.OWLServiceImpl;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.FuzzyOWLServiceImpl;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.related.RelatedClassFactoryImpl;
import ru.mydesignstudio.protege.plugin.search.service.search.path.ShortestPathBuilder;
import ru.mydesignstudio.protege.plugin.search.service.search.serialization.SearchStrategySerializationServiceImpl;
import ru.mydesignstudio.protege.plugin.search.service.search.strategy.SearchStrategyRegistryImpl;
import ru.mydesignstudio.protege.plugin.search.service.search.strategy.SearchStrategyServiceImpl;
import ru.mydesignstudio.protege.plugin.search.service.swrl.SwrlServiceImpl;
import ru.mydesignstudio.protege.plugin.search.service.swrl.rule.engine.SwrlEngineManager;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology.processor.calculator.DatatypeCalculator;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology.processor.calculator.MaximumDatatypeCalculator;

/**
 * Created by abarmin on 03.01.17.
 */
public class ModuleConfig extends AbstractModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleConfig.class);

    @Override
    protected void configure() {
        bind(SearchStrategyService.class).to(SearchStrategyServiceImpl.class).in(Singleton.class);
        bind(SearchStrategyRegistry.class).to(SearchStrategyRegistryImpl.class).in(Singleton.class);
        bind(OWLService.class).to(OWLServiceImpl.class);
        bind(RelatedClassFactory.class).to(RelatedClassFactoryImpl.class);
        bind(WeightCalculator.class).to(WeightCalculatorDefault.class);
        bind(SwrlService.class).to(SwrlServiceImpl.class);
        bind(SwrlEngineManager.class).in(Singleton.class);
        bind(PathBuilder.class).to(ShortestPathBuilder.class);
        bind(FuzzyOWLService.class).to(FuzzyOWLServiceImpl.class);
        bind(DatatypeCalculator.class).to(MaximumDatatypeCalculator.class);
        bind(SearchStrategySerializationService.class).to(SearchStrategySerializationServiceImpl.class);
        bindListener(Matchers.any(), new TypeListener() {
            @Override
            public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
                typeEncounter.register(new InjectionListener<I>() {
                    @Override
                    public void afterInjection(I i) {
                        final Method[] methods = i.getClass().getMethods();
                        for (Method method : methods) {
                            if (method.getAnnotation(PostConstruct.class) != null) {
                                try {
                                    method.invoke(i);
                                } catch (Exception e) {
                                    LOGGER.warn("Can't invoke @PostConstruct method", e);
                                }
                            }
                        }
                    }
                });
            }
        });
    }
}
