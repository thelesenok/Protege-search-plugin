package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.Weight;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.FuzzyOWLService;
import uk.ac.manchester.cs.owl.owlapi.OWLDataPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

@RunWith(MockitoJUnitRunner.class)
public class ProximityCalculatorLikeTest {
	@Mock
	private OWLService owlService;
	@Mock
	private FuzzyOWLService fuzzyOwlService;
	private final OWLIndividual individual = new OWLNamedIndividualImpl(IRI.create("prefix", "John Smith"));
	@SuppressWarnings("rawtypes")
	private final OWLProperty property = new OWLDataPropertyImpl(IRI.create("prefix", "fullName"));
	
	private ProximityCalculator calculator;

	@Before
	public void setUp() throws Exception {
		calculator = spy(new ProximityCalculatorLike(owlService, fuzzyOwlService));
		//
		final ProximityCalculatorSupport calculatorSupport = (ProximityCalculatorSupport) calculator;
		doReturn("Tomas Smith").when(calculatorSupport).getPropertyAsString(individual, property);
	}

	@Test
	public void testLikeCalculate() throws ApplicationException {
		final Weight proximityValue = calculator.calculate("Smith", individual, property, false);
		final Weight targetValue = new Weight(
				(double) "Smith".length() / "Tomas Smith".length()
				, 1);
		assertEquals("Wrong proximity calculation", targetValue, proximityValue);
	}
	
	@Test
	public void testCalculatorSupportWithCommonCharacters() throws ApplicationException {
		final ProximityCalculatorSupport support = (ProximityCalculatorSupport) calculator;
		//
		assertEquals("Failure on common characters check", 0, support.getCommonSymbols("Not found", "Here"));
		assertEquals("Failure on common characters check", 1, support.getCommonSymbols("Source string", "i"));
		assertEquals("Failure on common characters check", 3, support.getCommonSymbols("String", "str"));
	}
	
	@Test
	public void testCalculatorSupportWithSeveralIntersections() throws ApplicationException {
		final ProximityCalculatorSupport support = (ProximityCalculatorSupport) calculator;
		//
		assertEquals("Failure on common characters check", 6, support.getCommonSymbols("String string", "str"));
	}
	
	@Test
	public void testCalculatorSupportSingleSumbol() throws ApplicationException {
		final ProximityCalculatorSupport support = (ProximityCalculatorSupport) calculator;
		//
		assertEquals("Failure on common characters check", 1, support.getCommonSymbols("sssss", "str"));
	}
}
