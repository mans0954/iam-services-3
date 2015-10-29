package org.openiam.provision.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by zaporozhec on 10/29/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SourceAdapterKey {
    @XmlElement(required = true)
    SourceAdapterKeyEnum name;
    @XmlElement(required = true)
    String value;

    public SourceAdapterKeyEnum getName() {
        return name;
    }

    public void setName(SourceAdapterKeyEnum name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
