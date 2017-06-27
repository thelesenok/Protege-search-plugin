package ru.mydesignstudio.protege.plugin.search.service.swrl.converter;

import org.semanticweb.owlapi.model.OWLIndividual;
import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part.FromTypeSwrlConverter;
import ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part.IndividualToSwrlConverter;
import ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part.WherePartsCollectionSwrlConverter;

import javax.inject.Inject;

/**
 * Created by abarmin on 26.06.17.
 *
 * Конвертер поискового запроса и одного конкретного результата в SWRL-правило
 */
@Component
public class SelectQueryToSwrlConverter {
    private final FromTypeSwrlConverter fromTypeSwrlConverter;
    private final IndividualToSwrlConverter individualSwrlConverter;
    private final WherePartsCollectionSwrlConverter wherePartsCollectionConverter;

    @Inject
    public SelectQueryToSwrlConverter(FromTypeSwrlConverter fromTypeSwrlConverter, IndividualToSwrlConverter individualSwrlConverter, WherePartsCollectionSwrlConverter wherePartsCollectionConverter) {
        this.fromTypeSwrlConverter = fromTypeSwrlConverter;
        this.individualSwrlConverter = individualSwrlConverter;
        this.wherePartsCollectionConverter = wherePartsCollectionConverter;
    }

    /**
     * Конвертирует результат запроса и поисковый запрос в SWRL-правило
     * @param individual - результат запроса
     * @param selectQuery - запрос
     * @return - SWRL-правило в текстовой форме
     * @throws ApplicationException
     */
    public String covert(OWLIndividual individual, SelectQuery selectQuery) throws ApplicationException {
        final StringBuilder builder = new StringBuilder();
        /**
         * Добавляем FromType из запрос
         */
        builder.append(fromTypeSwrlConverter.convert(selectQuery.getFrom()));
        /**
         * Конвертируем параметры поиска
         */
        builder.append(" ^ ");
        builder.append(wherePartsCollectionConverter.convert(selectQuery.getWhereParts()));
        builder.append(" -> ");
        /**
         * Конвертируем результат
         */
        builder.append(individualSwrlConverter.convert(individual));
        /**
         * Все готово
         */
        return builder.toString();
    }
}
