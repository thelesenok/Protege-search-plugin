package ru.mydesignstudio.protege.plugin.search.utils;

import org.junit.Test;
import ru.mydesignstudio.protege.plugin.search.utils.function.BinaryFunction;
import ru.mydesignstudio.protege.plugin.search.utils.function.Function;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
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

    @Test
    public void testSubcollection() throws Exception {
        final Collection<Integer> source = Arrays.asList(1, 2, 3, 4, 5);
        // create empty collection
        final Collection<Integer> subcollection = CollectionUtils.subcollection(source, 0, 0);
        assertEquals("Collection is not empty", 0, subcollection.size());
        // create collection with one element
        final Collection<Integer> singleElementCollection = CollectionUtils.subcollection(source, 0, 1);
        assertEquals("Collection contains more than one element", 1, singleElementCollection.size());
        // create collection with end behind collection size
        final Collection<Integer> oversizedCollection = CollectionUtils.subcollection(source, 2, 10);
        assertEquals("Collection contains more elements than expected", 3, oversizedCollection.size());
    }

    @Test
    public void testReverse() throws Exception {
        final Collection<Integer> source = Arrays.asList(1, 2, 3);
        final Collection<Integer> targetCollection = Arrays.asList(3, 2, 1);
        //
        final Collection<Integer> reversed = CollectionUtils.reverse(source);
        assertEquals("Collections are not equal", targetCollection, reversed);
    }
}