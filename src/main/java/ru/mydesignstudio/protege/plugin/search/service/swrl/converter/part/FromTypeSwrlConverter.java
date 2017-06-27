package ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part;

import org.semanticweb.owlapi.model.OWLClass;
import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.common.FieldConstants;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.FromType;

/**
 * Created by abarmin on 26.06.17.
 *
 * Конвертер {@link FromType} в SWRL
 */
@Component
public class FromTypeSwrlConverter implements SwrlConverter<FromType> {
    @Override
    public String convert(FromType part) throws ApplicationException {
        final OWLClass owlClass = part.getOwlClass();
        return owlClass.getIRI().getFragment() + "(" +
                "?" + FieldConstants.OBJECT_IRI +
                ")";
    }
}
