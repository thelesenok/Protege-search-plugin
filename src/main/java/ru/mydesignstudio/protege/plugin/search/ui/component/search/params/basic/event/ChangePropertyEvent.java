package ru.mydesignstudio.protege.plugin.search.ui.component.search.params.basic.event;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.Event;

/**
 * Created by abarmin on 10.01.17.
 */
public class ChangePropertyEvent implements Event {
    private final OWLClass owlClass;
    private final OWLProperty property;
    private final int editingRow;

    public ChangePropertyEvent(OWLClass owlClass, OWLProperty property, int editingRow) {
        this.owlClass = owlClass;
        this.property = property;
        this.editingRow = editingRow;
    }

    public OWLClass getOwlClass() {
        return owlClass;
    }

    public OWLProperty getProperty() {
        return property;
    }

    public int getEditingRow() {
        return editingRow;
    }
}
