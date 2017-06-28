package ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part;

import org.semanticweb.owlapi.model.OWLClass;
import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.common.FieldConstants;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.FromType;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by abarmin on 26.06.17.
 *
 * Конвертер {@link FromType} в SWRL
 */
@Component
public class FromTypeSwrlConverter implements SwrlConverter<FromType> {
    private final SwrlPrefixResolver prefixResolver;

    @Inject
    public FromTypeSwrlConverter(SwrlPrefixResolver prefixResolver) {
        this.prefixResolver = prefixResolver;
    }

    @Override
    public String convert(FromType part) throws ApplicationException {
        final OWLClass owlClass = part.getOwlClass();
        //
        final Collection<String> parts = new ArrayList<>();
        parts.add(prefixResolver.extractPrefix(owlClass.getIRI()));
        parts.add(":");
        parts.add(owlClass.getIRI().getFragment());
        parts.add("(");
        parts.add("?" + FieldConstants.OBJECT_IRI);
        parts.add(")");
        //
        return StringUtils.join(parts, "");
    }
}
