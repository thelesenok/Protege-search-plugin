package ru.mydesignstudio.protege.plugin.search.service.search.serialization.adapter.domain;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationRuntimeException;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainIndividual;
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;

import java.lang.reflect.Type;

/**
 * Created by abarmin on 08.05.17.
 *
 * Адаптер для конкретного объекта
 */
public class OWLDomainIndividualAdapter implements JsonSerializer<OWLDomainIndividual>, JsonDeserializer<OWLDomainIndividual> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OWLDomainLiteralAdapter.class);

    @Override
    public JsonElement serialize(OWLDomainIndividual owlDomainIndividual, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(owlDomainIndividual.getQuotedString());
    }

    @Override
    public OWLDomainIndividual deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonPrimitive jsonPrimitive = (JsonPrimitive) jsonElement;
        final OWLService owlService = InjectionUtils.getInstance(OWLService.class);
        try {
            final IRI iri = IRI.create(jsonPrimitive.getAsString());
            final OWLNamedIndividual individual = (OWLNamedIndividual) owlService.getIndividual(iri);
            return new OWLDomainIndividual(individual);
        } catch (ApplicationException e) {
            LOGGER.error("Can't deserialize domain individual",e);
            throw new ApplicationRuntimeException(e);
        }
    }
}
