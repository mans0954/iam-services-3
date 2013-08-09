
package org.openiam.connector.type.request;

import org.openiam.connector.type.constant.ReturnData;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.LinkedList;
import java.util.List;


/**
 * <p>Java class for LookupRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LookupRequestType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:SPML:2:0}RequestType">
 *       &lt;sequence>
 *         &lt;element name="psoID" type="{urn:oasis:names:tc:SPML:2:0}PSOIdentifierType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="returnData" type="{urn:oasis:names:tc:SPML:2:0}ReturnDataType" default="everything" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
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
