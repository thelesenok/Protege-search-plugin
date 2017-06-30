package ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part;

import org.semanticweb.owlapi.model.OWLClass;
import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.common.Pair;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by abarmin on 26.06.17.
 *
 * Конвертер коллекции параметров в SWRL правило
 */
@Component
public class WherePartsCollectionSwrlConverter implements SwrlConverter<Pair<OWLClass, Collection<WherePart>>> {
    private final WherePartSwrlConverter wherePartConverter;

    @Inject
    public WherePartsCollectionSwrlConverter(WherePartSwrlConverter wherePartConverter) {
        this.wherePartConverter = wherePartConverter;
    }

    @Override
    public String convert(Pair<OWLClass, Collection<WherePart>> pair) throws ApplicationException {
        final StringBuilder builder = new StringBuilder();
        /**
         * Конвертируем части по отдельности
         */
        int index = 0;
        final Collection<String> swrlPart = new ArrayList<>();
        for (WherePart part : pair.getSecond()) {
            swrlPart.add(wherePartConverter.convert(new Pair<>(pair.getFirst(), part), index));
            index++;
        }
        /**
         * Объединяем их через пробел
         */
        builder.append(StringUtils.join(swrlPart, " "));
        /**
         * Готово
         */
        return builder.toString();
    }
}
