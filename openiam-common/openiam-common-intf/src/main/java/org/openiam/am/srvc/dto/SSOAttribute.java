package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.constants.SsoAttributeType;
import org.openiam.base.KeyNameDTO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * User: Alexander Duckardt
 * Date: 8/16/12
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SSOAttribute", propOrder = {
        "attributeValue",
        "attributeType"
})
public class SSOAttribute extends KeyNameDTO implements Comparable<SSOAttribute> {
    private String attributeValue;
    private SsoAttributeType attributeType;

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    @Override
    public int compareTo(SSOAttribute o) {
        return this.getName().compareTo(o.getName());
    }

    public SsoAttributeType getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(SsoAttributeType attributeType) {
        this.attributeType = attributeType;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("SSOAttribute");
        sb.append("{name=").append(getName());
        sb.append(", attributeValue=").append(attributeValue);
        sb.append(", attributeType=").append(attributeType);
        sb.append('}');
        return sb.toString();
    }
}
