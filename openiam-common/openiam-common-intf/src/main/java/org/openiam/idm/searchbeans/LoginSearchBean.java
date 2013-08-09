package org.openiam.idm.searchbeans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.auth.dto.Login;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GroupSearchBean", propOrder = {
	"domainId",
	"login",
	"managedSysId",
	"userId"
})
public class LoginSearchBean extends AbstractSearchBean<Login, String> implements SearchBean<Login, String>, Serializable {

	private String domainId;
	private String login;
	private String managedSysId;
	private String userId;
	
	public String getDomainId() {
		return domainId;
	}
	
	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getManagedSysId() {
		return managedSysId;
	}
	
	public void setManagedSysId(String managedSysId) {
		this.managedSysId = managedSysId;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
