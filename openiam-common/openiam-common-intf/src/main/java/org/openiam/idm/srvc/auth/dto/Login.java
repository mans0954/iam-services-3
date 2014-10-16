package org.openiam.idm.srvc.auth.dto;
// Generated Feb 18, 2008 3:56:06 PM by Hibernate Tools 3.2.0.b11


import org.openiam.base.AttributeOperationEnum;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.pswd.dto.PasswordHistory;

import javax.xml.bind.annotation.*;
import java.util.*;

/**
 * Login domain object
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Login", propOrder = {
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
        "provStatus",
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
        "lastUpdate",
        "passwordHistory"
})
@XmlSeeAlso({
        Subject.class,
        SSOToken.class,
        PasswordHistory.class
})
@DozerDTOCorrespondence(LoginEntity.class)
public class Login implements java.io.Serializable {

    private static final long serialVersionUID = -1972779170001619759L;
    protected AttributeOperationEnum operation = AttributeOperationEnum.NO_CHANGE;
    
    private String loginId;
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

    protected ProvLoginStatusEnum provStatus;

    @XmlTransient
    protected LoginStatusEnum initialStatus;

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

    private Set<PasswordHistory> passwordHistory = new HashSet<PasswordHistory>(0);

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

    public ProvLoginStatusEnum getProvStatus() {
        return provStatus;
    }

    public void setProvStatus(ProvLoginStatusEnum provStatus) {
        this.provStatus = provStatus;
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

	public String getLogin() {
		return login;
	}

    public void setLogin(String login) {
        this.login = login;
        if(login != null) {
            this.lowerCaseLogin = login.toLowerCase();
        } else {
            this.lowerCaseLogin = null;
        }
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

    public LoginStatusEnum getInitialStatus() {
        return initialStatus;
    }

    public void setInitialStatus(LoginStatusEnum initialStatus) {
        this.initialStatus = initialStatus;
    }

    public Set<PasswordHistory> getPasswordHistory() {
        return passwordHistory;
    }

    public void setPasswordHistory(Set<PasswordHistory> passwordHistory) {
        this.passwordHistory = passwordHistory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Login login1 = (Login) o;

        if (login != null ? !login.equals(login1.login) : login1.login != null) return false;
        if (loginId != null ? !loginId.equals(login1.loginId) : login1.loginId != null) return false;
        if (managedSysId != null ? !managedSysId.equals(login1.managedSysId) : login1.managedSysId != null)
            return false;
        if (userId != null ? !userId.equals(login1.userId) : login1.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = loginId != null ? loginId.hashCode() : 0;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (managedSysId != null ? managedSysId.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Login");
        sb.append("{login='").append(login).append('\'');
        sb.append(", managedSysId='").append(managedSysId).append('\'');
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", pwdChanged=").append(pwdChanged);
        sb.append(", pwdExp=").append(pwdExp);
        sb.append(", status=").append(status);
        sb.append(", provStatus=").append(provStatus);
        sb.append(", initialStatus=").append(initialStatus);
        sb.append(", lastUpdate=").append(lastUpdate);
        sb.append('}');
        return sb.toString();
    }
}

