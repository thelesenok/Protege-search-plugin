package ru.mydesignstudio.protege.plugin.search.config;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.WeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.WeightCalculatorDefault;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.ProximityCalculator;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.ProximityCalculatorEndsWith;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.ProximityCalculatorEquals;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.ProximityCalculatorFuzzyLike;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.ProximityCalculatorLessThan;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.ProximityCalculatorLessThanOrEquals;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.ProximityCalculatorLike;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.ProximityCalculatorMoreThan;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.ProximityCalculatorMoreThanOrEquals;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.ProximityCalculatorNotEquals;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.ProximityCalculatorStartsWith;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.binding.CalculatorEndsWith;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.binding.CalculatorEquals;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.binding.CalculatorFuzzyLike;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.binding.CalculatorLessOrEqulas;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.binding.CalculatorLessThan;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.binding.CalculatorLike;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.binding.CalculatorMoreOrEquals;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.binding.CalculatorMoreThan;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.binding.CalculatorNotEquals;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.binding.CalculatorStartsWith;
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
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.property.DataPropertyWeightFactory;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.property.ObjectPropertyWeightFactory;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.property.PropertyWeightFactory;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.property.binding.DataWeightFactory;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.property.binding.ObjectWeightFactory;
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

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;

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
        /** Property weight factory */
        bind(PropertyWeightFactory.class)
                .annotatedWith(DataWeightFactory.class)
                .to(DataPropertyWeightFactory.class);
        bind(PropertyWeightFactory.class)
                .annotatedWith(ObjectWeightFactory.class)
                .to(ObjectPropertyWeightFactory.class);
        /** Proximity calculators */
        bind(ProximityCalculator.class)
        		.annotatedWith(CalculatorEndsWith.class)
        		.to(ProximityCalculatorEndsWith.class);
        bind(ProximityCalculator.class)
        		.annotatedWith(CalculatorEquals.class)
        		.to(ProximityCalculatorEquals.class);
        bind(ProximityCalculator.class)
        		.annotatedWith(CalculatorFuzzyLike.class)
        		.to(ProximityCalculatorFuzzyLike.class);
        bind(ProximityCalculator.class)
        		.annotatedWith(CalculatorLessOrEqulas.class)
        		.to(ProximityCalculatorLessThanOrEquals.class);
        bind(ProximityCalculator.class)
        		.annotatedWith(CalculatorLessThan.class)
        		.to(ProximityCalculatorLessThan.class);
        bind(ProximityCalculator.class)
        		.annotatedWith(CalculatorLike.class)
        		.to(ProximityCalculatorLike.class);
        bind(ProximityCalculator.class)
        		.annotatedWith(CalculatorMoreOrEquals.class)
        		.to(ProximityCalculatorMoreThanOrEquals.class);
        bind(ProximityCalculator.class)
        		.annotatedWith(CalculatorMoreThan.class)
        		.to(ProximityCalculatorMoreThan.class);
        bind(ProximityCalculator.class)
        		.annotatedWith(CalculatorNotEquals.class)
        		.to(ProximityCalculatorNotEquals.class);
        bind(ProximityCalculator.class)
        		.annotatedWith(CalculatorStartsWith.class)
        		.to(ProximityCalculatorStartsWith.class);
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
