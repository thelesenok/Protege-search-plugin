package ru.mydesignstudio.protege.plugin.search.utils.function;

/**
 * Created by abarmin on 22.06.17.
 *
 * Функция
 */
public interface Function<FROM, TO> {
    /**
     * Интерфейс преобразования входного значения в выходное
     * @param value
     * @return
     */
    TO run(FROM value);
}
