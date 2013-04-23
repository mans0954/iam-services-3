package org.openiam.idm.srvc.user.dto;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.meta.dto.PageTempate;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NewUserProfileRequestModel", 
	propOrder = { 
        "roleIds",
        "loginList"
})
public class NewUserProfileRequestModel extends UserProfileRequestModel implements Serializable {

	private List<Login> loginList;
	private List<String> roleIds;

	public List<String> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<String> roleIds) {
		this.roleIds = roleIds;
	}

	public List<Login> getLoginList() {
		return loginList;
	}

	public void setLoginList(List<Login> loginList) {
		this.loginList = loginList;
	}
	
	public void addLogin(final Login login) {
		if(login != null) {
			if(this.loginList == null) {
				this.loginList = new LinkedList<Login>();
			}
			this.loginList.add(login);
		}
	}
}
