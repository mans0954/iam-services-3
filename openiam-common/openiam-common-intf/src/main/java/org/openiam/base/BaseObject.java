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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((objectState == null) ? 0 : objectState.hashCode());
		result = prime * result
				+ ((requestClientIP == null) ? 0 : requestClientIP.hashCode());
		result = prime * result
				+ ((requestorDomain == null) ? 0 : requestorDomain.hashCode());
		result = prime * result
				+ ((requestorLogin == null) ? 0 : requestorLogin.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseObject other = (BaseObject) obj;
		if (objectState == null) {
			if (other.objectState != null)
				return false;
		} else if (!objectState.equals(other.objectState))
			return false;
		if (requestClientIP == null) {
			if (other.requestClientIP != null)
				return false;
		} else if (!requestClientIP.equals(other.requestClientIP))
			return false;
		if (requestorDomain == null) {
			if (other.requestorDomain != null)
				return false;
		} else if (!requestorDomain.equals(other.requestorDomain))
			return false;
		if (requestorLogin == null) {
			if (other.requestorLogin != null)
				return false;
		} else if (!requestorLogin.equals(other.requestorLogin))
			return false;
		return true;
	}
    
    
}
