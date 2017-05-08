package ru.mydesignstudio.protege.plugin.search.service.search.serialization.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationRuntimeException;
import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.service.SearchStrategyService;
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;

import java.lang.reflect.Type;

/**
 * Created by abarmin on 08.05.17.
 *
 * Адаптер для стратегии поиска
 */
public class SearchStrategyAdapter implements JsonSerializer<SearchStrategy>, JsonDeserializer<SearchStrategy> {
    @Override
    public JsonElement serialize(SearchStrategy searchStrategy, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(searchStrategy.getClass().getCanonicalName());
    }

    @Override
    public SearchStrategy deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonPrimitive primitive = (JsonPrimitive) jsonElement;
        try {
            final Class<? extends SearchStrategy> strategyClass = (Class<? extends SearchStrategy>) Class.forName(primitive.getAsString());
            final SearchStrategyService strategyService = InjectionUtils.getInstance(SearchStrategyService.class);
            return strategyService.getStrategy(strategyClass);
        } catch (Exception e) {
            throw new ApplicationRuntimeException(e);
        }
    }
}
