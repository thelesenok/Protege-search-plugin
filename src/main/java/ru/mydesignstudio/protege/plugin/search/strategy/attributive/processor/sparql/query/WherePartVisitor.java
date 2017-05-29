package ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query;

import ru.mydesignstudio.protege.plugin.search.api.common.Triplet;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.FromType;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectField;

/**
 * Created by abarmin on 07.01.17.
 *
 * Визитор для поля. Вроде как раньше это был визитор
 * для WherePart, поэтому сейчас он так странно называется
 */
public interface WherePartVisitor {
    /**
     * Посетить условие/поле
     * @param fromType - тип, по которому происходит отбор
     * @param wherePart - условие/поле для отбора
     * @return - трока вида <название поля, условие отбора, конкатенатор с предыдущим>
     * @throws ApplicationException - если формат данных не может быть сконвертирован в строку, обычно
     */
    Triplet<String, String, LogicalOperation> visit(FromType fromType, SelectField wherePart) throws ApplicationException;
}
