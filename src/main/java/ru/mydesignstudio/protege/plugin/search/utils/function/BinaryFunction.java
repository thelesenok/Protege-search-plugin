package ru.mydesignstudio.protege.plugin.search.utils.function;

/**
 * Created by abarmin on 22.06.17.
 *
 * Функция двух аргументов
 */
public interface BinaryFunction<FIRST, SECOND, TO> {
    /**
     * Функция двух аргументов
     * @param first - первый аргумент
     * @param second - второй аргумент
     * @return
     */
    TO run(FIRST first, SECOND second);
}
