package ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.property.xml;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by abarmin on 26.06.17.
 *
 * Значение веса атрибута
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
public class Priority {
    /**
     * Тип приоритета
     */
    @XmlAttribute(name = "type", required = true)
    private String type;
    /**
     * Значение веса атрибута
     */
    @XmlAttribute(name = "a", required = true)
    private double value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
