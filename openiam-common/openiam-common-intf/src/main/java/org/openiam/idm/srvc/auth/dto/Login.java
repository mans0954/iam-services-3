package org.openiam.idm.srvc.auth.dto;
// Generated Feb 18, 2008 3:56:06 PM by Hibernate Tools 3.2.0.b11


import org.openiam.base.AttributeOperationEnum;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.auth.domain.LoginEntity;

import javax.xml.bind.annotation.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Login domain object
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Login", propOrder = {
		"domainId",
        "login",
        "lowerCaseLogin",
        "managedSysId",
        "userId",
        "password",
        "pwdEquivalentToken",
        "pwdChanged",
        "pwdExp",
        "firstTimeLogin",
        "resetPassword",
        "isLocked",
        "status",
        "gracePeriod",
        "createDate",
        "createdBy",
        "currentLoginHost",
        "authFailCount",
        "lastAuthAttempt",
        "canonicalName",
        "lastLogin",
        "isDefault",
        "selected",
        "loginAttributes",
        "passwordChangeCount",
        "operation",
        "origPrincipalName",
        "managedSysName",
        "lastLoginIP",
        "prevLoginIP",
        "prevLogin",
        "pswdResetToken",
        "pswdResetTokenExp",
        "loginId",
        "lastUpdate"
})
@XmlSeeAlso({
        Subject.class,
        SSOToken.class
})
@DozerDTOCorrespondence(LoginEntity.class)
public class Login implements java.io.Serializable {

    private static final long serialVersionUID = -1972779170001619759L;
    protected AttributeOperationEnum operation = AttributeOperationEnum.NO_CHANGE;
    
    private String loginId;
    private String domainId;
    private String login;
    private String lowerCaseLogin;
    private String managedSysId;

    protected String userId;

    protected String password;

    protected String pwdEquivalentToken;

    @XmlSchemaType(name = "dateTime")
    private Date pwdChanged;

    @XmlSchemaType(name = "dateTime")
    private Date pwdExp;

    protected int firstTimeLogin;

    protected int resetPassword;

    protected int isLocked;

    protected LoginStatusEnum status;

    @XmlSchemaType(name = "dateTime")
    private Date gracePeriod;

    @XmlSchemaType(name = "dateTime")
    private Date createDate;

    protected String createdBy;

    protected String currentLoginHost;

    protected Integer authFailCount = new Integer(0);

    @XmlSchemaType(name = "dateTime")
    private Date lastAuthAttempt;

    protected String canonicalName;

    @XmlSchemaType(name = "dateTime")
    private Date lastLogin;

    protected Integer isDefault = new Integer(0);

    protected Integer passwordChangeCount = new Integer(0);

    protected String lastLoginIP;

    @XmlSchemaType(name = "dateTime")
    private Date prevLogin;

    protected String prevLoginIP;

    protected String pswdResetToken;

    @XmlSchemaType(name = "dateTime")
    private Date pswdResetTokenExp;

    protected Set<LoginAttribute> loginAttributes = new HashSet<LoginAttribute>(0);
    
    protected boolean selected;
    
    protected String origPrincipalName;
    
    protected String managedSysName;
    
    private Date lastUpdate;


    public Login() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPwdEquivalentToken() {
        return this.pwdEquivalentToken;
    }

    public void setPwdEquivalentToken(String pwdEquivalentToken) {
        this.pwdEquivalentToken = pwdEquivalentToken;
    }

    public Date getPwdChanged() {
        return this.pwdChanged;
    }

    public void setPwdChanged(Date pwdChanged) {
        this.pwdChanged = pwdChanged;
    }

    public Date getPwdExp() {
        return this.pwdExp;
    }

    public void setPwdExp(Date pwdExp) {
        this.pwdExp = pwdExp;
    }

    public int getFirstTimeLogin() {
        return firstTimeLogin;
    }

    public void setFirstTimeLogin(int firstTimeLogin) {
        this.firstTimeLogin = firstTimeLogin;
    }
    /**
     * Indicates that the password has been reset
     *
     * @return
     */
    public int getResetPassword() {
        return resetPassword;
    }

    public void setResetPassword(int resetPassword) {
        this.resetPassword = resetPassword;
    }

    public int getIsLocked() {
        return this.isLocked;
    }

    public void setIsLocked(int isLocked) {
        this.isLocked = isLocked;
    }

    public LoginStatusEnum getStatus() {
        return this.status;
    }

    public void setStatus(LoginStatusEnum status) {
        this.status = status;
    }

    public Date getGracePeriod() {

        return this.gracePeriod;
    }

    public void setGracePeriod(Date gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCurrentLoginHost() {
        return this.currentLoginHost;
    }

    public void setCurrentLoginHost(String currentLoginHost) {
        this.currentLoginHost = currentLoginHost;
    }

    public Integer getAuthFailCount() {
        return this.authFailCount;
    }

    public void setAuthFailCount(Integer authFailCount) {
        this.authFailCount = authFailCount;
    }

    public Date getLastAuthAttempt() {
        return this.lastAuthAttempt;
    }

    public void setLastAuthAttempt(Date lastAuthAttempt) {
        this.lastAuthAttempt = lastAuthAttempt;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    /**
     * Tracks how many times the password has been changed.
     *
     * @return
     */

    public Integer getPasswordChangeCount() {
        return passwordChangeCount;
    }

    public void setPasswordChangeCount(Integer passwordChangeCount) {
        this.passwordChangeCount = passwordChangeCount;
    }


    public Date getLastLogin() {
        return lastLogin;
    }


    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }


    public String getLastLoginIP() {
        return lastLoginIP;
    }

    public void setLastLoginIP(String lastLoginIP) {
        this.lastLoginIP = lastLoginIP;
    }

    public Date getPrevLogin() {
        return prevLogin;
    }

    public void setPrevLogin(Date prevLogin) {
        this.prevLogin = prevLogin;
    }

    public String getPrevLoginIP() {
        return prevLoginIP;
    }

    public void setPrevLoginIP(String prevLoginIP) {
        this.prevLoginIP = prevLoginIP;
    }


    public String getPswdResetToken() {
        return pswdResetToken;
    }

    public void setPswdResetToken(String pswdResetToken) {
        this.pswdResetToken = pswdResetToken;
    }


    public Date getPswdResetTokenExp() {
        return pswdResetTokenExp;
    }

    public void setPswdResetTokenExp(Date pswdResetTokenExp) {
        this.pswdResetTokenExp = pswdResetTokenExp;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public Set<LoginAttribute> getLoginAttributes() {
        return this.loginAttributes;
    }

    public void setLoginAttributes(Set<LoginAttribute> loginAttributes) {
        this.loginAttributes = loginAttributes;
    }

    public boolean isSelected() {
        return selected;
    }


    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public AttributeOperationEnum getOperation() {
        return operation;
    }


    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
    }

    public String getOrigPrincipalName() {
        return origPrincipalName;
    }


    public void setOrigPrincipalName(String origPrincipalName) {
        this.origPrincipalName = origPrincipalName;
    }

    public String getManagedSysName() {
        return managedSysName;
    }


    public void setManagedSysName(String managedSysName) {
        this.managedSysName = managedSysName;
    }

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

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

    public String getLowerCaseLogin() {
        return lowerCaseLogin;
    }

    public void setLowerCaseLogin(String lowerCaseLogin) {
        if(lowerCaseLogin != null) {
            this.lowerCaseLogin = lowerCaseLogin.toLowerCase();
        }
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((authFailCount == null) ? 0 : authFailCount.hashCode());
		result = prime * result
				+ ((canonicalName == null) ? 0 : canonicalName.hashCode());
		result = prime * result
				+ ((createDate == null) ? 0 : createDate.hashCode());
		result = prime * result
				+ ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime
				* result
				+ ((currentLoginHost == null) ? 0 : currentLoginHost.hashCode());
		result = prime * result
				+ ((domainId == null) ? 0 : domainId.hashCode());
		result = prime * result + firstTimeLogin;
		result = prime * result
				+ ((gracePeriod == null) ? 0 : gracePeriod.hashCode());
		result = prime * result
				+ ((isDefault == null) ? 0 : isDefault.hashCode());
		result = prime * result + isLocked;
		result = prime * result
				+ ((lastAuthAttempt == null) ? 0 : lastAuthAttempt.hashCode());
		result = prime * result
				+ ((lastLogin == null) ? 0 : lastLogin.hashCode());
		result = prime * result
				+ ((lastLoginIP == null) ? 0 : lastLoginIP.hashCode());
		result = prime * result
				+ ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		result = prime * result + ((loginId == null) ? 0 : loginId.hashCode());
		result = prime * result
				+ ((managedSysId == null) ? 0 : managedSysId.hashCode());
		result = prime * result
				+ ((managedSysName == null) ? 0 : managedSysName.hashCode());
		result = prime * result
				+ ((operation == null) ? 0 : operation.hashCode());
		result = prime
				* result
				+ ((origPrincipalName == null) ? 0 : origPrincipalName
						.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime
				* result
				+ ((passwordChangeCount == null) ? 0 : passwordChangeCount
						.hashCode());
		result = prime * result
				+ ((prevLogin == null) ? 0 : prevLogin.hashCode());
		result = prime * result
				+ ((prevLoginIP == null) ? 0 : prevLoginIP.hashCode());
		result = prime * result
				+ ((pswdResetToken == null) ? 0 : pswdResetToken.hashCode());
		result = prime
				* result
				+ ((pswdResetTokenExp == null) ? 0 : pswdResetTokenExp
						.hashCode());
		result = prime * result
				+ ((pwdChanged == null) ? 0 : pwdChanged.hashCode());
		result = prime
				* result
				+ ((pwdEquivalentToken == null) ? 0 : pwdEquivalentToken
						.hashCode());
		result = prime * result + ((pwdExp == null) ? 0 : pwdExp.hashCode());
		result = prime * result + resetPassword;
		result = prime * result + (selected ? 1231 : 1237);
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		Login other = (Login) obj;
		if (authFailCount == null) {
			if (other.authFailCount != null)
				return false;
		} else if (!authFailCount.equals(other.authFailCount))
			return false;
		if (canonicalName == null) {
			if (other.canonicalName != null)
				return false;
		} else if (!canonicalName.equals(other.canonicalName))
			return false;
		if (createDate == null) {
			if (other.createDate != null)
				return false;
		} else if (!createDate.equals(other.createDate))
			return false;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (currentLoginHost == null) {
			if (other.currentLoginHost != null)
				return false;
		} else if (!currentLoginHost.equals(other.currentLoginHost))
			return false;
		if (domainId == null) {
			if (other.domainId != null)
				return false;
		} else if (!domainId.equals(other.domainId))
			return false;
		if (firstTimeLogin != other.firstTimeLogin)
			return false;
		if (gracePeriod == null) {
			if (other.gracePeriod != null)
				return false;
		} else if (!gracePeriod.equals(other.gracePeriod))
			return false;
		if (isDefault == null) {
			if (other.isDefault != null)
				return false;
		} else if (!isDefault.equals(other.isDefault))
			return false;
		if (isLocked != other.isLocked)
			return false;
		if (lastAuthAttempt == null) {
			if (other.lastAuthAttempt != null)
				return false;
		} else if (!lastAuthAttempt.equals(other.lastAuthAttempt))
			return false;
		if (lastLogin == null) {
			if (other.lastLogin != null)
				return false;
		} else if (!lastLogin.equals(other.lastLogin))
			return false;
		if (lastLoginIP == null) {
			if (other.lastLoginIP != null)
				return false;
		} else if (!lastLoginIP.equals(other.lastLoginIP))
			return false;
		if (lastUpdate == null) {
			if (other.lastUpdate != null)
				return false;
		} else if (!lastUpdate.equals(other.lastUpdate))
			return false;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		if (loginId == null) {
			if (other.loginId != null)
				return false;
		} else if (!loginId.equals(other.loginId))
			return false;
		if (managedSysId == null) {
			if (other.managedSysId != null)
				return false;
		} else if (!managedSysId.equals(other.managedSysId))
			return false;
		if (managedSysName == null) {
			if (other.managedSysName != null)
				return false;
		} else if (!managedSysName.equals(other.managedSysName))
			return false;
		if (operation != other.operation)
			return false;
		if (origPrincipalName == null) {
			if (other.origPrincipalName != null)
				return false;
		} else if (!origPrincipalName.equals(other.origPrincipalName))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (passwordChangeCount == null) {
			if (other.passwordChangeCount != null)
				return false;
		} else if (!passwordChangeCount.equals(other.passwordChangeCount))
			return false;
		if (prevLogin == null) {
			if (other.prevLogin != null)
				return false;
		} else if (!prevLogin.equals(other.prevLogin))
			return false;
		if (prevLoginIP == null) {
			if (other.prevLoginIP != null)
				return false;
		} else if (!prevLoginIP.equals(other.prevLoginIP))
			return false;
		if (pswdResetToken == null) {
			if (other.pswdResetToken != null)
				return false;
		} else if (!pswdResetToken.equals(other.pswdResetToken))
			return false;
		if (pswdResetTokenExp == null) {
			if (other.pswdResetTokenExp != null)
				return false;
		} else if (!pswdResetTokenExp.equals(other.pswdResetTokenExp))
			return false;
		if (pwdChanged == null) {
			if (other.pwdChanged != null)
				return false;
		} else if (!pwdChanged.equals(other.pwdChanged))
			return false;
		if (pwdEquivalentToken == null) {
			if (other.pwdEquivalentToken != null)
				return false;
		} else if (!pwdEquivalentToken.equals(other.pwdEquivalentToken))
			return false;
		if (pwdExp == null) {
			if (other.pwdExp != null)
				return false;
		} else if (!pwdExp.equals(other.pwdExp))
			return false;
		if (resetPassword != other.resetPassword)
			return false;
		if (selected != other.selected)
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	
}

