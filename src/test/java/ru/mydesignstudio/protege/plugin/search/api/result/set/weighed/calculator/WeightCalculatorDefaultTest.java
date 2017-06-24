package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.Weight;

/**
 * Created by abarmin on 24.06.17.
 */
public class WeightCalculatorDefaultTest {
    private WeightCalculator calculator;

    @Before
    public void setUp() throws Exception {
        calculator = new WeightCalculatorDefault();
    }

    @Test
    public void shouldNotCount() throws Exception {
        final Weight weight = Weight.noneWeight();
        weight.addWeight(Weight.noneWeight());
        weight.addWeight(Weight.noneWeight());
        //
        Assert.assertEquals("Counted nulled values", 0, calculator.calculate(weight), 0);
    }

    @Test
    public void shouldBeMaximal() throws Exception {
        final Weight weight = Weight.noneWeight();
        weight.addWeight(Weight.maxWeight());
        weight.addWeight(Weight.maxWeight());
        //
        Assert.assertEquals("Invalid calculated value", 1, calculator.calculate(weight), 0);
    }

    @Test
    public void shouldBeMinimal() throws Exception {
        final Weight weight = Weight.noneWeight();
        weight.addWeight(Weight.minWeight());
        weight.addWeight(Weight.minWeight());
        //
        Assert.assertEquals("Invalid calculated value", 0, calculator.calculate(weight), 0);
    }

    @Test
    public void shouldBeOneHalf() throws Exception {
        final Weight weight = Weight.noneWeight();
        weight.addWeight(Weight.maxWeight());
        weight.addWeight(Weight.minWeight());
        //
        Assert.assertEquals("Invalid calculated value", 0.5, calculator.calculate(weight), 0);
    }

    @Test
    public void shouldBeOneHalfWithoutNoneWeight() throws Exception {
        final Weight weight = Weight.maxWeight();
        weight.addWeight(Weight.noneWeight());
        weight.addWeight(Weight.minWeight());
        //
        Assert.assertEquals("Invalid calculated value", 0.5, calculator.calculate(weight), 0);
    }
}