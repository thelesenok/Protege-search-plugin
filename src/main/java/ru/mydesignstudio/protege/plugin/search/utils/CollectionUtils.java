package ru.mydesignstudio.protege.plugin.search.utils;

import ru.mydesignstudio.protege.plugin.search.api.common.Validation;
import ru.mydesignstudio.protege.plugin.search.utils.function.BinaryFunction;
import ru.mydesignstudio.protege.plugin.search.utils.function.Function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by abarmin on 04.01.17.
 *
 * Вспомогательные функции по работе с коллекциями
 */
public class CollectionUtils {
    /**
     * Переданная коллекция пустая
     * @param source - коллекция
     * @param <ITEM> - тип элемента коллекции
     * @return
     */
    public static final <ITEM> boolean isEmpty(Collection<ITEM> source) {
        return !isNotEmpty(source);
    }

    public static final <ITEM> boolean isNotEmpty(Collection<ITEM> source) {
        if (source == null) {
            return false;
        }
        if (source.size() == 0) {
            return false;
        }
        final Specification<ITEM> specification = new Specification<ITEM>() {
            @Override
            public boolean isSatisfied(ITEM item) {
                return item != null;
            }
        };
        return CollectionUtils.some(source, specification);
    }

    /**
     * Прогнать коллекцию через трансформер.
     * За неимением stream-ов делаем так вот
     * @param source - исходная коллекция
     * @param transformer - трансформер
     * @param <SOURCE> - тип элемента исходной коллекции
     * @param <DESTINATION> - тип элемента целевой коллекции
     * @return - трансформированная коллекция
     */
    public static final <SOURCE, DESTINATION> Collection<DESTINATION> flatMap(Collection<SOURCE> source, Transformer<SOURCE, Collection<DESTINATION>> transformer) {
        final Collection<DESTINATION> destinations = new ArrayList<DESTINATION>();
        for (SOURCE item : source) {
            destinations.addAll(transformer.transform(item));
        }
        return destinations;
    }

    /**
     * Прогнать коллекцию через трансформер.
     * За неимением stream-ов делаем так вот
     * @param source - исходная коллекция
     * @param transformer - трансформер
     * @param <SOURCE> - тип элемента исходной коллекции
     * @param <DESTINATION> - тип элемента целевой коллекции
     * @return - трансформированная коллекция
     */
    public static final <SOURCE, DESTINATION> Collection<DESTINATION> map(Collection<SOURCE> source, Transformer<SOURCE, DESTINATION> transformer) {
        final Collection<DESTINATION> destination = new ArrayList<DESTINATION>();
        for (SOURCE item : source) {
            destination.add(transformer.transform(item));
        }
        return destination;
    }

    /**
     * Найти минимальный элемет в коллекции
     * @param source - коллекция
     * @param comparator - компаратор
     * @param <ITEM> - тип элемента в коллекции
     * @return
     */
    public static final <ITEM> ITEM min(Collection<ITEM> source, Comparator<ITEM> comparator) {
        return Collections.min(source, comparator);
    }

    /**
     * Получить первый элемент коллекции, удовлетворяющий условию
     * @param source - коллекция
     * @param specification - условие
     * @param <ITEM> - элемент коллекции
     * @return - подходящий элемент
     */
    public static final <ITEM> ITEM findFirst(Collection<ITEM> source, Specification<ITEM> specification) {
        for (ITEM item : source) {
            if (specification.isSatisfied(item)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Get first value in collection of items.
     * @param source source collection
     * @param <ITEM> element type
     * @return first value of null if collection is empty
     */
    public static final <ITEM> ITEM findFirst(Collection<ITEM> source) {
        if (source.size() > 0) {
            return source.iterator().next();
        }
        return null;
    }

    public static final <ITEM> void forEach(Collection<ITEM> source, Action<ITEM> action) {
        for (ITEM item : source) {
            action.run(item);
        }
    }

    public static final <ITEM> boolean some(Collection<ITEM> source, Specification<ITEM> specification) {
        for (ITEM item : source) {
            if (specification.isSatisfied(item)) {
                return true;
            }
        }
        return false;
    }

    public static final <ITEM> boolean every(Collection<ITEM> source, Specification<ITEM> specification) {
        boolean result = CollectionUtils.isNotEmpty(source);
        for (ITEM item : source) {
            result = result && specification.isSatisfied(item);
        }
        return result;
    }

    public static final <ITEM> Collection<ITEM> filter(Collection<ITEM> source, Specification<ITEM> specification) {
        final Collection<ITEM> target = new ArrayList<>();
        for (ITEM item : source) {
            if (specification.isSatisfied(item)) {
                target.add(item);
            }
        }
        return target;
    }

    /**
     * Create collection of source collection from start to end indexes
     * @param source - source collection
     * @param start - start index (inclusive)
     * @param end - end index (exclusive)
     * @param <ITEM> - collection type
     * @return - copy of collection part
     */
    public static final <ITEM> Collection<ITEM> subcollection(Collection<ITEM> source, int start, int end) {
        Validation.assertTrue("Start should be less than end", start <= end);
        Validation.assertTrue("Start should be more or equals to zero", start >= 0);
        Validation.assertTrue("End should be more or equals to zero", end >= 0);
        if (start > source.size() - 1) {
            return Collections.emptyList();
        }
        if (end > source.size() - 1) {
            end = source.size();
        }
        final Collection<ITEM> result = new ArrayList<>(end - start);
        int index = 0;
        for (ITEM item : source) {
            if (index >= start && index < end) {
                result.add(item);
            }
            index++;
            if (index >= end) {
                break;
            }
        }
        return result;
    }

    /**
     * Reverse collection
     * @param source - source collection
     * @param <ITEM> - collection type
     * @return - reversed collection
     */
    public final static <ITEM> Collection<ITEM> reverse(Collection<ITEM> source) {
        final List<ITEM> result = new ArrayList<>(source);
        Collections.reverse(result);
        return result;
    }

    /**
     * Редуцировать коллекцию
     * @param source - исходная коллекция
     * @param defaultValue - начальное значение, от которого редуцируем
     * @param getter - функция получения значения из каждого элемента
     * @param reducer - редуктор
     * @param <ITEM> - тип элемента в коллекции
     * @param <VALUE> - тип выходного значения
     * @return
     */
    public static final <ITEM, VALUE> VALUE reduce(Collection<ITEM> source, VALUE defaultValue, Function<ITEM, VALUE> getter, BinaryFunction<VALUE, VALUE, VALUE> reducer) {
        VALUE value = defaultValue;
        for (ITEM item : source) {
            VALUE valueFromItem = getter.run(item);
            value = reducer.run(value, valueFromItem);
        }
        return value;
    }
}
