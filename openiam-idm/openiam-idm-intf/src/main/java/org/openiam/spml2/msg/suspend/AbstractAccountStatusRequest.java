package org.openiam.spml2.msg.suspend;

import org.openiam.provision.dto.ProvisionUser;
import org.openiam.spml2.msg.PSOIdentifierType;
import org.openiam.spml2.msg.RequestType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/12/13
 * Time: 1:55 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractAccountStatusRequest", propOrder = {
        "psoID"
})
public abstract class AbstractAccountStatusRequest extends RequestType<ProvisionUser> {

    @XmlElement(required = true)
    protected PSOIdentifierType psoID;

    /**
     * Gets the value of the psoID property.
     *
     * @return
     *     possible object is
     *     {@link PSOIdentifierType }
     *
     */
    public PSOIdentifierType getPsoID() {
        return psoID;
    }

    /**
     * Sets the value of the psoID property.
     *
     * @param value
     *     allowed object is
     *     {@link PSOIdentifierType }
     *
     */
    public void setPsoID(PSOIdentifierType value) {
        this.psoID = value;
    }
}
