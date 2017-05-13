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
    @XmlElement(name = "Datatype")
    private Datatype datatype;

    public Datatype getDatatype() {
        return datatype;
    }

    public void setDatatype(Datatype datatype) {
        this.datatype = datatype;
    }
}
