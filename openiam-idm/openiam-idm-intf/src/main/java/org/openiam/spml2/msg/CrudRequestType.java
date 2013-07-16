package org.openiam.spml2.msg;

import org.openiam.provision.dto.GenericProvisionObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/16/13
 * Time: 1:25 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CrudRequestType", propOrder = {
        "psoID"
})
public abstract class CrudRequestType<ProvisionObject extends GenericProvisionObject>   extends RequestType<ProvisionObject> {
    protected PSOIdentifierType psoID;
    public CrudRequestType() {} ;

    public CrudRequestType(String requestId, ExecutionModeType executionMode, PSOIdentifierType type) {
        super(requestId, executionMode);
        this.psoID=type;
    }

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
