package ru.mydesignstudio.protege.plugin.search.utils;

import ru.mydesignstudio.protege.plugin.search.utils.function.BinaryFunction;
import ru.mydesignstudio.protege.plugin.search.utils.function.Function;

import java.util.ArrayList;
import java.util.Collection;

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
        return some(source, new Specification<ITEM>() {
            @Override
            public boolean isSatisfied(ITEM item) {
                return (item != null);
            }
        });
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
        boolean result = true;
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
