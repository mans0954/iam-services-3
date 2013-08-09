package org.openiam.connector.type.response;

import org.openiam.provision.type.ExtensibleAttribute;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.LinkedList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LookupAttributeResponse", propOrder = {
        "attributes"
})
public class LookupAttributeResponse extends ResponseType {

    protected List<ExtensibleAttribute> attributes = new LinkedList<ExtensibleAttribute>();

    public List<ExtensibleAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<ExtensibleAttribute> attributes) {
        this.attributes = attributes;
    }
}
