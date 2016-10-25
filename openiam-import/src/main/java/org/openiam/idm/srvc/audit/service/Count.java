
package org.openiam.idm.srvc.audit.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for count complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="count">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="searchBean" type="{urn:idm.openiam.org/srvc/audit/service}AuditLogSearchBean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "count", propOrder = {
    "searchBean"
})
public class Count {

    protected AuditLogSearchBean searchBean;

    /**
     * Gets the value of the searchBean property.
     * 
     * @return
     *     possible object is
     *     {@link AuditLogSearchBean }
     *     
     */
    public AuditLogSearchBean getSearchBean() {
        return searchBean;
    }

    /**
     * Sets the value of the searchBean property.
     * 
     * @param value
     *     allowed object is
     *     {@link AuditLogSearchBean }
     *     
     */
    public void setSearchBean(AuditLogSearchBean value) {
        this.searchBean = value;
    }

}
