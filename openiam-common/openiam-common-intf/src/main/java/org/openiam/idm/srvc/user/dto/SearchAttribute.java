package org.openiam.idm.srvc.user.dto;

import org.hibernate.criterion.MatchMode;
import org.openiam.base.ws.MatchType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by IntelliJ IDEA.
 * User: suneetshah
 * Date: 4/26/11
 * Time: 2:46 PM
 * To change this template use File | Settings | File Templates.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchAttribute", propOrder = {
        "attributeName",
        "attributeValue",
        "attributeElementId",
        "matchType"
})
public class SearchAttribute {
    private String attributeName;
    private String attributeValue;
    private String attributeElementId;
    private MatchType matchType = MatchType.EXACT;

    public SearchAttribute() {
    }

    public SearchAttribute(final String name, final String value) {
        this(name, value, null);
    }

    public SearchAttribute(final String name, final String value, final String attributeElementId) {
        this.attributeName = name;
        this.attributeValue = value;
        this.attributeElementId = attributeElementId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getAttributeElementId() {
        return attributeElementId;
    }

    public void setAttributeElementId(String attributeElementId) {
        this.attributeElementId = attributeElementId;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    @Override
    public String toString() {
        return "SearchAttribute{" +
                "attributeName='" + attributeName + '\'' +
                ", attributeValue='" + attributeValue + '\'' +
                ", attributeElementId='" + attributeElementId + '\'' +
                ", matchType='" + matchType + '\'' +
                '}';
    }
}
