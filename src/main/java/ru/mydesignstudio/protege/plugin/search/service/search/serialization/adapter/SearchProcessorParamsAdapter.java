package ru.mydesignstudio.protege.plugin.search.service.search.serialization.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationRuntimeException;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;

import java.lang.reflect.Type;

/**
 * Created by abarmin on 08.05.17.
 *
 * Адаптер для параметров стратегий
 */
public class SearchProcessorParamsAdapter implements JsonSerializer<SearchProcessorParams>, JsonDeserializer<SearchProcessorParams> {
    @Override
    public JsonElement serialize(SearchProcessorParams searchProcessorParams, Type type, JsonSerializationContext jsonSerializationContext) {
        final JsonElement serialized = jsonSerializationContext.serialize(searchProcessorParams, searchProcessorParams.getClass());
        final JsonObject jsonObject = (JsonObject) serialized;
        jsonObject.add("_type", new JsonPrimitive(searchProcessorParams.getClass().getCanonicalName()));
        return serialized;
    }

    @Override
    public SearchProcessorParams deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonObject jsonObject = (JsonObject) jsonElement;
        final String classNameString = jsonObject.get("_type").getAsString();
        try {
            final Class<?> paramsClass = Class.forName(classNameString);
            jsonObject.remove("_type");
            return jsonDeserializationContext.deserialize(jsonObject, paramsClass);
        } catch (Exception e) {
            throw new ApplicationRuntimeException(e);
        }
    }
}
