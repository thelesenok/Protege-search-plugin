package ru.mydesignstudio.protege.plugin.search.ui.model;

import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

/**
 * Created by abarmin on 04.01.17.
 */
public class OWLUIProperty implements OWLUIObject {
    private final OWLProperty owlProperty;

    public OWLUIProperty(OWLProperty owlProperty) {
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
