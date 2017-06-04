package ru.mydesignstudio.protege.plugin.search.utils;

/**
 * Created by abarmin on 04.06.17.
 *
 * Служебные методы по работе с типами данных
 */
public class DataTypeUtils {
    /**
     * Имеет ли переданное значение тип Integer
     * @param value - значение для проверки
     * @return
     */
    public static final boolean isInteger(Object value) {
        return Integer.class == value.getClass();
    }
}
