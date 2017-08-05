package ru.mydesignstudio.protege.plugin.search.utils;

import org.junit.Assert;
import org.junit.Test;
import ru.mydesignstudio.protege.plugin.search.utils.function.BinaryFunction;
import ru.mydesignstudio.protege.plugin.search.utils.function.Function;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        assertTrue("Invalid reducer", 15 == reduced);
    }

    @Test
    public void testIsNotEmpty() throws Exception {
        final Collection<Integer> nonEmtpyCollection = Arrays.asList(1, 2, 3);
        assertTrue("Collection emptiness check failed", CollectionUtils.isNotEmpty(nonEmtpyCollection));
        assertFalse("Collection not emptiness check failed", CollectionUtils.isEmpty(nonEmtpyCollection));
        //
        final Collection<Integer> emptyCollection = Arrays.asList(null, null, null);
        assertTrue("Collection emptiness check failed", CollectionUtils.isEmpty(emptyCollection));
        assertFalse("Collection emptiness check failed", CollectionUtils.isNotEmpty(emptyCollection));
    }

    @Test
    public void testEvery() throws Exception {
        final Collection<Integer> collectionWithNull = Arrays.asList(1, 2, null, 3);
        assertFalse("Test for all items failed", CollectionUtils.every(collectionWithNull, new Specification<Integer>() {
            @Override
            public boolean isSatisfied(Integer integer) {
                return integer != null;
            }
        }));
        //
        final Collection<Integer> emptyCollection = Collections.emptyList();
        assertFalse("Empty collection test failed", CollectionUtils.every(emptyCollection, new Specification<Integer>() {
            @Override
            public boolean isSatisfied(Integer integer) {
                return integer != null;
            }
        }));
    }
}