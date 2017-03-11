package ru.mydesignstudio.protege.plugin.search.service.sparql.query.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by abarmin on 11.03.17.
 */
public class DateWherePartConverter implements WherePartConditionConverter<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateWherePartConverter.class);
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T00:00:00+00:00'";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);
    private static final String SOURCE_FORMAT_PATTERN = "dd.mm.yyyy";
    private static final SimpleDateFormat SOURCE_DATE_FORMAT = new SimpleDateFormat(SOURCE_FORMAT_PATTERN);

    @Override
    public String convert(WherePart wherePart, String value, String variableName) {
        final LogicalOperation operation = wherePart.getLogicalOperation();
        final StringBuilder builder = new StringBuilder();
        final Date sourceDate;
        final Date nextDayDate;
        try {
            sourceDate = SOURCE_DATE_FORMAT.parse(value);
            final Calendar calendar = new GregorianCalendar(sourceDate.getYear(),
                    sourceDate.getMonth(),
                    sourceDate.getDay());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            nextDayDate = calendar.getTime();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        builder.append("FILTER(");
        if (LogicalOperation.EQUALS.equals(operation)) {
            builder.append("(");
            builder.append(variableName + " >= \"" + DATE_FORMAT.format(sourceDate) + "\"^^xsd:dateTime");
            builder.append(") && (");
            builder.append(variableName + " <= \"" + DATE_FORMAT.format(nextDayDate) + "\"^^xsd:dateTime");
            builder.append(")");
        } else if (LogicalOperation.MORE_THAN.equals(operation)) {
            builder.append(variableName + " > \"" + DATE_FORMAT.format(sourceDate) + "\"^^xsd:dateTime");
        } else if (LogicalOperation.MORE_OR_EQUALS.equals(operation)) {
            builder.append(variableName + " >= \"" + DATE_FORMAT.format(sourceDate) + "\"^^xsd:dateTime");
        } else if (LogicalOperation.LESS_THAN.equals(operation)) {
            builder.append(variableName + " < \"" + DATE_FORMAT.format(sourceDate) + "\"^^xsd:dateTime");
        } else if (LogicalOperation.LESS_OR_EQUALS.equals(operation)) {
            builder.append(variableName + " <= \"" + DATE_FORMAT.format(sourceDate) + "\"^^xsd:dateTime");
        } else {
            LOGGER.error("Unsupported operation {}", operation);
            throw new RuntimeException(String.format(
                    "Unsupported operation %s",
                    operation
            ));
        }
        builder.append(")");
        return builder.toString();
    }
}
