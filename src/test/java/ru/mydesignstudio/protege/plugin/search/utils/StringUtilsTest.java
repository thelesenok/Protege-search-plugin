package ru.mydesignstudio.protege.plugin.search.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by abarmin on 28.06.17.
 */
public class StringUtilsTest {
    @Test
    public void substringBeforeLast() throws Exception {
        final String prepared = "aaaa|bbb";
        final String parsed = StringUtils.substringBeforeLast(prepared, "|");
        //
        Assert.assertEquals("Substring extraction exception", "aaaa", parsed);
    }

    @Test
    public void substringAfterLast() throws Exception {
        final String prepared = "aaaa|bbb";
        final String parsed = StringUtils.substringAfterLast(prepared, "|");
        //
        Assert.assertEquals("Substring extraction exception", "bbb", parsed);
    }
}