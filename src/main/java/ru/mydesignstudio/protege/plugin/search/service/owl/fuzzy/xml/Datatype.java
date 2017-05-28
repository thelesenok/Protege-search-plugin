package ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.xml;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by abarmin on 13.05.17.
 */
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
@XmlAccessorType(XmlAccessType.FIELD)
public class Datatype {
    @XmlAttribute(name = "type")
    private String type;
    @XmlAttribute(name = "a")
    private double a;
    @XmlAttribute(name = "b")
    private double b;
    @XmlAttribute(name = "c")
    private double c;
    @XmlAttribute(name = "d")
    private double d;

    public Datatype() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }
}
