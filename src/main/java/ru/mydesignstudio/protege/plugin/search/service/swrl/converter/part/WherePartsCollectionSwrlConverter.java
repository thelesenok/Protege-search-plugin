package ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part;

import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
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
public class WherePartsCollectionSwrlConverter implements SwrlConverter<Collection<WherePart>> {
    private final WherePartSwrlConverter wherePartConverter;

    @Inject
    public WherePartsCollectionSwrlConverter(WherePartSwrlConverter wherePartConverter) {
        this.wherePartConverter = wherePartConverter;
    }

    @Override
    public String convert(Collection<WherePart> parts) throws ApplicationException {
        final StringBuilder builder = new StringBuilder();
        /**
         * Конвертируем части по отдельности
         */
        int index = 0;
        final Collection<String> swrlPart = new ArrayList<>();
        for (WherePart part : parts) {
            swrlPart.add(wherePartConverter.convert(part, index));
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
