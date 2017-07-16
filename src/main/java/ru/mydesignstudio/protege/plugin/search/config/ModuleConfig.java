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
import ru.mydesignstudio.protege.plugin.search.api.service.PropertyBasedPathBuilder;
import ru.mydesignstudio.protege.plugin.search.api.service.SearchStrategyRegistry;
import ru.mydesignstudio.protege.plugin.search.api.service.SearchStrategySerializationService;
import ru.mydesignstudio.protege.plugin.search.api.service.SearchStrategyService;
import ru.mydesignstudio.protege.plugin.search.api.service.SwrlService;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.FuzzyOWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.related.RelatedClassFactory;
import ru.mydesignstudio.protege.plugin.search.service.owl.OWLServiceImpl;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.FuzzyOWLServiceImpl;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.related.RelatedClassFactoryImpl;
import ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy.OwlClassHierarchyBuilder;
import ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy.OwlClassHierarchyBuilderImpl;
import ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy.path.PathBuildingStrategy;
import ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy.path.ShortestPathBuildingStrategy;
import ru.mydesignstudio.protege.plugin.search.service.search.path.ShortestPathBuilder;
import ru.mydesignstudio.protege.plugin.search.service.search.serialization.SearchStrategySerializationServiceImpl;
import ru.mydesignstudio.protege.plugin.search.service.search.strategy.SearchStrategyRegistryImpl;
import ru.mydesignstudio.protege.plugin.search.service.search.strategy.SearchStrategyServiceImpl;
import ru.mydesignstudio.protege.plugin.search.service.swrl.SwrlServiceImpl;
import ru.mydesignstudio.protege.plugin.search.service.swrl.rule.engine.SwrlEngineManager;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology.processor.calculator.DatatypeCalculator;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology.processor.calculator.MaximumDatatypeCalculator;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.taxonomy.processor.related.FuzzyTaxonomyRelatedQueryCreator;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.taxonomy.processor.related.binding.FuzzyQueryCreator;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.related.EqualClassesRelatedQueriesCreator;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.related.NearestNeighboursRelatedQueriesCreator;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.related.RelatedQueriesCreator;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.related.binding.EqualClassesQueryCreator;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.related.binding.NearestNeighboursQueryCreator;

/**
 * Created by abarmin on 03.01.17.
 */
public class ModuleConfig extends AbstractModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleConfig.class);

    @Override
    protected void configure() {
        bind(SearchStrategyService.class).to(SearchStrategyServiceImpl.class).in(Singleton.class);
        bind(SearchStrategyRegistry.class).to(SearchStrategyRegistryImpl.class).in(Singleton.class);
        /** Default services */
        bind(FuzzyOWLService.class).to(FuzzyOWLServiceImpl.class);
        bind(OWLService.class).to(OWLServiceImpl.class);
        bind(SwrlService.class).to(SwrlServiceImpl.class);
        bind(RelatedClassFactory.class).to(RelatedClassFactoryImpl.class);
        bind(WeightCalculator.class).to(WeightCalculatorDefault.class);
        bind(SwrlEngineManager.class).in(Singleton.class);
        /** Path building strategies */
        bind(PropertyBasedPathBuilder.class).to(ShortestPathBuilder.class);
        bind(PathBuildingStrategy.class).to(ShortestPathBuildingStrategy.class);
        bind(DatatypeCalculator.class).to(MaximumDatatypeCalculator.class);
        bind(OwlClassHierarchyBuilder.class).to(OwlClassHierarchyBuilderImpl.class);
        /** Related query builders */
        bind(RelatedQueriesCreator.class)
        		.annotatedWith(NearestNeighboursQueryCreator.class)
        		.to(NearestNeighboursRelatedQueriesCreator.class);
        bind(RelatedQueriesCreator.class)
        		.annotatedWith(EqualClassesQueryCreator.class)
        		.to(EqualClassesRelatedQueriesCreator.class);
        bind(RelatedQueriesCreator.class)
        		.annotatedWith(FuzzyQueryCreator.class)
        		.to(FuzzyTaxonomyRelatedQueryCreator.class);
        //
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
