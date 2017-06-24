package ru.mydesignstudio.protege.plugin.search.utils;

import org.junit.Assert;
import org.junit.Test;
import ru.mydesignstudio.protege.plugin.search.utils.function.BinaryFunction;
import ru.mydesignstudio.protege.plugin.search.utils.function.Function;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by abarmin on 22.06.17.
 */
public class CollectionUtilsTest {
    @Test
    public void shouldReducerSumIntegerCollection() throws Exception {
        final Collection<Integer> items = Arrays.asList(1, 2, 3, 4, 5);
        final Integer reduced = CollectionUtils.reduce(items, 0, new Function<Integer, Integer>() {
            @Override
            public Integer run(Integer value) {
                return value;
            }
        }, new BinaryFunction<Integer, Integer, Integer>() {
            @Override
            public Integer run(Integer first, Integer second) {
                return first + second;
            }
        });
        Assert.assertTrue("Invalid reducer", 15 == reduced);
    }

}