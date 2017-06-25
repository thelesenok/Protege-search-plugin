package ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.xml;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by abarmin on 13.05.17.
 *
 * Класс для анмаршаллинга XML-описания функций принадлежности
 */
@XmlRootElement(name = "fuzzyOwl2")
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
@XmlAccessorType(XmlAccessType.FIELD)
public class FuzzyOWL2 {
    /**
     * Тип функции принадлежности
     */
    @XmlElement(name = "Datatype", required = false)
    private Datatype datatype;
    /**
     * Схожие классы с указанной заранее мерой сходства
     */
    @XmlElement(name = "Concept", required = false)
    private Concept concept;

    public Concept getConcept() {
        return concept;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    public Datatype getDatatype() {
        return datatype;
    }

    public void setDatatype(Datatype datatype) {
        this.datatype = datatype;
    }
}
