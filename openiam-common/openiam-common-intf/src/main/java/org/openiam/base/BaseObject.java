package org.openiam.base;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * Base object for all POJOs that represent domain objects.
 *
 * @author Suneet Shah
 * @version 3
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BaseObject", propOrder = {
        "objectState",
        "requestorUserId",
        "requestorLogin",
        "requestorDomain",
        "requestClientIP"
})
public class BaseObject implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5732158957137722277L;

    public static final String NEW = "NEW";
    public static final String UPDATE = "UPDATE";
    public static final String DELETE = "DELETE";

    protected String objectState = NEW;

    // track the source of the request
    protected String requestorUserId;
    protected String requestorLogin;
    protected String requestorDomain;
    protected String requestClientIP;


    public BaseObject() {

    }

    public String getObjectState() {
        return objectState;
    }

    public void setObjectState(String objectState) {
        this.objectState = objectState;
    }

    @Deprecated
    public String getRequestorLogin() {
        return requestorLogin;
    }

    @Deprecated
    public void setRequestorLogin(String requestorLogin) {
        this.requestorLogin = requestorLogin;
    }

    @Deprecated
    public String getRequestorDomain() {
        return requestorDomain;
    }

    @Deprecated
    public void setRequestorDomain(String requestorDomain) {
        this.requestorDomain = requestorDomain;
    }

    public String getRequestClientIP() {
        return requestClientIP;
    }

    public void setRequestClientIP(String requestClientIP) {
        this.requestClientIP = requestClientIP;
    }
    
    

	public String getRequestorUserId() {
		return requestorUserId;
	}

	public void setRequestorUserId(String requestorUserId) {
		this.requestorUserId = requestorUserId;
	}


    
}
