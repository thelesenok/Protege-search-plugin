package ru.mydesignstudio.protege.plugin.search.service.search.serialization.adapter.domain;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationRuntimeException;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(OWLDomainLiteralAdapter.class);

    @Override
    public JsonElement serialize(OWLDomainLiteral owlDomainLiteral, Type type, JsonSerializationContext jsonSerializationContext) {
        final String quotedString = owlDomainLiteral.getQuotedString();
        final String preparedString = quotedString.replace("\"", "");
        return new JsonPrimitive(preparedString);
    }

    @Override
    public OWLDomainLiteral deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonPrimitive jsonPrimitive = (JsonPrimitive) jsonElement;
        final OWLService owlService = InjectionUtils.getInstance(OWLService.class);
        try {
            final OWLLiteral literal = owlService.getLiteral(jsonElement.getAsString());
            return new OWLDomainLiteral(literal);
        } catch (ApplicationException e) {
            LOGGER.error("Can't deserialize domain literal", e);
            throw new ApplicationRuntimeException(e);
        }
    }
}
