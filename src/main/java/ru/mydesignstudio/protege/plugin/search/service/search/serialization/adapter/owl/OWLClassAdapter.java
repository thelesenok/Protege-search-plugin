package ru.mydesignstudio.protege.plugin.search.service.search.serialization.adapter.owl;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationRuntimeException;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;

import java.lang.reflect.Type;

/**
 * Created by abarmin on 08.05.17.
 *
 * Адаптер для класса онтологии
 */
public class OWLClassAdapter implements JsonSerializer<OWLClass>, JsonDeserializer<OWLClass> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OWLClassAdapter.class);

    @Override
    public JsonElement serialize(OWLClass owlClass, Type type, JsonSerializationContext jsonSerializationContext) {
        final String quotedString = owlClass.getIRI().toQuotedString();
        final String preparedString = quotedString.replace("<", "").replace(">", "");
        return new JsonPrimitive(preparedString);
    }

    @Override
    public OWLClass deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonPrimitive jsonPrimitive = (JsonPrimitive) jsonElement;
        final OWLService owlService = InjectionUtils.getInstance(OWLService.class);
        final IRI iri = IRI.create(jsonPrimitive.getAsString());
        try {
            final OWLClass owlClass = owlService.getOWLClass(iri);
            return owlClass;
        } catch (ApplicationException e) {
            LOGGER.error("Can't deserialize OWLCLass", e);
            throw new ApplicationRuntimeException(e);
        }
    }
}
