package ru.mydesignstudio.protege.plugin.search.service.search.path;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

/**
 * Доступный путь между двумя вершинами
 *
 * Created by Aleksandr_Barmin on 6/30/2017.
 */
public class VertexPath {
    /**
     * Из какой вершины
     */
    private OWLClass fromClass;
    /**
     * В какую вершину
     */
    private OWLClass toPath;
    /**
     * Через какое свойство
     */
    private OWLObjectProperty property;

    protected VertexPath() {

    }

    public static final VertexPathBuilder builder() {
        return new VertexPath().new VertexPathBuilder();
    }

    public OWLClass getFromClass() {
        return fromClass;
    }

    public void setFromClass(OWLClass fromClass) {
        this.fromClass = fromClass;
    }

    public OWLClass getToPath() {
        return toPath;
    }

    public void setToPath(OWLClass toPath) {
        this.toPath = toPath;
    }

    public OWLObjectProperty getProperty() {
        return property;
    }

    public void setProperty(OWLObjectProperty property) {
        this.property = property;
    }

    public class VertexPathBuilder {
        public VertexPathBuilder from(String className) {
            VertexPath.this.setFromClass(new OWLClassImpl(
                    IRI.create("dummy", className)
            ));
            return this;
        }

        public VertexPathBuilder to(String className) {
            VertexPath.this.setToPath(new OWLClassImpl(
                    IRI.create("dummy", className)
            ));
            return this;
        }

        public VertexPathBuilder through(String propertyName) {
            VertexPath.this.setProperty(new OWLObjectPropertyImpl(
                    IRI.create("dummy", propertyName)
            ));
            return this;
        }

        public VertexPath build() {
            return VertexPath.this;
        }
    }
}
