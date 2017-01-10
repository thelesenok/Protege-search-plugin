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
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.SearchStrategyService;
import ru.mydesignstudio.protege.plugin.search.service.OWLServiceImpl;
import ru.mydesignstudio.protege.plugin.search.service.SearchStrategyRegistry;
import ru.mydesignstudio.protege.plugin.search.service.SearchStrategyServiceImpl;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;

/**
 * Created by abarmin on 03.01.17.
 */
public class ModuleConfig extends AbstractModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleConfig.class);

    @Override
    protected void configure() {
        bind(SearchStrategyService.class).to(SearchStrategyServiceImpl.class);
        bind(SearchStrategyRegistry.class).in(Singleton.class);
        bind(OWLService.class).to(OWLServiceImpl.class);
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
