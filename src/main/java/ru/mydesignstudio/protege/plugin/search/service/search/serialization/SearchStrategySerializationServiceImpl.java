package ru.mydesignstudio.protege.plugin.search.service.search.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;
import ru.mydesignstudio.protege.plugin.search.api.search.params.LookupParam;
import ru.mydesignstudio.protege.plugin.search.api.service.SearchStrategySerializationService;
import ru.mydesignstudio.protege.plugin.search.api.service.SearchStrategyService;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainClass;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainIndividual;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainLiteral;
import ru.mydesignstudio.protege.plugin.search.service.search.serialization.adapter.SearchProcessorParamsAdapter;
import ru.mydesignstudio.protege.plugin.search.service.search.serialization.adapter.SearchStrategyAdapter;
import ru.mydesignstudio.protege.plugin.search.service.search.serialization.adapter.domain.OWLDomainClassAdapter;
import ru.mydesignstudio.protege.plugin.search.service.search.serialization.adapter.domain.OWLDomainIndividualAdapter;
import ru.mydesignstudio.protege.plugin.search.service.search.serialization.adapter.domain.OWLDomainLiteralAdapter;
import ru.mydesignstudio.protege.plugin.search.service.search.serialization.adapter.owl.OWLClassAdapter;
import ru.mydesignstudio.protege.plugin.search.service.search.serialization.adapter.owl.OWLPropertyAdapter;
import ru.mydesignstudio.protege.plugin.search.service.search.serialization.adapter.query.WherePartAdapter;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by abarmin on 08.05.17.
 */
@Component
public class SearchStrategySerializationServiceImpl implements SearchStrategySerializationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchStrategySerializationServiceImpl.class);

    private Gson serializer;

    @Inject
    private SearchStrategyService strategyService;

    @PostConstruct
    public void init() {
        final GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter(OWLDomainClass.class, new OWLDomainClassAdapter());
        builder.registerTypeAdapter(OWLDomainIndividual.class, new OWLDomainIndividualAdapter());
        builder.registerTypeAdapter(OWLDomainLiteral.class, new OWLDomainLiteralAdapter());
        builder.registerTypeAdapter(OWLClass.class, new OWLClassAdapter());
        builder.registerTypeAdapter(OWLProperty.class, new OWLPropertyAdapter());
        builder.registerTypeAdapter(SearchProcessorParams.class, new SearchProcessorParamsAdapter());
        builder.registerTypeAdapter(WherePart.class, new WherePartAdapter());
        /**
         * какой-то непонятный глюк, что нужно адаптер для
         * каждой стратегии регистрировать отдельно.
         */
        builder.registerTypeAdapter(SearchStrategy.class, new SearchStrategyAdapter());
        for (SearchStrategy strategy : strategyService.getStrategies()) {
            builder.registerTypeAdapter(strategy.getClass(), new SearchStrategyAdapter());
        }
        serializer = builder.create();
    }

    @Override
    public void save(File targetFile, Collection<LookupParam> properties) throws ApplicationException {
        FileWriter writer = null;
        try {
            writer = new FileWriter(targetFile);
            serializer.toJson(properties, writer);
        } catch (IOException e) {
            throw new ApplicationException(e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    @Override
    public Collection<LookupParam> load(File sourceFile) throws ApplicationException {
        FileReader reader = null;
        final Collection<LookupParam> properties;
        try {
            reader = new FileReader(sourceFile);
            properties = serializer.fromJson(reader, new TypeToken<Collection<LookupParam>>() {
            }.getType());
        } catch (IOException e) {
            throw new ApplicationException(e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return properties;
    }
}
