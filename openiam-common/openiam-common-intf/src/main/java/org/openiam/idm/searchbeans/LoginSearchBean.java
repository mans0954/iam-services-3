package org.openiam.idm.searchbeans;

import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.SearchParam;
import org.openiam.idm.srvc.auth.dto.Login;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import java.io.Serializable;

/**
 * Search Bean to search login records
 * @author lbornov2
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GroupSearchBean", propOrder = {
	"managedSysId",
	"userId",
	"loginMatchToken"
})
public class LoginSearchBean extends AbstractSearchBean<Login, String> implements SearchBean<Login, String>, Serializable {
	
	/**
	 * The managed system ID of the login 
	 */
	private String managedSysId;
	
	/**
	 * The user ID that the matched login should belong to
	 */
	private String userId;
	
	/**
	 * The login token to search by
	 */
	private SearchParam loginMatchToken;
	
	public SearchParam getLoginMatchToken() {
		return loginMatchToken;
	}

	public void setLoginMatchToken(SearchParam loginMatchToken) {
		this.loginMatchToken = loginMatchToken;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((loginMatchToken == null) ? 0 : loginMatchToken.hashCode());
		result = prime * result
				+ ((managedSysId == null) ? 0 : managedSysId.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		LoginSearchBean other = (LoginSearchBean) obj;
		if (loginMatchToken == null) {
			if (other.loginMatchToken != null)
				return false;
		} else if (!loginMatchToken.equals(other.loginMatchToken))
			return false;
		if (managedSysId == null) {
			if (other.managedSysId != null)
				return false;
		} else if (!managedSysId.equals(other.managedSysId))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	
}
