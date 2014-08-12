package org.openiam.idm.srvc.auth.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Used to track the authentication state of a user.  Also used with SSO.
 */
@Entity
@Table(name = "AUTH_STATE")
public class AuthStateEntity implements java.io.Serializable {

	@EmbeddedId
	private AuthStateId id;
    
    @Column(name="AUTH_STATE", precision =5, scale = 1)
    private BigDecimal authState;
    
    @Column(name="TOKEN",length = 100)
    private String token;
    
    @Column(name="AA",length = 20)
    private String aa;
    
    @Column(name = "EXPIRATION",precision = 18, scale = 0)
    private Long expiration;
    
    @Column(name = "LAST_LOGIN",length = 19)
    
    private Date lastLogin;
    
    @Column(name="IP_ADDRESS",length = 20)
    private String ipAddress;

    public AuthStateEntity() {
    }

    public AuthStateEntity(final String aa, final BigDecimal authState, final Long expiration,
                     	   final String token, final String userId, final String tokenType) {
        super();
        this.aa = aa;
        this.authState = authState;
        this.expiration = expiration;
        this.token = token;
        this.id = new AuthStateId(userId, tokenType);
    }

    public BigDecimal getAuthState() {
        return this.authState;
    }

    public void setAuthState(BigDecimal authState) {
        this.authState = authState;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAa() {
        return this.aa;
    }

    public void setAa(String aa) {
        this.aa = aa;
    }

    public Long getExpiration() {
        return this.expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public Date getLastLogin() {
        return this.lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

	public AuthStateId getId() {
		return id;
	}

	public void setId(AuthStateId id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aa == null) ? 0 : aa.hashCode());
		result = prime * result
				+ ((authState == null) ? 0 : authState.hashCode());
		result = prime * result
				+ ((expiration == null) ? 0 : expiration.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((ipAddress == null) ? 0 : ipAddress.hashCode());
		result = prime * result
				+ ((lastLogin == null) ? 0 : lastLogin.hashCode());
		result = prime * result + ((token == null) ? 0 : token.hashCode());
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
		AuthStateEntity other = (AuthStateEntity) obj;
		if (aa == null) {
			if (other.aa != null)
				return false;
		} else if (!aa.equals(other.aa))
			return false;
		if (authState == null) {
			if (other.authState != null)
				return false;
		} else if (!authState.equals(other.authState))
			return false;
		if (expiration == null) {
			if (other.expiration != null)
				return false;
		} else if (!expiration.equals(other.expiration))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (ipAddress == null) {
			if (other.ipAddress != null)
				return false;
		} else if (!ipAddress.equals(other.ipAddress))
			return false;
		if (lastLogin == null) {
			if (other.lastLogin != null)
				return false;
		} else if (!lastLogin.equals(other.lastLogin))
			return false;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("AuthStateEntity [id=%s, authState=%s, token=%s, aa=%s, expiration=%s, lastLogin=%s, ipAddress=%s]",
						id, authState, token, aa, expiration, lastLogin,
						ipAddress);
	}

	
}
