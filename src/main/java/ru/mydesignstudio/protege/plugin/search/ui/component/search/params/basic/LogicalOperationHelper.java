package ru.mydesignstudio.protege.plugin.search.ui.component.search.params.basic;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by abarmin on 05.01.17.
 */
public class LogicalOperationHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogicalOperationHelper.class);

    public static final Collection<LogicalOperation> getAvailableOperations(Collection<OWLPropertyRange> propertyRanges) {
        final Collection<LogicalOperation> operations = new ArrayList<>();
        for (OWLPropertyRange propertyRange : propertyRanges) {
            if (propertyRange instanceof OWLClassExpression) {
                operations.add(LogicalOperation.EQUALS);
            } else if (propertyRange instanceof OWLDatatype) {
                final OWLDatatype datatype = (OWLDatatype) propertyRange;
                if (datatype.isString()) {
                    operations.add(LogicalOperation.EQUALS);
                    operations.add(LogicalOperation.LIKE);
                    operations.add(LogicalOperation.STARTS_WITH);
                    operations.add(LogicalOperation.ENDS_WITH);
                    operations.add(LogicalOperation.EQUALS_NOT);
                } else if (datatype.isInteger()) {
                    operations.add(LogicalOperation.EQUALS);
                    operations.add(LogicalOperation.MORE_THAN);
                    operations.add(LogicalOperation.LESS_THAN);
                    operations.add(LogicalOperation.MORE_OR_EQUALS);
                    operations.add(LogicalOperation.LESS_OR_EQUALS);
                } else if (datatype.isDatatype()) {
                    operations.add(LogicalOperation.EQUALS);
                    operations.add(LogicalOperation.MORE_THAN);
                    operations.add(LogicalOperation.MORE_OR_EQUALS);
                    operations.add(LogicalOperation.LESS_THAN);
                    operations.add(LogicalOperation.LESS_OR_EQUALS);
                } else {
                    LOGGER.error("Unsupported datatype {}", datatype);
                    throw new RuntimeException("Unsupported datatype");
                }
            } else if (propertyRange instanceof OWLDataOneOf) {
                operations.add(LogicalOperation.EQUALS);
            }
        }
        return operations;
    }

    public static final boolean hasClassExpression(Collection<OWLPropertyRange> propertyRanges) {
        return CollectionUtils.every(propertyRanges, new Specification<OWLPropertyRange>() {
            @Override
            public boolean isSatisfied(OWLPropertyRange owlPropertyRange) {
                return (owlPropertyRange instanceof OWLClassExpression);
            }
        });
    }

    public static final boolean hasDateExpression(Collection<OWLPropertyRange> propertyRanges) {
        return CollectionUtils.every(propertyRanges, new Specification<OWLPropertyRange>() {
            @Override
            public boolean isSatisfied(OWLPropertyRange owlPropertyRange) {
                if (owlPropertyRange instanceof OWLDatatype) {
                    final OWLDatatype datatype = (OWLDatatype) owlPropertyRange;
                    return datatype.isDatatype();
                }
                return false;
            }
        });
    }

    public static final boolean hasStringExpression(Collection<OWLPropertyRange> propertyRanges) {
        return CollectionUtils.every(propertyRanges, new Specification<OWLPropertyRange>() {
            @Override
            public boolean isSatisfied(OWLPropertyRange owlPropertyRange) {
                if (owlPropertyRange instanceof OWLDatatype) {
                    final OWLDatatype datatype = (OWLDatatype) owlPropertyRange;
                    return datatype.isString();
                }
                return false;
            }
        });
    }

    public static final boolean hasIntegerExpression(Collection<OWLPropertyRange> propertyRanges) {
        return CollectionUtils.every(propertyRanges, new Specification<OWLPropertyRange>() {
            @Override
            public boolean isSatisfied(OWLPropertyRange owlPropertyRange) {
                if (owlPropertyRange instanceof OWLDatatype) {
                    final OWLDatatype datatype = (OWLDatatype) owlPropertyRange;
                    return datatype.isInteger();
                }
                return false;
            }
        });
    }

    public static final boolean hasEnumerationExpression(Collection<OWLPropertyRange> propertyRanges) {
        return CollectionUtils.every(propertyRanges, new Specification<OWLPropertyRange>() {
            @Override
            public boolean isSatisfied(OWLPropertyRange owlPropertyRange) {
                return (owlPropertyRange instanceof OWLDataOneOf);
            }
        });
    }
}
