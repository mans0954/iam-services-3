package org.openiam.provision.resp;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.connector.type.ObjectValue;
import org.openiam.provision.type.ExtensibleAttribute;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso(ObjectValue.class)
@XmlType(name = "LookupObjectResponse", propOrder = {
        "attrList",
        "principalName",
        "managedSysId"
})
public class LookupObjectResponse extends Response {

    List<ExtensibleAttribute> attrList;
    String principalName;
    String managedSysId;

    public LookupObjectResponse() {
        super();

    }

    public LookupObjectResponse(ResponseStatus s) {
        super(s);

    }

    public List<ExtensibleAttribute> getAttrList() {
        return attrList;
    }

    public void setAttrList(List<ExtensibleAttribute> attrList) {
        this.attrList = attrList;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getManagedSysId() {
        return managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
    }

}
