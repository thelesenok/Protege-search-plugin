package ru.mydesignstudio.protege.plugin.search.utils.reducer;

import ru.mydesignstudio.protege.plugin.search.utils.function.BinaryFunction;

/**
 * Created by abarmin on 22.06.17.
 *
 * Редукторы для работы с числами
 */
public class NumericReducer {
    /**
     * Суммирующий редуктор для целых чисел
     */
    public static final BinaryFunction<Integer, Integer, Integer> SUM_INTEGER = new BinaryFunction<Integer, Integer, Integer>() {
        @Override
        public Integer run(Integer first, Integer second) {
            return first + second;
        }
    };
    /**
     * Суммирующий редуктор для чисел с плавающей запятой
     */
    public static final BinaryFunction<Double, Double, Double> SUM_DOUBLE = new BinaryFunction<Double, Double, Double>() {
        @Override
        public Double run(Double first, Double second) {
            return (double) first + second;
        }
    };
}
