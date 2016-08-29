package org.openiam.provision.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.LinkedList;
import java.util.List;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectValue", propOrder = {
    "objectIdentity",
    "attributeList"
})
public class ObjectValue {

    protected String objectIdentity;
    List<ExtensibleAttribute> attributeList = new LinkedList<ExtensibleAttribute>();

	public String getObjectIdentity() {
		return objectIdentity;
	}

	public void setObjectIdentity(String objectIdentity) {
		this.objectIdentity = objectIdentity;
	}

    public List<ExtensibleAttribute> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<ExtensibleAttribute> attributeList) {
        this.attributeList = attributeList;
    }
}
