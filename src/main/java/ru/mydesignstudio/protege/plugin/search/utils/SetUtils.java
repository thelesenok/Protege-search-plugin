package ru.mydesignstudio.protege.plugin.search.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Aleksandr_Barmin on 6/30/2017.
 */
public class SetUtils {
    /**
     * Конвертировать коллекцию во множество
     * @param source - исходная коллекция
     * @param <ITEM> - тип элемента множества
     * @return - множество из исходных элементов
     */
    public static final <ITEM> Set<ITEM> toSet(Collection<ITEM> source) {
        final Set<ITEM> result = new HashSet<>();
        for (ITEM item : source) {
            result.add(item);
        }
        return result;
    }
}
