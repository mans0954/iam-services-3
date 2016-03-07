package org.openiam.idm.srvc.auth.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ObjectMapAdapter;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.service.AuthenticationConstants;
import org.openiam.idm.srvc.user.dto.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * AuthenticationContext enables a higher level of flexibility during the authentication
 * process.
 * @author Suneet Shah
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthenticationContextImpl", propOrder = {
    "authenticationType",
    "resourceId",
    "credential",
    "principal",
    "password",
    "authParam",
    "loginModule",
    "user",
    "login",
    "managedSysId"
})
@XmlSeeAlso({
    PasswordCredential.class
})
public class AuthenticationContextImpl implements Serializable, AuthenticationContext {

	private String authenticationType;
	/* Class that will be responsible for authentication  */
	private String loginModule;
	private String resourceId;
	private Credential credential;
	private String principal;
	private String password;	
	@XmlJavaTypeAdapter(ObjectMapAdapter.class)
	private Map<String,Object> authParam = new HashMap();
	
	private User user;
	private Login login;
	private String managedSysId;

    protected String clientIP;
    protected String nodeIP;
    protected boolean skipPasswordCheck;


	
	private static final Log log = LogFactory.getLog(AuthenticationContextImpl.class);
	


	public void AuthenticationContext() {
		
	}
	
	@Override
	public void setSkipPasswordCheck(boolean skipPasswordCheck) {
		this.skipPasswordCheck = skipPasswordCheck;
	}

	@Override
	public boolean isSkipPasswordCheck() {
		return skipPasswordCheck;
	}
	
	/**
	 * Add a parameter to the context
	 * @param key
	 * @param value
	 */
    @Override
	public void addParam(String key, Object value) {
		authParam.put(key, value);
	}
	/**
	 * Retrieve a parameter from the context
	 * @param key
	 * @return
	 */
    @Override
	public Object getParam(String key) {
		return (authParam.get(key));
	}
	
	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.context.AuthenticationContext#createCredentialObject(java.lang.String)
	 */
    @Override
	public Credential createCredentialObject(String authnType) {
		if (authnType.equals(AuthenticationConstants.AUTHN_TYPE_PASSWORD)) {
			return new PasswordCredential();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.context.AuthenticationContext#setCredential(java.lang.String, org.openiam.idm.srvc.auth.context.Credential)
	 */
    @Override
	public void setCredential(String authnType, Credential cred) {
		authenticationType = authnType;
		credential = cred;
	}
	
	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.context.AuthenticationContext#getCredential()
	 */
    @Override
	public Credential getCredential() {
		return credential;
	}

	
	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.context.AuthenticationContext#getResourceId()
	 */
    @Override
	public String getResourceId() {
		return resourceId;
	}


	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.context.AuthenticationContext#setResourceId(java.lang.String)
	 */
    @Override
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}


	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.context.AuthenticationContext#getAuthParam()
	 */
    @Override
	public Map<String, Object> getAuthParam() {
		return authParam;
	}


	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.auth.context.AuthenticationContext#setAuthParam(java.util.Map)
	 */
    @Override
	public void setAuthParam(Map<String, Object> authParam) {
		this.authParam = authParam;
	}

	public String getAuthenticationType() {
		return authenticationType;
	}

	public void setAuthenticationType(String authenticationType) {
		this.authenticationType = authenticationType;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

    public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

    public void setCredential(Credential credential) {
		this.credential = credential;
	}
    @Override
	public String getLoginModule() {
		return loginModule;
	}
    @Override
	public void setLoginModule(String loginModule) {
		this.loginModule = loginModule;
	}
    @Override
	public User getUser() {
		return user;
	}
    @Override
	public void setUser(User user) {
		this.user = user;
	}
    @Override
	public Login getLogin() {
		return login;
	}
    @Override
	public void setLogin(Login login) {
		this.login = login;
	}
    @Override
	public String getManagedSysId() {
		return managedSysId;
	}
    @Override
	public void setManagedSysId(String managedSysId) {
		this.managedSysId = managedSysId;
	}
    @Override
    public String getClientIP() {
        return clientIP;
    }
    @Override
    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }
    @Override
    public String getNodeIP() {
        return nodeIP;
    }
    @Override
    public void setNodeIP(String nodeIP) {
        this.nodeIP = nodeIP;
    }
}
