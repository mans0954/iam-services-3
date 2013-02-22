package org.openiam.base;

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
        "selected",
        "requestorLogin",
        "requestorDomain",
        "requestClientIP"
})
public class BaseObject implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5732158957137722277L;

    public static final String NEW = "NEW";
    public static final String UPDATE = "UPDATE";
    public static final String DELETE = "DELETE";

    protected Boolean selected = new Boolean(false);

    protected String objectState = NEW;

    // track the source of the request
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

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getRequestorLogin() {
        return requestorLogin;
    }

    public void setRequestorLogin(String requestorLogin) {
        this.requestorLogin = requestorLogin;
    }

    public String getRequestorDomain() {
        return requestorDomain;
    }

    public void setRequestorDomain(String requestorDomain) {
        this.requestorDomain = requestorDomain;
    }

    public String getRequestClientIP() {
        return requestClientIP;
    }

    public void setRequestClientIP(String requestClientIP) {
        this.requestClientIP = requestClientIP;
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
		if (selected == null) {
			if (other.selected != null)
				return false;
		} else if (!selected.equals(other.selected))
			return false;
		return true;
	}
    
    
}
