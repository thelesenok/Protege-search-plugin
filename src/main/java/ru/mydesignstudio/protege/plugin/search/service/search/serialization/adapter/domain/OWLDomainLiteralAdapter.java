package ru.mydesignstudio.protege.plugin.search.service.search.serialization.adapter.domain;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainLiteral;
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;

import java.lang.reflect.Type;

/**
 * Created by abarmin on 08.05.17.
 *
 * Адаптер для перечисления
 */
public class OWLDomainLiteralAdapter implements JsonSerializer<OWLDomainLiteral>, JsonDeserializer<OWLDomainLiteral> {
    @Override
    public JsonElement serialize(OWLDomainLiteral owlDomainLiteral, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(owlDomainLiteral.getQuotedString());
    }

    @Override
    public OWLDomainLiteral deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonPrimitive jsonPrimitive = (JsonPrimitive) jsonElement;
        final OWLService owlService = InjectionUtils.getInstance(OWLService.class);
        return null;
    }
}
