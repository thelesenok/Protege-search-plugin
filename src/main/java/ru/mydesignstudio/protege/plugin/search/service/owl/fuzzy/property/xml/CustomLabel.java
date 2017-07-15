package ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.property.xml;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Created by abarmin on 26.06.17.
 *
 * Аннотация для учета веса свойств
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
@XmlRootElement(name = "customLabel")
public class CustomLabel {
    /**
     * Тип свойства
     */
    @XmlJavaTypeAdapter(CustomLabelTypeAdapter.class)
    @XmlAttribute(name = "type")
    private CustomLabelType type;
    /**
     * Вес атрибута
     */
    @XmlElement(name = "Priority", required = false)
    private Priority priority;

    public CustomLabelType getType() {
        return type;
    }

    public void setType(CustomLabelType type) {
        this.type = type;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}
