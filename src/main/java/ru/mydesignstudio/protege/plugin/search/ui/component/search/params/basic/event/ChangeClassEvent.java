package ru.mydesignstudio.protege.plugin.search.ui.component.search.params.basic.event;

import org.semanticweb.owlapi.model.OWLClass;
import ru.mydesignstudio.protege.plugin.search.api.Event;

/**
 * Created by abarmin on 10.01.17.
 */
public class ChangeClassEvent implements Event {
    private final OWLClass owlClass;
    private final int editingRow;

    public ChangeClassEvent(OWLClass owlClass, int editingRow) {
        this.owlClass = owlClass;
        this.editingRow = editingRow;
    }

    public OWLClass getOwlClass() {
        return owlClass;
    }

    public int getEditingRow() {
        return editingRow;
    }
}
