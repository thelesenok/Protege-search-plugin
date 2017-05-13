package ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.attributive.processor.FuzzyWherePart;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by abarmin on 11.05.17.
 *
 * Конвертер значений для нечеткого поиска.
 * Очень похож на StringWherePartConverter, но поддерживает только одно условие
 */
public class FuzzyWherePartConverter implements WherePartConditionConverter<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FuzzyWherePartConverter.class);
    public static final Character TOKEN = '.';

    @Override
    public String convert(WherePart wherePart, String value, String variableName) throws ApplicationException {
        final FuzzyWherePart fuzzyWherePart = (FuzzyWherePart) wherePart;
        //
        final LogicalOperation operation = wherePart.getLogicalOperation();
        final StringBuilder builder = new StringBuilder();
        builder.append("FILTER(");
        if (LogicalOperation.FUZZY_LIKE.equals(operation)) {
            final Collection<String> parts = new HashSet<>();
            for (String partValue : createAvailableValues(value, fuzzyWherePart.getMaskSize())) {
                parts.add(createPartialCondition(variableName, partValue));
            }
            builder.append(
                    StringUtils.join(parts, " || ")
            );
        } else {
            LOGGER.error("Unsupported operation {}", operation);
            throw new ApplicationException(String.format(
                    "Unsupported operation %s",
                    operation
            ));
        }
        builder.append(")");
        return builder.toString();
    }

    /**
     * Сгенерировать регулярку для конкретного возможного значения
     * @param variableName - переменная в sparql запросе
     * @param partValue - возможное значение
     * @return
     */
    private String createPartialCondition(String variableName, String partValue) {
        return "( REGEX(" + variableName + ", \"" + partValue + "\") )";
    }

    /**
     * Построить коллекцию из всех возможных значений для поиска с учетом маске
     * @param originalValue - исходное значение, в нем будем заменять символы
     * @param maskSize - количество символов в маске
     * @return
     */
    public Collection<String> createAvailableValues(final String originalValue, int maskSize) {
        if (maskSize >= originalValue.length()) {
            /**
             * маска длинее исходного значения
             */
            return Collections.singleton(StringUtils.repeat(TOKEN, maskSize));
        }
        final Collection<String> values = new HashSet<>();
        for (int start = 0; start < originalValue.length() - maskSize; start++) {
            List<Character> currentToken = createInitialToken(originalValue, start, maskSize);
            values.add(charListToString(currentToken));
            while (hasNextToken(currentToken, maskSize)) {
                currentToken = createNextToken(currentToken, originalValue);
                values.add(charListToString(currentToken));
            }
        }
        return values;
    }

    private String charListToString(List<Character> characterList) {
        final StringBuilder builder = new StringBuilder();
        for (Character character : characterList) {
            builder.append(character);
        }
        return builder.toString();
    }

    private List<Character> createNextToken(List<Character> currentValue, String originalValue) {
        /**
         * идем справа налево, ищем токен, который можно сдвинуть
         * здесь гарантировано есть такой токен, так как мы проверили
         */
        int tokenPosition = 0;
        for (int index = currentValue.size() - 2; index >= 0; index--) {
            if (TOKEN.equals(currentValue.get(index)) && !TOKEN.equals(currentValue.get(index + 1))) {
                tokenPosition = index;
                break;
            }
        }
        /**
         * мы узнали номер элемента, который двигаем направо
         * меняем этот элемент на значение из исходной строки,
         * в следующей позиции ставим токен
         */
        currentValue.remove(tokenPosition);
        currentValue.remove(tokenPosition);
        currentValue.add(tokenPosition, originalValue.charAt(tokenPosition));
        currentValue.add(tokenPosition + 1, TOKEN);
        return currentValue;
    }

    /**
     * Можно ли сгенерировать еще токен
     * @param currentValue - текущий токен
     * @param maskSize - сколько символов в токене
     * @return
     */
    private boolean hasNextToken(List<Character> currentValue, int maskSize) {
        /**
         * посчитаем, в скольких симолах с конца токен.
         * если совпадает, то больше нельзя
         */
        int tokens = 0;
        for (int i = currentValue.size() - 1; i >= 0; i--) {
            if (TOKEN.equals(currentValue.get(i))) {
                tokens++;
            } else {
                break;
            }
        }
        return tokens != maskSize;
    }

    /**
     * Создать исходный токен - заменяем первые символы на токен
     * @param originalValue - исходная строка
     * @param start - с какого символа начать токенизация
     * @param maskSize - сколько символов заменять
     * @return
     */
    private List<Character> createInitialToken(String originalValue, int start, int maskSize) {
        final List<Character> list = new LinkedList<>();
        for (int index = 0; index < start; index++) {
            list.add(originalValue.charAt(index));
        }
        for (int index = start; index < originalValue.length(); index++) {
            if (index < maskSize + start) {
                list.add(TOKEN);
            } else {
                list.add(originalValue.charAt(index));
            }
        }
        return list;
    }
}
