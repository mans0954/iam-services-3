package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.constants.SsoAttributeType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * User: Alexander Duckardt
 * Date: 8/16/12
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SSOAttribute", propOrder = {
        "targetAttributeName",
        "attributeValue",
        "attributeType"
})
public class SSOAttribute implements Comparable<SSOAttribute> {
    private String targetAttributeName;
    private String attributeValue;
    private SsoAttributeType attributeType;

    public String getTargetAttributeName() {
        return targetAttributeName;
    }

    public void setTargetAttributeName(String targetAttributeName) {
        this.targetAttributeName = targetAttributeName;
    }
    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    @Override
    public int compareTo(SSOAttribute o) {
        return this.targetAttributeName.compareTo(o.targetAttributeName);
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
        sb.append("{targetAttributeName=").append(targetAttributeName);
        sb.append(", attributeValue=").append(attributeValue);
        sb.append(", attributeType=").append(attributeType);
        sb.append('}');
        return sb.toString();
    }
}
