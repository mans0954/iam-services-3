package org.openiam.idm.srvc.auth.dto;
// Generated Feb 18, 2008 3:56:06 PM by Hibernate Tools 3.2.0.b11


import org.openiam.base.AttributeOperationEnum;

import javax.xml.bind.annotation.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Login domain object
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Login", propOrder = {
        "id",
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
        "pswdResetTokenExp"
})
@XmlSeeAlso({
        Subject.class,
        SSOToken.class
})
public class Login implements java.io.Serializable, Cloneable {

    /**
     *
     */
    private static final long serialVersionUID = -1972779170001619759L;

    protected AttributeOperationEnum operation;

    protected LoginId id;
    protected String userId;
    protected String password;
    protected String pwdEquivalentToken;
    @XmlSchemaType(name = "dateTime")
    protected Date pwdChanged;
    @XmlSchemaType(name = "dateTime")
    protected Date pwdExp;
    protected int firstTimeLogin;
    protected int resetPassword;
    protected int isLocked;
    protected String status;
    @XmlSchemaType(name = "dateTime")
    protected Date gracePeriod;
    @XmlSchemaType(name = "dateTime")
    protected Date createDate;
    protected String createdBy;
    protected String currentLoginHost;
    protected Integer authFailCount = new Integer(0);
    @XmlSchemaType(name = "dateTime")
    protected Date lastAuthAttempt;
    protected String canonicalName;
    @XmlSchemaType(name = "dateTime")
    protected Date lastLogin;
    protected Integer isDefault = new Integer(0);
    protected Integer passwordChangeCount = new Integer(0);
    protected boolean selected;
    protected Set<LoginAttribute> loginAttributes = new HashSet<LoginAttribute>(0);
    protected String origPrincipalName;
    protected String managedSysName;

    protected String lastLoginIP;
    @XmlSchemaType(name = "dateTime")
    protected Date prevLogin;
    protected String prevLoginIP;

    protected String pswdResetToken;
    @XmlSchemaType(name = "dateTime")
    protected Date pswdResetTokenExp;


    public Login() {
    }


    public Login(LoginId id, int resetPwd, int isLocked) {
        this.id = id;
        this.firstTimeLogin = resetPwd;
        this.isLocked = isLocked;
    }

    public Login(LoginId id, String userId, String password, String pwdEquivalentToken, Date pwdChanged, Date pwdExp, int resetPwd, int isLocked, String status, Date gracePeriod, Date createDate, String createdBy, String currentLoginHost, Integer authFailCount, Date lastAuthAttempt, Set<LoginAttribute> loginAttributes) {
        this.id = id;
        this.userId = userId;
        this.password = password;
        this.pwdEquivalentToken = pwdEquivalentToken;
        this.pwdChanged = pwdChanged;
        this.pwdExp = pwdExp;
        this.firstTimeLogin = resetPwd;
        this.isLocked = isLocked;
        this.status = status;
        this.gracePeriod = gracePeriod;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.currentLoginHost = currentLoginHost;
        this.authFailCount = authFailCount;
        this.lastAuthAttempt = lastAuthAttempt;
        this.loginAttributes = loginAttributes;

    }

    @Override
    public Object clone() {
        Login l = new Login();
        LoginId lgId = new LoginId(id.getDomainId(), id.getLogin(), id.getManagedSysId());
        l.setId(lgId);

        l.setAuthFailCount(authFailCount);
        l.setCanonicalName(canonicalName);
        l.setCreateDate(createDate);
        l.setCreatedBy(createdBy);
        l.setCurrentLoginHost(currentLoginHost);
        l.setFirstTimeLogin(firstTimeLogin);
        l.setGracePeriod(gracePeriod);
        l.setIsDefault(isDefault);
        l.setLastAuthAttempt(lastAuthAttempt);
        l.setLastLogin(lastLogin);
        l.setLoginAttributes(loginAttributes);
        l.setOperation(operation);
        l.setPassword(password);
        l.setPasswordChangeCount(passwordChangeCount);
        l.setPwdChanged(pwdChanged);
        l.setPwdExp(pwdExp);
        l.setResetPassword(resetPassword);
        l.setSelected(selected);
        l.setStatus(status);
        l.setUserId(userId);
        return l;

    }


    public LoginId getId() {
        return this.id;
    }

    public void setId(LoginId id) {
        this.id = id;
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

    public int getIsLocked() {
        return this.isLocked;
    }

    public void setIsLocked(int isLocked) {
        this.isLocked = isLocked;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
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

    public Set<LoginAttribute> getLoginAttributes() {
        return this.loginAttributes;
    }

    public void setLoginAttributes(Set<LoginAttribute> loginAttributes) {
        this.loginAttributes = loginAttributes;
    }


    public Date getLastLogin() {
        return lastLogin;
    }


    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }


    public Integer getIsDefault() {
        return isDefault;
    }


    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }


    public String getCanonicalName() {
        return canonicalName;
    }


    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }


    public boolean isSelected() {
        return selected;
    }


    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "Login{" +
                "operation=" + operation +
                ", id=" + id +
                ", userId='" + userId + '\'' +
                ", password='" + password + '\'' +
                ", pwdEquivalentToken='" + pwdEquivalentToken + '\'' +
                ", pwdChanged=" + pwdChanged +
                ", pwdExp=" + pwdExp +
                ", firstTimeLogin=" + firstTimeLogin +
                ", resetPassword=" + resetPassword +
                ", isLocked=" + isLocked +
                ", status='" + status + '\'' +
                ", gracePeriod=" + gracePeriod +
                ", createDate=" + createDate +
                ", createdBy='" + createdBy + '\'' +
                ", currentLoginHost='" + currentLoginHost + '\'' +
                ", authFailCount=" + authFailCount +
                ", lastAuthAttempt=" + lastAuthAttempt +
                ", canonicalName='" + canonicalName + '\'' +
                ", lastLogin=" + lastLogin +
                ", isDefault=" + isDefault +
                ", passwordChangeCount=" + passwordChangeCount +
                ", selected=" + selected +
                ", loginAttributes=" + loginAttributes +
                ", origPrincipalName='" + origPrincipalName + '\'' +
                ", managedSysName='" + managedSysName + '\'' +
                ", lastLoginIP='" + lastLoginIP + '\'' +
                ", prevLogin=" + prevLogin +
                ", prevLoginIP='" + prevLoginIP + '\'' +
                '}';
    }


    public String getUserId() {
        return userId;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }


    public int getFirstTimeLogin() {
        return firstTimeLogin;
    }


    public void setFirstTimeLogin(int firstTimeLogin) {
        this.firstTimeLogin = firstTimeLogin;
    }


    public AttributeOperationEnum getOperation() {
        return operation;
    }


    public void setOperation(AttributeOperationEnum operation) {
        this.operation = operation;
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

    public Date getPswdResetTokenExp() {
        return pswdResetTokenExp;
    }

    public void setPswdResetTokenExp(Date pswdResetTokenExp) {
        this.pswdResetTokenExp = pswdResetTokenExp;
    }

    public String getPswdResetToken() {
        return pswdResetToken;
    }

    public void setPswdResetToken(String pswdResetToken) {
        this.pswdResetToken = pswdResetToken;
    }


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (firstTimeLogin != other.firstTimeLogin)
			return false;
		if (gracePeriod == null) {
			if (other.gracePeriod != null)
				return false;
		} else if (!gracePeriod.equals(other.gracePeriod))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
		if (loginAttributes == null) {
			if (other.loginAttributes != null)
				return false;
		} else if (!loginAttributes.equals(other.loginAttributes))
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

