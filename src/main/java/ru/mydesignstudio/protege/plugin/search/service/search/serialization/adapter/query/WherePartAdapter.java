package ru.mydesignstudio.protege.plugin.search.service.search.serialization.adapter.query;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationRuntimeException;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;

import java.lang.reflect.Type;

/**
 * Created by abarmin on 08.05.17.
 *
 * Адаптер для части запроса с условием
 */
public class WherePartAdapter implements JsonSerializer<WherePart>, JsonDeserializer<WherePart> {
    @Override
    public JsonElement serialize(WherePart wherePart, Type type, JsonSerializationContext jsonSerializationContext) {
        final JsonObject jsonObject = new JsonObject();
        //
        jsonObject.add("owlClass", jsonSerializationContext.serialize(wherePart.getOwlClass(), OWLClass.class));
        jsonObject.add("logicalOperation", jsonSerializationContext.serialize(wherePart.getLogicalOperation()));
        jsonObject.add("property", jsonSerializationContext.serialize(wherePart.getProperty(), OWLProperty.class));
        final Object value = wherePart.getValue();
        jsonObject.add("value", jsonSerializationContext.serialize(value, value.getClass()));
        jsonObject.add("valueType", new JsonPrimitive(value.getClass().getCanonicalName()));
        //
        return jsonObject;
    }

    @Override
    public WherePart deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final WherePart wherePart = new WherePart();
        //
        final JsonObject jsonObject = (JsonObject) jsonElement;
        wherePart.setOwlClass(jsonDeserializationContext.deserialize(
                jsonObject.get("owlClass"),
                OWLClass.class
        ));
        wherePart.setLogicalOperation(jsonDeserializationContext.deserialize(
                jsonObject.get("logicalOperation"),
                LogicalOperation.class
        ));
        wherePart.setProperty(jsonDeserializationContext.deserialize(
                jsonObject.get("property"),
                OWLProperty.class
        ));
        try {
            final Class<?> valueClass = Class.forName(jsonObject.get("valueType").getAsString());
            wherePart.setValue(jsonDeserializationContext.deserialize(
                    jsonObject.get("value"),
                    valueClass
            ));
        } catch (Exception e) {
            throw new ApplicationRuntimeException(e);
        }
        //
        return wherePart;
    }
}
