package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row;

import javax.inject.Inject;

import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.binding.CalculatorEndsWith;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.binding.CalculatorEquals;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.binding.CalculatorFuzzyLike;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.binding.CalculatorLessOrEqulas;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.binding.CalculatorLessThan;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.binding.CalculatorLike;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.binding.CalculatorMoreOrEquals;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.binding.CalculatorMoreThan;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.binding.CalculatorNotEquals;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.binding.CalculatorStartsWith;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;

/**
 * Created by abarmin on 08.05.17.
 *
 * Фабрика классов для вычисления близости
 */
@Component
public class ProximityCalculatorFactory {
	private final ProximityCalculator equalsCalculator;
	private final ProximityCalculator fuzzyLikeCalculator;
	private final ProximityCalculator startsWithCalculator;
	private final ProximityCalculator endsWithCalculator;
	private final ProximityCalculator likeCalculator;
	private final ProximityCalculator notEqualsCalculator;
	private final ProximityCalculator moreThanCalculator;
	private final ProximityCalculator moreOrEqualsCalculator;
	private final ProximityCalculator lessThanCalculator;
	private final ProximityCalculator lessOrEqualsCalculator;
	
	@Inject
	public ProximityCalculatorFactory(
			@CalculatorEquals ProximityCalculator equalsCalculator,
			@CalculatorFuzzyLike ProximityCalculator fuzzyLikeCalculator, 
			@CalculatorStartsWith ProximityCalculator startsWithCalculator,
			@CalculatorEndsWith ProximityCalculator endsWithCalculator, 
			@CalculatorLike ProximityCalculator likeCalculator,
			@CalculatorNotEquals ProximityCalculator notEqualsCalculator, 
			@CalculatorMoreThan ProximityCalculator moreThanCalculator,
			@CalculatorMoreOrEquals ProximityCalculator moreOrEqualsCalculator, 
			@CalculatorLessThan ProximityCalculator lessThanCalculator,
			@CalculatorLessOrEqulas ProximityCalculator lessOrEqualsCalculator) {
		
		this.equalsCalculator = equalsCalculator;
		this.fuzzyLikeCalculator = fuzzyLikeCalculator;
		this.startsWithCalculator = startsWithCalculator;
		this.endsWithCalculator = endsWithCalculator;
		this.likeCalculator = likeCalculator;
		this.notEqualsCalculator = notEqualsCalculator;
		this.moreThanCalculator = moreThanCalculator;
		this.moreOrEqualsCalculator = moreOrEqualsCalculator;
		this.lessThanCalculator = lessThanCalculator;
		this.lessOrEqualsCalculator = lessOrEqualsCalculator;
	}

	/**
     * Получить калькулятор для указанной логической операции
     * @param operation - для какой логической операции нужен калькулятор
     * @param params - дополнительные параметры калькулятора
     * @return - калькулятор
     * @throws ApplicationException
     */
    public ProximityCalculator getCalculator(LogicalOperation operation, SearchProcessorParams params) throws ApplicationException {
        if (LogicalOperation.EQUALS.equals(operation)) {
            return equalsCalculator;
        }
        if (LogicalOperation.FUZZY_LIKE.equals(operation)) {
            return fuzzyLikeCalculator;
        }
        if (LogicalOperation.STARTS_WITH.equals(operation)) {
            return startsWithCalculator;
        }
        if (LogicalOperation.ENDS_WITH.equals(operation)) {
            return endsWithCalculator;
        }
        if (LogicalOperation.LIKE.equals(operation)) {
            return likeCalculator;
        }
        if (LogicalOperation.EQUALS_NOT.equals(operation)) {
            return notEqualsCalculator;
        }
        if (LogicalOperation.MORE_THAN.equals(operation)) {
            return moreThanCalculator;
        }
        if (LogicalOperation.MORE_OR_EQUALS.equals(operation)) {
            return moreOrEqualsCalculator;
        }
        if (LogicalOperation.LESS_THAN.equals(operation)) {
            return lessThanCalculator;
        }
        if (LogicalOperation.LESS_OR_EQUALS.equals(operation)) {
            return lessOrEqualsCalculator;
        }
        throw new ApplicationException(String.format(
                "Can't find calculator for %s operation",
                operation
        ));
    }
}
