package org.openiam.idm.searchbeans;

import org.openiam.idm.srvc.auth.dto.Login;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GroupSearchBean", propOrder = {
	"login",
	"managedSysId",
	"userId"
})
public class LoginSearchBean extends AbstractSearchBean<Login, String> implements SearchBean<Login, String>, Serializable {

	private String login;
	private String managedSysId;
	private String userId;
	
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
