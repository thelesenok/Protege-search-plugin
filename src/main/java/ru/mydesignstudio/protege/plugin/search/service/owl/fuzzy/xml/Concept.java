package ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.xml;

import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.related.ConceptType;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.related.ConceptTypeAdapter;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by abarmin on 25.06.17.
 *
 * Описание схожего класса с указанной заранее мерой сходства
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
public class Concept {
    /**
     * Тип схожести - только один класс или сразу несколько
     */
    @XmlAttribute(required = true, name = "type")
    @XmlJavaTypeAdapter(ConceptTypeAdapter.class)
    private ConceptType type;
    /**
     * Степень схожести
     */
    @XmlAttribute(required = false)
    private Double value;
    /**
     * На какой класс похоже
     */
    @XmlAttribute(required = false)
    private String base;
    /**
     * Дочерние
     */
    @XmlElement(name = "Concept")
    private Collection<Concept> children = new ArrayList<>();

    public ConceptType getType() {
        return type;
    }

    public void setType(ConceptType type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Collection<Concept> getChildren() {
        return children;
    }

    public void setChildren(Collection<Concept> children) {
        this.children = children;
    }
}
