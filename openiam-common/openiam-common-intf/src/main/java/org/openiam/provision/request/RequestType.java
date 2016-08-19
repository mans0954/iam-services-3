
package org.openiam.provision.request;

import org.openiam.provision.type.ExtensibleObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;



/**
 * <p>Java class for RequestType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="RequestType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:SPML:2:0}ExtensibleType">
 *       &lt;attribute name="requestID" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="executionMode" type="{urn:oasis:names:tc:SPML:2:0}ExecutionModeType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BaseRequestType",
        propOrder = {
                "requestID",
                "executionMode",
                "targetID",
                "hostUrl",
                "hostPort",
                "hostLoginId",
                "hostLoginPassword",
                "baseDN",
                "containerID",
                "scriptHandler",
                "operation",
                "extensibleObject",
                "objectIdentity"
        })
@XmlSeeAlso({
        CrudRequest.class
})
public class RequestType<ExtObject extends ExtensibleObject>   {

    @XmlElement(required = true)
    protected String requestID;
    protected String executionMode;
    protected String targetID;
    protected String hostUrl;
    protected String hostPort;
    protected String hostLoginId;
    protected String hostLoginPassword;
    protected String baseDN;
    protected String containerID;
    @XmlElement
    private String scriptHandler;
    /* Change to an enum */
    protected String operation;

    protected ExtObject extensibleObject;

    @XmlElement(required = true)
    protected String objectIdentity;

    public RequestType() {

    }

    public RequestType(String requestID, String executionMode) {
        this.requestID = requestID;
        this.executionMode = executionMode;
    }

    /**
     * Gets the value of the requestID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRequestID() {
        return requestID;
    }

    /**
     * Sets the value of the requestID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRequestID(String value) {
        this.requestID = value;
    }

    /**
     * Gets the value of the executionMode property.
     *
     * @return
     *     possible object is String
     *
     */
    public String getExecutionMode() {
        return executionMode;
    }

    /**
     * Sets the value of the executionMode property.
     *
     * @param value
     *     allowed object is String
     *
     */
    public void setExecutionMode(String value) {
        this.executionMode = value;
    }

    public String getTargetID() {
        return targetID;
    }

    public void setTargetID(String targetID) {
        this.targetID = targetID;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    public String getHostPort() {
        return hostPort;
    }

    public void setHostPort(String hostPort) {
        this.hostPort = hostPort;
    }

    public String getHostLoginId() {
        return hostLoginId;
    }

    public void setHostLoginId(String hostLoginId) {
        this.hostLoginId = hostLoginId;
    }

    public String getHostLoginPassword() {
        return hostLoginPassword;
    }

    public void setHostLoginPassword(String hostLoginPassword) {
        this.hostLoginPassword = hostLoginPassword;
    }

    public String getBaseDN() {
        return baseDN;
    }

    public void setBaseDN(String baseDN) {
        this.baseDN = baseDN;
    }

    public String getContainerID() {
        return containerID;
    }

    public void setContainerID(String containerID) {
        this.containerID = containerID;
    }

    public String getScriptHandler() {
        return scriptHandler;
    }

    public void setScriptHandler(String scriptHandler) {
        this.scriptHandler = scriptHandler;
    }
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public ExtObject getExtensibleObject() {
        return extensibleObject;
    }

    public void setExtensibleObject(ExtObject extensibleObject) {
        this.extensibleObject = extensibleObject;
    }

    public String getObjectIdentity() {
        return objectIdentity;
    }

    public void setObjectIdentity(String objectIdentity) {
        this.objectIdentity = objectIdentity;
    }

}
