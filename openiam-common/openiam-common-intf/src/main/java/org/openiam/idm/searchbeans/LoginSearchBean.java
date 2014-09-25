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
	
}
