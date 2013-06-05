package org.openiam.am.srvc.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Attribute", propOrder = {
        "targetAttributeName",
        "attributeValue"
})
@Deprecated
public class Attribute implements Comparable<Attribute> {
    private String targetAttributeName;
    private String attributeValue;

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
    public int compareTo(Attribute o) {
        return this.targetAttributeName.compareTo(o.targetAttributeName);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("SSOAttribute");
        sb.append("{targetAttributeName=").append(targetAttributeName);
        sb.append(", attributeValue=").append(attributeValue);
        sb.append('}');
        return sb.toString();
    }
}
