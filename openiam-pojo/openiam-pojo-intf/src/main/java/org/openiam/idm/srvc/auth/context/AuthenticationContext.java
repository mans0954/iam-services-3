/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the Lesser GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 *
 */
package org.openiam.idm.srvc.auth.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ObjectMapAdapter;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.service.AuthenticationConstants;
import org.openiam.idm.srvc.user.dto.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationContext {

	private String languageId;
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


	
	private static final Log log = LogFactory.getLog(AuthenticationContext.class);
	


	public void AuthenticationContext() {
		
	}

	public void addParam(String key, Object value) {
		authParam.put(key, value);
	}

	public Object getParam(String key) {
		return (authParam.get(key));
	}

	public Credential createCredentialObject(String authnType) {
		if (authnType.equals(AuthenticationConstants.AUTHN_TYPE_PASSWORD)) {
			return new PasswordCredential();
		}
		return null;
	}

	public void setCredential(String authnType, Credential cred) {
		authenticationType = authnType;
		credential = cred;
	}

	public Credential getCredential() {
		return credential;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public Map<String, Object> getAuthParam() {
		return authParam;
	}

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

	public String getLoginModule() {
		return loginModule;
	}

	public void setLoginModule(String loginModule) {
		this.loginModule = loginModule;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Login getLogin() {
		return login;
	}

	public void setLogin(Login login) {
		this.login = login;
	}

	public String getManagedSysId() {
		return managedSysId;
	}

	public void setManagedSysId(String managedSysId) {
		this.managedSysId = managedSysId;
	}

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public String getNodeIP() {
        return nodeIP;
    }

    public void setNodeIP(String nodeIP) {
        this.nodeIP = nodeIP;
    }

	public String getLanguageId() {
		return languageId;
	}

	public void setLanguageId(String languageId) {
		this.languageId = languageId;
	}
}