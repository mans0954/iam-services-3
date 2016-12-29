
package org.openiam.idm.srvc.audit.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getIds complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getIds">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="searchBean" type="{urn:idm.openiam.org/srvc/audit/service}AuditLogSearchBean" minOccurs="0"/>
 *         &lt;element name="from" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="size" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getIds", propOrder = {
    "searchBean",
    "from",
    "size"
})
public class GetIds {

    protected AuditLogSearchBean searchBean;
    protected int from;
    protected int size;

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

    /**
     * Gets the value of the from property.
     * 
     */
    public int getFrom() {
        return from;
    }

    /**
     * Sets the value of the from property.
     * 
     */
    public void setFrom(int value) {
        this.from = value;
    }

    /**
     * Gets the value of the size property.
     * 
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     * 
     */
    public void setSize(int value) {
        this.size = value;
    }

}
