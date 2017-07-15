package ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by abarmin on 26.06.17.
 *
 * Конвертирует результат в виде {@link OWLIndividual} в SWRL-строку
 */
@Component
public class IndividualToSwrlConverter implements SwrlConverter<OWLIndividual> {
    private final OWLService owlService;
    private final SwrlPrefixResolver prefixResolver;

    @Inject
    public IndividualToSwrlConverter(OWLService owlService, SwrlPrefixResolver prefixResolver) {
        this.owlService = owlService;
        this.prefixResolver = prefixResolver;
    }

    @Override
    public String convert(OWLIndividual part) throws ApplicationException {
        /**
         * Узнаем, какого типа на самом деле результат
         */
        final OWLClass individualClass = owlService.getIndividualClass(part);
        /**
         * Результат должен быть типа NamedIndividual, нам от него IRI надо взять
         */
        if (!(part instanceof OWLNamedIndividual)) {
            throw new ApplicationException(String.format(
                    "Unsupported individual type, %s",
                    part.getClass()
            ));
        }
        final OWLNamedIndividual individual = (OWLNamedIndividual) part;
        final String individualName = individual.getIRI().getFragment();
        /**
         * Конвертируем
         */
        final Collection<String> parts = new ArrayList<>();
        parts.add(prefixResolver.extractPrefix(individualClass.getIRI()));
        parts.add(":");
        parts.add(individualClass.getIRI().getFragment());
        parts.add("(");
        parts.add(prefixResolver.extractPrefix(individual.getIRI()));
        parts.add(":");
        parts.add(individualName);
        parts.add(")");
        return StringUtils.join(parts, "");
    }
}
