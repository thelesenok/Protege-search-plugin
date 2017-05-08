package ru.mydesignstudio.protege.plugin.search.service.search.serialization;

import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationRuntimeException;
import ru.mydesignstudio.protege.plugin.search.api.search.params.LookupParam;
import ru.mydesignstudio.protege.plugin.search.api.service.SearchStrategySerializationService;
import ru.mydesignstudio.protege.plugin.search.service.EventBus;
import ru.mydesignstudio.protege.plugin.search.ui.event.properties.LoadSearchPropertiesEvent;
import ru.mydesignstudio.protege.plugin.search.ui.event.properties.LoadedSearchPropertiesEvent;
import ru.mydesignstudio.protege.plugin.search.ui.event.properties.SaveSearchPropertiesEvent;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;

/**
 * Created by abarmin on 08.05.17.
 */
@Singleton
public class SearchStrategySerializationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchStrategySerializationManager.class);

    @Inject
    private SearchStrategySerializationService serializationService;

    private final EventBus eventBus = EventBus.getInstance();

    @PostConstruct
    public void init() {
        eventBus.register(this);
    }

    /**
     * Обработчик события сохранения параметров поиска в файл
     * @param event - объект события
     */
    @Subscribe
    public void onSavePropertiesEventListener(SaveSearchPropertiesEvent event) {
        try {
            serializationService.save(
                    event.getTargetFile(),
                    event.getLookupParams()
            );
        } catch (ApplicationException e) {
            LOGGER.error("Can't save properties to file", e);
            throw new ApplicationRuntimeException(e);
        }
    }

    /**
     * Обработчик события загрузки параметров поиска из файла
     * @param event - объект события
     */
    @Subscribe
    public void onLoadPropertiesEventListener(LoadSearchPropertiesEvent event) {
        try {
            final Collection<LookupParam> properties = serializationService.load(event.getTargetFile());
            eventBus.publish(new LoadedSearchPropertiesEvent(properties));
        } catch (ApplicationException e) {
            LOGGER.error("Can't load properties from file", e);
            throw new ApplicationRuntimeException(e);
        }
    }
}
