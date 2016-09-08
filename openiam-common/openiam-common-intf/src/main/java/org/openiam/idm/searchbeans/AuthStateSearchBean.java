package org.openiam.idm.searchbeans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.auth.domain.AuthStateEntity;
import org.openiam.idm.srvc.auth.domain.AuthStateId;
import org.openiam.idm.srvc.res.dto.Resource;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthStateSearchBean", propOrder = {
	"onlyActive",
	"aa"
})
public class AuthStateSearchBean extends AbstractSearchBean<AuthStateEntity, AuthStateId> {

	private boolean onlyActive;
	private String aa;
	
	public AuthStateSearchBean() {
		
	}

	public boolean isOnlyActive() {
		return onlyActive;
	}

	public void setOnlyActive(boolean onlyActive) {
		this.onlyActive = onlyActive;
	}
	
	

	public String getAa() {
		return aa;
	}

	public void setAa(String aa) {
		this.aa = aa;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((aa == null) ? 0 : aa.hashCode());
		result = prime * result + (onlyActive ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthStateSearchBean other = (AuthStateSearchBean) obj;
		if (aa == null) {
			if (other.aa != null)
				return false;
		} else if (!aa.equals(other.aa))
			return false;
		if (onlyActive != other.onlyActive)
			return false;
		return true;
	}

	

}
