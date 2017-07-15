package ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part;

import org.semanticweb.owlapi.model.IRI;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataPropertyImpl;

/**
 * Created by abarmin on 26.06.17.
 *
 * Построитель объектов для тестов
 */
public class WherePartBuilder {
    private final WherePart wherePart;

    protected WherePartBuilder() {
        wherePart = new WherePart();
    }

    public static final WherePartBuilder builder() {
        return new WherePartBuilder();
    }

    public WherePartBuilder and() {
        wherePart.setConcatOperation(LogicalOperation.AND);
        return this;
    }

    public WherePartBuilder or() {
        wherePart.setConcatOperation(LogicalOperation.OR);
        return this;
    }

    public WherePartBuilder property(String propertyName, String className) {
        wherePart.setOwlClass(new OWLClassImpl(
                IRI.create("https://wiki.csc.calpoly.edu/OntologyTutorial/my_custom_prefix.owl#", className)
        ));
        return property(propertyName);
    }

    public WherePartBuilder property(String propertyName) {
        wherePart.setProperty(new OWLDataPropertyImpl(
                IRI.create("https://wiki.csc.calpoly.edu/OntologyTutorial/my_custom_prefix.owl#", propertyName)
        ));
        return this;
    }

    public WherePart build() {
        return wherePart;
    }

    public WherePartBuilder equalTo(String value) {
        wherePart.setLogicalOperation(LogicalOperation.EQUALS);
        wherePart.setValue(value);
        return this;
    }

    public WherePartBuilder like(String value) {
        wherePart.setLogicalOperation(LogicalOperation.LIKE);
        wherePart.setValue(value);
        return this;
    }

    public WherePartBuilder contains(String value) {
        wherePart.setLogicalOperation(LogicalOperation.CONTAINS);
        wherePart.setValue(value);
        return this;
    }

    public WherePartBuilder notEqualsTo(String value) {
        wherePart.setLogicalOperation(LogicalOperation.EQUALS_NOT);
        wherePart.setValue(value);
        return this;
    }

    public WherePartBuilder startsWith(String value) {
        wherePart.setLogicalOperation(LogicalOperation.STARTS_WITH);
        wherePart.setValue(value);
        return this;
    }

    public WherePartBuilder endsWith(String value) {
        wherePart.setLogicalOperation(LogicalOperation.ENDS_WITH);
        wherePart.setValue(value);
        return this;
    }

    public WherePartBuilder moreThan(int value) {
        wherePart.setLogicalOperation(LogicalOperation.MORE_THAN);
        wherePart.setValue(value);
        return this;
    }

    public WherePartBuilder moreOrEqualsTo(int value) {
        wherePart.setLogicalOperation(LogicalOperation.MORE_OR_EQUALS);
        wherePart.setValue(value);
        return this;
    }

    public WherePartBuilder lessThan(int value) {
        wherePart.setLogicalOperation(LogicalOperation.LESS_THAN);
        wherePart.setValue(value);
        return this;
    }

    public WherePartBuilder lessOrEqualsTo(int value) {
        wherePart.setLogicalOperation(LogicalOperation.LESS_OR_EQUALS);
        wherePart.setValue(value);
        return this;
    }
}
