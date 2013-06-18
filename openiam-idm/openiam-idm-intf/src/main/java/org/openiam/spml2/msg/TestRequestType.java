package org.openiam.spml2.msg;

import org.openiam.provision.dto.GenericProvisionObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TestRequestType", propOrder = {
        "psoID"
})
public class TestRequestType<ProvisionObject extends GenericProvisionObject>   extends RequestType<ProvisionObject>{
    @XmlElement(required = true)
    protected PSOIdentifierType psoID;

    public PSOIdentifierType getPsoID() {
        return psoID;
    }

    public void setPsoID(PSOIdentifierType psoID) {
        this.psoID = psoID;
    }
}
