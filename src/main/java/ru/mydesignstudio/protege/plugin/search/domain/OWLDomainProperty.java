package ru.mydesignstudio.protege.plugin.search.domain;

import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

/**
 * Created by abarmin on 04.01.17.
 */
public class OWLDomainProperty implements OWLDomainObject {
    private final OWLProperty owlProperty;

    public OWLDomainProperty(OWLProperty owlProperty) {
        this.owlProperty = owlProperty;
    }

    public OWLProperty getOwlProperty() {
        return owlProperty;
    }

    @Override
    public String getQuotedString() {
        return getOwlProperty().getIRI().toQuotedString();
    }

    @Override
    public String toString() {
        return StringUtils.substringAfter(
                owlProperty.toStringID(),
                "#"
        );
    }
}
