package ru.mydesignstudio.protege.plugin.search.utils;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationRuntimeException;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.FuzzyOWLService;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by abarmin on 05.01.17.
 */
public class LogicalOperationHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogicalOperationHelper.class);

    public static final Collection<LogicalOperation> getAvailableOperations(Collection<OWLPropertyRange> propertyRanges) throws ApplicationException {
        final Collection<LogicalOperation> operations = new HashSet<>();
        for (OWLPropertyRange propertyRange : propertyRanges) {
            if (propertyRange instanceof OWLClassExpression) {
                operations.add(LogicalOperation.EQUALS);
            } else if (propertyRange instanceof OWLDatatype) {
                if (hasStringExpression(propertyRange)) {
                    operations.add(LogicalOperation.EQUALS);
                    operations.add(LogicalOperation.LIKE);
                    operations.add(LogicalOperation.STARTS_WITH);
                    operations.add(LogicalOperation.ENDS_WITH);
                    operations.add(LogicalOperation.EQUALS_NOT);
                } else if (hasNumericExpression(propertyRange)) {
                    operations.add(LogicalOperation.EQUALS);
                    operations.add(LogicalOperation.MORE_THAN);
                    operations.add(LogicalOperation.LESS_THAN);
                    operations.add(LogicalOperation.MORE_OR_EQUALS);
                    operations.add(LogicalOperation.LESS_OR_EQUALS);
                } else if (hasDateExpression(propertyRange)) {
                    operations.add(LogicalOperation.EQUALS);
                    operations.add(LogicalOperation.MORE_THAN);
                    operations.add(LogicalOperation.MORE_OR_EQUALS);
                    operations.add(LogicalOperation.LESS_THAN);
                    operations.add(LogicalOperation.LESS_OR_EQUALS);
                } else if (hasBooleanExpression(propertyRange)) {
                		operations.add(LogicalOperation.EQUALS);
                		operations.add(LogicalOperation.EQUALS_NOT);
                } else if (hasFuzzyExpression(propertyRange)) {
                    operations.add(LogicalOperation.EQUALS);
                } else {
                    LOGGER.error("Unsupported datatype {}", propertyRange);
                    throw new ApplicationException("Unsupported datatype");
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
    
    /**
     * Does property range contains boolean type
     * @param propertyRange range to check
     * @return is contains
     */
    public static final boolean hasBooleanExpression(OWLPropertyRange propertyRange) {
    		if (propertyRange instanceof OWLDatatype) {
    			final OWLDatatype datatype = (OWLDatatype) propertyRange;
    			return datatype.isBoolean();
    		}
    		return false;
    }
    
    /**
     * Does range contains date data type
     * @param propertyRange range to check
     * @return is contains
     */
    public static final boolean hasDateExpression(OWLPropertyRange propertyRange) {
    		if (propertyRange instanceof OWLDatatype) {
            final OWLDatatype datatype = (OWLDatatype) propertyRange;
            return StringUtils.equalsIgnoreCase(
            		"dateTime",
            		datatype.toString()
            		);
        }
        return false;
    }

    /**
     * Does collection of ranges contains date expression
     * @param propertyRanges collection of ranges to check
     * @return is collection contains date expression
     */
    public static final boolean hasDateExpression(Collection<OWLPropertyRange> propertyRanges) {
        return CollectionUtils.every(propertyRanges, new Specification<OWLPropertyRange>() {
            @Override
            public boolean isSatisfied(OWLPropertyRange owlPropertyRange) {
            		return hasDateExpression(owlPropertyRange);
            }
        });
    }
    
    /**
     * If property range is string data type
     * @param propertyRange property range to check
     * @return is string type
     */
    public static final boolean hasStringExpression(OWLPropertyRange propertyRange) {
		if (propertyRange instanceof OWLDatatype) {
			final OWLDatatype datatype = (OWLDatatype) propertyRange;
			return datatype.isString();
    		}
		return false;
    }

    /**
     * If collection of property ranges contains string data type
     * @param propertyRanges set of property ranges to check
     * @return contains string type
     */
    public static final boolean hasStringExpression(Collection<OWLPropertyRange> propertyRanges) {
        return CollectionUtils.every(propertyRanges, new Specification<OWLPropertyRange>() {
            @Override
            public boolean isSatisfied(OWLPropertyRange owlPropertyRange) {
                return hasStringExpression(owlPropertyRange);
            }
        });
    }
    
    /**
     * Does property range is numeric type like integer, float of double
     * @param propertyRange range to check
     * @return is numeric range
     */
    public static final boolean hasNumericExpression(OWLPropertyRange propertyRange) {
		if (propertyRange instanceof OWLDatatype) {
			final OWLDatatype datatype = (OWLDatatype) propertyRange;
			return datatype.isInteger()
					|| datatype.isDouble()
					|| datatype.isFloat();
		}
		return false;
    }

    /**
     * Does designated property ranges set contains numeric data types
     * @param propertyRanges set of property ranges
     * @return contains numeric type
     */
    public static final boolean hasNumericExpression(Collection<OWLPropertyRange> propertyRanges) {
    	return CollectionUtils.every(propertyRanges, new Specification<OWLPropertyRange>() {
    		@Override
    		public boolean isSatisfied(OWLPropertyRange owlPropertyRange) {
    			return hasNumericExpression(owlPropertyRange);
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

    private static boolean isFuzzyDatatype(OWLDatatype datatype) {
        final FuzzyOWLService owlService = InjectionUtils.getInstance(FuzzyOWLService.class);
        try {
            return owlService.isFuzzyDatatype(datatype);
        } catch (ApplicationException e) {
            throw new ApplicationRuntimeException(e);
        }
    }
    
    /**
     * Does property range is fuzzy data type
     * @param propertyRange range to check
     * @return is fuzzy type
     */
    public static final boolean hasFuzzyExpression(OWLPropertyRange propertyRange) {
    		// TODO: 13.05.17 как-то иначе, наверное, можно это проверять
        return (propertyRange instanceof OWLDatatype)
                && isFuzzyDatatype((OWLDatatype) propertyRange);
    }

    /**
     * Does set of ranges contains fuzzy types
     * @param ranges ranges to check
     * @return contains fuzzy types
     */
    public static final boolean hasFuzzyExpression(Collection<OWLPropertyRange> ranges) {
        return CollectionUtils.some(ranges, new Specification<OWLPropertyRange>() {
            @Override
            public boolean isSatisfied(OWLPropertyRange range) {
            		return hasFuzzyExpression(range);
            }
        });
    }

    /**
     * Does collection of ranges contains boolean types
     * @param ranges collection of ranges to check
     * @return is contains
     */
	public static boolean hasBooleanExpression(Collection<OWLPropertyRange> ranges) {
		return CollectionUtils.some(ranges, new Specification<OWLPropertyRange>() {
			@Override
			public boolean isSatisfied(OWLPropertyRange item) {
				return hasBooleanExpression(item);
			}
		});
	}
}
