package ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.FromType;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectField;

/**
 * Created by abarmin on 07.01.17.
 */
public interface SelectFieldVisitor {
    String visit(FromType fromType, SelectField wherePart) throws ApplicationException;
}
