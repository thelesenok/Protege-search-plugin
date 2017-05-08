package ru.mydesignstudio.protege.plugin.search.service.search.serialization.adapter.query;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;

import java.lang.reflect.Type;

/**
 * Created by abarmin on 08.05.17.
 *
 * Адаптер для части запроса с условием
 */
@Deprecated
public class WherePartAdapter implements JsonSerializer<WherePart> {
    @Override
    public JsonElement serialize(WherePart wherePart, Type type, JsonSerializationContext jsonSerializationContext) {
        final JsonElement serialize = jsonSerializationContext.serialize(wherePart);
        final JsonObject jsonObject = (JsonObject) serialize;
        jsonObject.add("_valueType", new JsonPrimitive(wherePart.getValue().getClass().getCanonicalName()));
        return jsonObject;
    }
}
