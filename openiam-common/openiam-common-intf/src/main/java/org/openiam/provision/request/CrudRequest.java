
package org.openiam.provision.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.provision.type.ExtensibleObject;


/*
* <p>Java class for AddRequestType complex type.
*
* <p>The following schema fragment specifies the expected content contained within this class.
*
* <pre>
* &lt;complexType name="AddRequestType">
*   &lt;complexContent>
*     &lt;extension base="{urn:oasis:names:tc:SPML:2:0}RequestType">
*       &lt;sequence>
*         &lt;element name="psoID" type="{urn:oasis:names:tc:SPML:2:0}PSOIdentifierType" minOccurs="0"/>
*         &lt;element name="containerID" type="{urn:oasis:names:tc:SPML:2:0}PSOIdentifierType" minOccurs="0"/>
*         &lt;element name="data" type="{urn:oasis:names:tc:SPML:2:0}ExtensibleType"/>
*         &lt;element name="capabilityData" type="{urn:oasis:names:tc:SPML:2:0}CapabilityDataType" maxOccurs="unbounded" minOccurs="0"/>
*       &lt;/sequence>
*       &lt;attribute name="targetID" type="{http://www.w3.org/2001/XMLSchema}string" />
*       &lt;attribute name="returnData" type="{urn:oasis:names:tc:SPML:2:0}ReturnDataType" default="everything" />
*     &lt;/extension>
*   &lt;/complexContent>
* &lt;/complexType>
* </pre>
*
*
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CrudRequest", propOrder = {
        "objectIdentityAttributeName"
})
public class CrudRequest<ExtObject extends ExtensibleObject> extends RequestType<ExtObject>{
    /**
     * Attribute name for identity value
     */
    protected String objectIdentityAttributeName;

    public CrudRequest() {}




    public CrudRequest(String objectIdentity, String containerID,
                       String targetID, String hostUrl, String hostPort,
                       String hostLoginId, String hostLoginPassword, String operation,
                       ExtObject extensibleObject) {
        super();
        this.objectIdentity = objectIdentity;
        this.containerID = containerID;
        this.targetID = targetID;
        this.hostUrl = hostUrl;
        this.hostPort = hostPort;
        this.hostLoginId = hostLoginId;
        this.hostLoginPassword = hostLoginPassword;
        this.operation = operation;
        this.extensibleObject = extensibleObject;
    }



    public String getObjectIdentityAttributeName() {
        return objectIdentityAttributeName;
    }

    public void setObjectIdentityAttributeName(String objectIdentityAttributeName) {
        this.objectIdentityAttributeName = objectIdentityAttributeName;
    }
}
