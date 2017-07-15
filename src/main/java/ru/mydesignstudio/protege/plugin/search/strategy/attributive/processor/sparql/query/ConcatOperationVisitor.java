package ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;

/**
 * Created by abarmin on 29.05.17.
 *
 * Визитор операций объединения условий запросов
 */
public interface ConcatOperationVisitor {
    /**
     * Посетить объединитель запросов
     * @param concatOperation - условие объединения
     * @return - строковое представление
     * @throws ApplicationException - если данный способ объединения не поддерживается
     */
    String visit(LogicalOperation concatOperation) throws ApplicationException;
}
