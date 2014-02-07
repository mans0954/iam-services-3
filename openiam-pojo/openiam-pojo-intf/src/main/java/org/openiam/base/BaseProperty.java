package org.openiam.base;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author zaporozhec
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BaseProperty", propOrder = { "name", "value", "attribute" })
public class BaseProperty implements Serializable {

    protected String name;
    protected String value;
    protected String attribute;

    private BaseProperty() {
        super();
    }

    private BaseProperty(String name, String value, String attribute) {
        super();
        this.name = name;
        this.value = value;
        this.attribute = attribute;
    }

    private BaseProperty(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
}
