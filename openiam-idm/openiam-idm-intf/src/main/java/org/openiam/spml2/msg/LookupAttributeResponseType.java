package org.openiam.spml2.msg;

import org.openiam.provision.type.ExtensibleAttribute;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * <p>Java class for LookupAttributeResponseType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="LookupAttributeResponseType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:SPML:2:0}ResponseType">
 *       &lt;sequence>
 *         &lt;element name="pso" type="{urn:oasis:names:tc:SPML:2:0}PSOType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LookupAttributeResponseType", propOrder = {
        "pso",
        "attributeList"
})
public class LookupAttributeResponseType extends ResponseType {

    protected PSOType pso;
    List<String> attributeList;

    /**
     * Gets the value of the pso property.
     *
     * @return
     *     possible object is
     *     {@link PSOType }
     *
     */
    public PSOType getPso() {
        return pso;
    }

    /**
     * Sets the value of the pso property.
     *
     * @param value
     *     allowed object is
     *     {@link PSOType }
     *
     */
    public void setPso(PSOType value) {
        this.pso = value;
    }

    public List<String> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<String> attributeList) {
        this.attributeList = attributeList;
    }
}
