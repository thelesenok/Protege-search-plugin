package ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import ru.mydesignstudio.protege.plugin.search.api.common.Pair;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by abarmin on 26.06.17.
 */
@RunWith(MockitoJUnitRunner.class)
public class WherePartsCollectionSwrlConverterTest {
    private WherePartsCollectionSwrlConverter converter;
    @Mock
    private WherePartSwrlConverter wherePartConverter;

    @Before
    public void setUp() throws Exception {
        converter = new WherePartsCollectionSwrlConverter(wherePartConverter);
        /**
         * Мок ничего не конвертирует, он возвращает заранее заготовленный текст
         */
        Mockito.doAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                return "^ MOCK";
            }
        }).when(wherePartConverter).convert(Mockito.any(Pair.class), Mockito.anyInt());
    }

    @Test
    public void testIsEmpty() throws Exception {
        final String swrl = converter.convert(new Pair<>(null, Collections.emptyList()));
        Assert.assertEquals("Fail empty collection", "", swrl);
    }

    @Test
    public void testSingleWherePart() throws Exception {
        final WherePart wherePart = new WherePart();
        final String swrl = converter.convert(new Pair<>(null, Collections.singletonList(wherePart)));
        //
        Assert.assertEquals("Fail single part conversion", "^ MOCK", swrl);
    }

    @Test
    public void testMultipleWhereParts() throws Exception {
        final Collection<WherePart> parts = Arrays.asList(
                new WherePart(),
                new WherePart()
        );
        final String swrl = converter.convert(new Pair<>(null, parts));
        //
        Assert.assertEquals("Fail multi part conversion", "^ MOCK ^ MOCK", swrl);
    }
}