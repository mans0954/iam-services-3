
package org.openiam.connector.type.request;

import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.LinkedList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LookupRequest", propOrder = {
    "requestedAttributes"
})
public class LookupRequest<ExtObject extends ExtensibleObject> extends SearchRequest<ExtObject>{

    protected List<ExtensibleAttribute> requestedAttributes = new LinkedList<ExtensibleAttribute>();

    public List<ExtensibleAttribute> getRequestedAttributes() {
        return requestedAttributes;
    }

    public void setRequestedAttributes(List<ExtensibleAttribute> requestedAttributes) {
        this.requestedAttributes = requestedAttributes;
    }
}
