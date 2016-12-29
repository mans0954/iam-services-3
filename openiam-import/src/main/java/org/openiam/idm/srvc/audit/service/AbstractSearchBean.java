
package org.openiam.idm.srvc.audit.service;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AbstractSearchBean complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractSearchBean">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="deepCopy" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="sortBy" type="{urn:idm.openiam.org/srvc/audit/service}SortParam" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="findInCache" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractSearchBean", propOrder = {
    "key",
    "deepCopy",
    "sortBy",
    "findInCache"
})
@XmlSeeAlso({
    AuditLogSearchBean.class
})
public abstract class AbstractSearchBean {

    protected Object key;
    protected boolean deepCopy;
    @XmlElement(nillable = true)
    protected List<SortParam> sortBy;
    protected boolean findInCache;

    /**
     * Gets the value of the key property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getKey() {
        return key;
    }

    /**
     * Sets the value of the key property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setKey(Object value) {
        this.key = value;
    }

    /**
     * Gets the value of the deepCopy property.
     * 
     */
    public boolean isDeepCopy() {
        return deepCopy;
    }

    /**
     * Sets the value of the deepCopy property.
     * 
     */
    public void setDeepCopy(boolean value) {
        this.deepCopy = value;
    }

    /**
     * Gets the value of the sortBy property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sortBy property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSortBy().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SortParam }
     * 
     * 
     */
    public List<SortParam> getSortBy() {
        if (sortBy == null) {
            sortBy = new ArrayList<SortParam>();
        }
        return this.sortBy;
    }

    /**
     * Gets the value of the findInCache property.
     * 
     */
    public boolean isFindInCache() {
        return findInCache;
    }

    /**
     * Sets the value of the findInCache property.
     * 
     */
    public void setFindInCache(boolean value) {
        this.findInCache = value;
    }

}
