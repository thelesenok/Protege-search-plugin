package ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part;

import org.semanticweb.owlapi.model.IRI;
import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

import java.lang.reflect.Field;

/**
 * Created by abarmin on 28.06.17.
 *
 * Выделяет префикс онтологии из строки вида http://www.owl-ontologies.com/generations.owl#
 */
@Component
public class SwrlPrefixResolver {
    /**
     * Извлечь префикс из IRI
     * @param iri - из чего извлекаем
     * @return - префикс онтологии
     * @throws ApplicationException
     */
    public String extractPrefix(IRI iri) throws ApplicationException {
        /**
         * Грязный хак, но иначе не придумалось
         */
        String prefixedString;
        try {
            final Field field = IRI.class.getDeclaredField("namespace");
            field.setAccessible(true);
            prefixedString = (String) field.get(iri);
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
        return extractPrefix(prefixedString);
    }

    /**
     * Извлечь префикс из строки
     * @param ontologyPrefixedString - строка с префиксом
     * @return - префикс онтологии
     * @throws ApplicationException
     */
    public String extractPrefix(String ontologyPrefixedString) throws ApplicationException {
        /**
         * Оставляем то, что левее .owl в строке
         */
        final String part = StringUtils.substringBeforeLast(ontologyPrefixedString, ".owl");
        /**
         * А теперь то, что правее /
         */
        return StringUtils.substringAfterLast(part, "/");
    }
}
