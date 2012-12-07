package org.openiam.idm.srvc.auth.domain;
// Generated Feb 18, 2008 3:56:06 PM by Hibernate Tools 3.2.0.b11


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Indexed;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginId;
import org.openiam.idm.srvc.auth.dto.SSOToken;
import org.openiam.idm.srvc.auth.dto.Subject;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="LOGIN")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(Login.class)
//@Indexed
public class LoginEntity implements java.io.Serializable, Cloneable {
    private static final long serialVersionUID = -1972779170001619759L;
    @EmbeddedId
    protected LoginEmbeddableId id;

    @Column(name="USER_ID",length=32)
    protected String userId;

    @Column(name="PASSWORD",length=255)
    protected String password;

    @Column(name="PWD_EQUIVALENT_TOKEN",length=255)
    protected String pwdEquivalentToken;

    @XmlSchemaType(name = "dateTime")
    @Column(name="PWD_CHANGED",length=19)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date pwdChanged;

    @XmlSchemaType(name = "dateTime")
    @Column(name="PWD_EXP",length=19)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date pwdExp;

    @Column(name="FIRST_TIME_LOGIN",nullable = false)
    protected int firstTimeLogin;

    @Column(name="RESET_PWD",nullable = false)
    protected int resetPassword;

    @Column(name="IS_LOCKED",nullable = false)
    protected int isLocked;

    @Column(name="STATUS",length = 20)
    protected String status;

    @XmlSchemaType(name = "dateTime")
    @Column(name="GRACE_PERIOD",length=19)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date gracePeriod;

    @XmlSchemaType(name = "dateTime")
    @Column(name="CREATE_DATE",length=19)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createDate;

    @Column(name="CREATED_BY",length = 32)
    protected String createdBy;

    @Column(name="CURRENT_LOGIN_HOST",length = 40)
    protected String currentLoginHost;

    @Column(name="AUTH_FAIL_COUNT")
    protected Integer authFailCount = new Integer(0);

    @XmlSchemaType(name = "dateTime")
    @Column(name="LAST_AUTH_ATTEMPT",length = 19)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date lastAuthAttempt;

    @Column(name="CANONICAL_NAME",length = 100)
    protected String canonicalName;

    @XmlSchemaType(name = "dateTime")
    @Column(name="LAST_LOGIN",length = 19)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date lastLogin;

    @Column(name="IS_DEFAULT")
    protected Integer isDefault = new Integer(0);

    @Column(name="PWD_CHANGE_COUNT")
    protected Integer passwordChangeCount = new Integer(0);

    @Column(name="LAST_LOGIN_IP")
    protected String lastLoginIP;

    @XmlSchemaType(name = "dateTime")
    @Column(name="PREV_LOGIN",length = 19)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date prevLogin;

    @Column(name="PREV_LOGIN_IP")
    protected String prevLoginIP;

    @Column(name="PSWD_RESET_TOKEN")
    protected String pswdResetToken;

    @XmlSchemaType(name = "dateTime")
    @Column(name="PSWD_RESET_TOKEN_EXP",length = 19)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date pswdResetTokenExp;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumns({@JoinColumn(name = "SERVICE_ID", referencedColumnName = "SERVICE_ID", insertable = false, updatable = false),
                  @JoinColumn(name = "LOGIN", referencedColumnName = "LOGIN", insertable = false, updatable = false),
                  @JoinColumn(name = "MANAGED_SYS_ID", referencedColumnName = "MANAGED_SYS_ID", insertable = false, updatable = false)})
    protected Set<LoginAttributeEntity> loginAttributes = new HashSet<LoginAttributeEntity>(0);


    public LoginEntity() {
    }

    public LoginEntity(LoginEmbeddableId id, int resetPwd, int isLocked) {
        this.id = id;
        this.firstTimeLogin = resetPwd;
        this.isLocked = isLocked;
    }

    public LoginEntity(LoginEmbeddableId id, String userId, String password, String pwdEquivalentToken, Date pwdChanged, Date pwdExp, int resetPwd, int isLocked, String status, Date gracePeriod, Date createDate, String createdBy, String currentLoginHost, Integer authFailCount, Date lastAuthAttempt, Set<LoginAttributeEntity> loginAttributes) {
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
        LoginEntity l = new LoginEntity();
        LoginEmbeddableId lgId = new LoginEmbeddableId(id.getDomainId(), id.getLogin(), id.getManagedSysId());
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
        l.setPassword(password);
        l.setPasswordChangeCount(passwordChangeCount);
        l.setPwdChanged(pwdChanged);
        l.setPwdExp(pwdExp);
        l.setResetPassword(resetPassword);
        l.setStatus(status);
        l.setUserId(userId);
        return l;

    }


    public LoginEmbeddableId getId() {
        return this.id;
    }

    public void setId(LoginEmbeddableId id) {
        this.id = id;
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

    public Set<LoginAttributeEntity> getLoginAttributes() {
        return this.loginAttributes;
    }

    public void setLoginAttributes(Set<LoginAttributeEntity> loginAttributes) {
        this.loginAttributes = loginAttributes;
    }


    @Override
    public String toString() {
        return "Login{" +
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
                ", loginAttributes=" + loginAttributes +
                ", lastLoginIP='" + lastLoginIP + '\'' +
                ", prevLogin=" + prevLogin +
                ", prevLoginIP='" + prevLoginIP + '\'' +
                '}';
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
		LoginEntity other = (LoginEntity) obj;
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

