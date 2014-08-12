package org.openiam.idm.srvc.auth.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

public class AuthStateId implements Serializable {
	
	public AuthStateId() {
		
	}
	
	public AuthStateId(final String userId, final String tokenType) {
		this.userId = userId;
		this.tokenType = tokenType;
	}

	@Column(name = "USER_ID",length = 32)
	private String userId;
	    
	@Column(name="TOKEN_TYPE", length=32)
	private String tokenType;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((tokenType == null) ? 0 : tokenType.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		AuthStateId other = (AuthStateId) obj;
		if (tokenType == null) {
			if (other.tokenType != null)
				return false;
		} else if (!tokenType.equals(other.tokenType))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("AuthStateId [userId=%s, tokenType=%s]", userId,
				tokenType);
	}
	 
	 
}
