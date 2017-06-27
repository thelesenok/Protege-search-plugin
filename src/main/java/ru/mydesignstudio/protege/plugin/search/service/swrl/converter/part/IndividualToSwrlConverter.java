package ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;

import javax.inject.Inject;

/**
 * Created by abarmin on 26.06.17.
 *
 * Конвертирует результат в виде {@link OWLIndividual} в SWRL-строку
 */
@Component
public class IndividualToSwrlConverter implements SwrlConverter<OWLIndividual> {
    private final OWLService owlService;

    @Inject
    public IndividualToSwrlConverter(OWLService owlService) {
        this.owlService = owlService;
    }

    @Override
    public String convert(OWLIndividual part) throws ApplicationException {
        /**
         * Узнаем, какого типа на самом деле результат
         */
        final OWLClass individualClass = owlService.getIndividualClass(part);
        /**
         * Конвертируем
         */
        return individualClass.getIRI().getFragment() + "(" +
                part.toStringID() +
                ")";
    }
}
