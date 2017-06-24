package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology.processor.result.set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.WeighedResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.WeighedRow;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.FuzzyOWLService;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrappedCallback;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query.SparqlQueryVisitor;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by abarmin on 28.05.17.
 */
public class FuzzyWeighedResultSet extends WeighedResultSet {
    private final OWLService owlService;
    private final FuzzyOWLService fuzzyOWLService;
    private final ExceptionWrapperService wrapperService;
    private final SelectQuery fuzzyQuery;
    private final Map<OWLProperty, OWLDatatype> fuzzyValues;

    public FuzzyWeighedResultSet(ResultSet resultSet,
                                 WeighedRowWeightCalculator weightCalculator,
                                 SelectQuery selectQuery,
                                 Map<OWLProperty, OWLDatatype> values) {
        super(resultSet, weightCalculator);
        //
        this.fuzzyQuery = selectQuery;
        this.fuzzyValues = values;
        //
        owlService = InjectionUtils.getInstance(OWLService.class);
        fuzzyOWLService = InjectionUtils.getInstance(FuzzyOWLService.class);
        wrapperService = InjectionUtils.getInstance(ExceptionWrapperService.class);
        /**
         * После того, как добавили все записи из исходного набора
         * данных остави там только те, которые подходят по условиям запроса
         */
        final Iterator<WeighedRow> rowIterator = getRows().iterator();
        while (rowIterator.hasNext()) {
            final WeighedRow weighedRow = rowIterator.next();
            if (!isValidFuzzyRow(weighedRow)) {
                rowIterator.remove();
            }
        }
        /**
         * Пройдем по оставшимся записям и вычислим новый вес
         */
        wrapperService.invokeWrapped(new ExceptionWrappedCallback<Void>() {
            @Override
            public Void run() throws ApplicationException {
                for (WeighedRow weighedRow : getRows()) {
                    weighedRow.setWeight(getRowWeight(weighedRow));
                }
                return null;
            }
        });
    }

    protected boolean isValidFuzzyRow(WeighedRow row) {
        return wrapperService.invokeWrapped(new ExceptionWrappedCallback<Boolean>() {
            @Override
            public Boolean run() throws ApplicationException {
                final IRI recordIRI = (IRI) row.getCell(SparqlQueryVisitor.OBJECT);
                final OWLIndividual record = owlService.getIndividual(recordIRI);
                return isValidFuzzyRow(
                        fuzzyQuery.getFrom().getOwlClass(),
                        record,
                        fuzzyValues
                );
            }
        });
    }

    /**
     * Подходит ли запись из результатов
     * @param recordClass - класс записи, который отбираем
     * @param record - запись, которую проверяем
     * @param values - набор нечетких свойств
     * @return
     * @throws ApplicationException
     */
    private boolean isValidFuzzyRow(OWLClass recordClass, OWLIndividual record, Map<OWLProperty, OWLDatatype> values) throws ApplicationException {
        /**
         * пройдем по всем свойствам, если свойство есть в fuzzyValues и тип совпадает,
         * то все подходит
         */
        final Collection<OWLDataProperty> properties = owlService.getDataProperties(recordClass);
        return CollectionUtils.every(values.entrySet(), new Specification<Map.Entry<OWLProperty, OWLDatatype>>() {
            @Override
            public boolean isSatisfied(Map.Entry<OWLProperty, OWLDatatype> entry) {
                final OWLProperty property = entry.getKey();
                final OWLDatatype fuzzyType = entry.getValue();
                if (fuzzyType == null) {
                    return false;
                }
                if (properties.contains(property)) {
                    final OWLDatatype propertyType = wrapperService.invokeWrapped(new ExceptionWrappedCallback<OWLDatatype>() {
                        @Override
                        public OWLDatatype run() throws ApplicationException {
                            return fuzzyOWLService.getPropertyDatatype(record, property);
                        }
                    });
                    return fuzzyType.equals(propertyType);
                }
                return false;
            }
        });
    }
}
