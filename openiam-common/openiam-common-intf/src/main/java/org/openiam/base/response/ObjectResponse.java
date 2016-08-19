
package org.openiam.base.response;


import org.openiam.provision.type.ObjectValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;




@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectResponse", propOrder = {
    "objectValue"
})
public class ObjectResponse extends ResponseType
{
    protected ObjectValue objectValue;

    public ObjectValue getObjectValue() {
        return objectValue;
    }

    public void setObjectValue(ObjectValue objectValue) {
        this.objectValue = objectValue;
    }
}
