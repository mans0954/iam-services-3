
package org.openiam.provision.request;

import org.openiam.provision.type.ExtensibleObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LookupRequest", propOrder = {
})
public class LookupRequest<ExtObject extends ExtensibleObject> extends SearchRequest<ExtObject>{

}
