package ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query.converter;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;

public class DateWherePartConverterTest {
  private WherePartConditionConverter<String> converter;
  
  @Before
  public void setUp() throws Exception {
    converter = new DateWherePartConverter();
  }

  @Test
  public void testEquals() throws Exception {
    final WherePart wherePart = new WherePart();
    wherePart.setLogicalOperation(LogicalOperation.EQUALS);
    wherePart.setValue("15.12.2017");
    //
    final String controlString = "((?var1 >= \"2017-12-15T00:00:00+00:00\"^^xsd:dateTime) && (?var1 <= \"2017-12-16T00:00:00+00:00\"^^xsd:dateTime))";
    final String conversionResult = converter.convert(wherePart, "15.12.2017", "?var1");
    assertEquals("Equals conversion check failed", controlString, conversionResult);
  }

}
