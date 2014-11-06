package org.openiam.idm.srvc.auth.domain;
// Generated Feb 18, 2008 3:56:06 PM by Hibernate Tools 3.2.0.b11


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;
import org.openiam.core.dao.lucene.LuceneId;
import org.openiam.core.dao.lucene.LuceneLastUpdate;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.dto.ProvLoginStatusEnum;
import org.openiam.idm.srvc.pswd.domain.PasswordHistoryEntity;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.*;

@Entity
@Table(name="LOGIN")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(Login.class)
@Indexed
@Embeddable
public class LoginEntity implements java.io.Serializable {
    private static final long serialVersionUID = -1972779170001619759L;
    
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "LOGIN_ID", length = 32, nullable = false)
    @LuceneId
    @DocumentId
    private String loginId;

    @Fields ({
        @Field(index = Index.TOKENIZED),
        @Field(name = "login", index = Index.TOKENIZED, store = Store.YES),
        @Field(name = "loginUntokenized", index = Index.UN_TOKENIZED, store = Store.YES)
    })
    @Column(name="LOGIN",length=320)
    private String login;
    
    @Column(name="LOWERCASE_LOGIN",length=320)
    private String lowerCaseLogin;
    
    @Field(name = "managedSysId", index = Index.UN_TOKENIZED, store = Store.YES)
    @Column(name="MANAGED_SYS_ID",length=50)
    private String managedSysId;

    @Field(name = "userId", index = Index.UN_TOKENIZED, store = Store.YES)
    @Column(name="USER_ID",length=32)
    protected String userId;

    @Column(name="PASSWORD",length=255)
    protected String password;

    @Column(name="PWD_EQUIVALENT_TOKEN",length=255)
    protected String pwdEquivalentToken;

    @Column(name="PWD_CHANGED",length=19)
    @Temporal(TemporalType.TIMESTAMP)
    private Date pwdChanged;

    @Column(name="PWD_EXP",length=19)
    @Temporal(TemporalType.TIMESTAMP)
    private Date pwdExp;

    @Column(name="FIRST_TIME_LOGIN",nullable = false)
    protected int firstTimeLogin;

    @Column(name="RESET_PWD",nullable = false)
    protected int resetPassword;

    @Column(name="IS_LOCKED",nullable = false)
    protected int isLocked;

    @Enumerated(EnumType.STRING)
    @Column(name="STATUS",length = 20)
    protected LoginStatusEnum status;

    @Enumerated(EnumType.STRING)
    @Column(name="PROV_STATUS",length = 20)
    protected ProvLoginStatusEnum provStatus;

    @Column(name="GRACE_PERIOD",length=19)
    @Temporal(TemporalType.TIMESTAMP)
    private Date gracePeriod;

    @Column(name="CREATE_DATE",length=19)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name="CREATED_BY",length = 32)
    protected String createdBy;

    @Column(name="CURRENT_LOGIN_HOST",length = 40)
    protected String currentLoginHost;

    @Column(name="AUTH_FAIL_COUNT")
    protected Integer authFailCount = 0;

    @Column(name="LAST_AUTH_ATTEMPT",length = 19)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastAuthAttempt;

    @Column(name="CANONICAL_NAME",length = 100)
    protected String canonicalName;

    @Column(name="LAST_LOGIN",length = 19)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;

    @Column(name="IS_DEFAULT")
    protected Integer isDefault = 0;

    @Column(name="PWD_CHANGE_COUNT")
    protected Integer passwordChangeCount = 0;

    @Column(name="LAST_LOGIN_IP")
    protected String lastLoginIP;

    @Column(name="PREV_LOGIN",length = 19)
    @Temporal(TemporalType.TIMESTAMP)
    private Date prevLogin;

    @Column(name="PREV_LOGIN_IP")
    protected String prevLoginIP;

    @Column(name="PSWD_RESET_TOKEN")
    protected String pswdResetToken;

    @Column(name="PSWD_RESET_TOKEN_EXP",length = 19)
    @Temporal(TemporalType.TIMESTAMP)
    private Date pswdResetTokenExp;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "LOGIN_ID")
    @Fetch(FetchMode.SUBSELECT)
    protected Set<LoginAttributeEntity> loginAttributes = new HashSet<LoginAttributeEntity>(0);

    @Column(name = "LAST_UPDATE", length = 19)
    @LuceneLastUpdate
    private Date lastUpdate;

    @OneToMany(orphanRemoval = true, mappedBy = "login", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<PasswordHistoryEntity> passwordHistory = new HashSet<PasswordHistoryEntity>(0);

    public LoginEntity() {
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

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
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

    public Set<PasswordHistoryEntity> getPasswordHistory() {
        return passwordHistory;
    }

    public void setPasswordHistory(Set<PasswordHistoryEntity> passwordHistory) {
        this.passwordHistory = passwordHistory;
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
		result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((provStatus == null) ? 0 : provStatus.hashCode());
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
        if (provStatus == null) {
            if (other.provStatus != null)
                return false;
        } else if (!provStatus.equals(other.provStatus))
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
        final StringBuilder sb = new StringBuilder();
        sb.append("LoginEntity");
        sb.append("{login='").append(login).append('\'');
        sb.append(", managedSysId='").append(managedSysId).append('\'');
        sb.append(", pwdChanged=").append(pwdChanged);
        sb.append(", pwdExp=").append(pwdExp);
        sb.append(", status=").append(status);
        sb.append(", provStatus=").append(provStatus);
        sb.append(", lastUpdate=").append(lastUpdate);
        sb.append('}');
        return sb.toString();
    }

	public void copyProperties(Login login) {
		setAuthFailCount(login.getAuthFailCount());
		setCanonicalName(login.getCanonicalName());
		setCreateDate(login.getCreateDate());
		setCreatedBy(login.getCreatedBy());
		setCurrentLoginHost(login.getCurrentLoginHost());
		setFirstTimeLogin(login.getFirstTimeLogin());
		setGracePeriod(login.getGracePeriod());
		setIsDefault(login.getIsDefault());
		setIsLocked(login.getIsLocked());
		setLastAuthAttempt(login.getLastAuthAttempt());
		setLastLogin(login.getLastLogin());
		setLastLoginIP(login.getLastLoginIP());
		setLastUpdate(login.getLastUpdate());
		setLogin(login.getLogin());
		setManagedSysId(login.getManagedSysId());
		setPassword(login.getPassword());
		setPasswordChangeCount(login.getPasswordChangeCount());
		setPrevLogin(login.getPrevLogin());
		setPrevLoginIP(login.getPrevLoginIP());
		setProvStatus(login.getProvStatus());
		setPswdResetToken(login.getPswdResetToken());
		setPswdResetTokenExp(login.getPswdResetTokenExp());
		setPwdChanged(login.getPwdChanged());
		setPwdEquivalentToken(login.getPwdEquivalentToken());
		setPwdExp(login.getPwdExp());
		setResetPassword(login.getResetPassword());
		setStatus(login.getStatus());
		setUserId(login.getUserId());
	}
}

