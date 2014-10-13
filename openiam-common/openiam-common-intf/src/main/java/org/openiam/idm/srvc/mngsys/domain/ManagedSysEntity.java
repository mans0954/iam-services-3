package org.openiam.idm.srvc.mngsys.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.am.srvc.constants.SearchScopeType;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;

import javax.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "MANAGED_SYS")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(ManagedSysDto.class)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "MANAGED_SYS_ID")),
	@AttributeOverride(name = "name", column = @Column(name = "NAME", length = 40))
})
public class ManagedSysEntity extends AbstractKeyNameEntity {
    private static final long serialVersionUID = -648884785253890053L;
    @Column(name = "DESCRIPTION", length = 80)
    private String description;
    @Column(name = "STATUS", length = 20)
    private String status;
    @Column(name = "CONNECTOR_ID", length = 32, nullable = false)
    private String connectorId;
    @Column(name = "HOST_URL", length = 80)
    private String hostUrl;
    @Column(name = "PORT")
    private Integer port;
    @Column(name = "COMM_PROTOCOL", length = 20)
    private String commProtocol;
    @Column(name = "USER_ID", length = 150)
    private String userId;
    @Column(name = "PSWD", length = 255)
    private String pswd;
    @Column(name = "START_DATE", length = 10)
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Column(name = "END_DATE", length = 10)
    @Temporal(TemporalType.DATE)
    private Date endDate;
    @Column(name = "ATTRIBUTE_NAMES_LOOKUP", length = 120)
    private String attributeNamesLookup;
    @Column(name = "SEARCH_SCOPE")
    @Enumerated(EnumType.ORDINAL)
    private SearchScopeType searchScope = SearchScopeType.SUBTREE_SCOPE;
    
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = true, updatable = false, nullable=false)
	private ResourceEntity resource;
    
    @Column(name = "PRIMARY_REPOSITORY")
    private Integer primaryRepository;
    @Column(name = "SECONDARY_REPOSITORY_ID", length = 32)
    private String secondaryRepositoryId;
    @Column(name = "ALWAYS_UPDATE_SECONDARY")
    private Integer updateSecondary;
    @Column(name = "DRIVER_URL", length = 100)
    private String driverUrl;
    @Column(name = "CONNECTION_STRING", length = 100)
    private String connectionString;
    @Column(name = "ADD_HNDLR", length = 100)
    private String addHandler;
    @Column(name = "MODIFY_HNDLR", length = 100)
    private String modifyHandler;
    @Column(name = "DELETE_HNDLR", length = 100)
    private String deleteHandler;
    @Column(name = "SETPASS_HNDLR", length = 100)
    private String passwordHandler;
    @Column(name = "SUSPEND_HNDLR", length = 100)
    private String suspendHandler;
    @Column(name = "RESUME_HNDLR", length = 100)
    private String resumeHandler;
    @Column(name = "SEARCH_HNDLR", length = 100)
    private String searchHandler;
    @Column(name = "LOOKUP_HNDLR", length = 100)
    private String lookupHandler;
    @Column(name = "TEST_CONNECTION_HNDLR", length = 100)
    private String testConnectionHandler;
    @Column(name = "RECONCILE_RESOURCE_HNDLR", length = 100)
    private String reconcileResourceHandler;
    @Column(name = "ATTRIBUTE_NAMES_HNDLR", length = 100)
    private String attributeNamesHandler;
    @Column(name = "HNDLR_5", length = 100)
    private String handler5;

    @OneToMany(mappedBy = "managedSys")
    private Set<ManagedSystemObjectMatchEntity> mngSysObjectMatchs = new HashSet<ManagedSystemObjectMatchEntity>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "MANAGED_SYS_ID", referencedColumnName = "MANAGED_SYS_ID")
    private List<ManagedSysRuleEntity> rules = new ArrayList<ManagedSysRuleEntity>(0);

    @OneToMany(orphanRemoval = false, cascade = { CascadeType.DETACH, CascadeType.REFRESH }, mappedBy = "managedSystem", fetch = FetchType.LAZY)
    private Set<GroupEntity> groups;

    @OneToMany(orphanRemoval = false, cascade = { CascadeType.DETACH, CascadeType.REFRESH }, mappedBy = "managedSystem", fetch = FetchType.LAZY)
    private Set<RoleEntity> roles;
    
    @OneToMany(orphanRemoval = false, cascade = { CascadeType.DETACH, CascadeType.REFRESH }, mappedBy = "managedSystem", fetch = FetchType.LAZY)
    private Set<AuthProviderEntity> authProviders;

    @OneToMany(orphanRemoval = false, cascade = { CascadeType.DETACH, CascadeType.REFRESH }, mappedBy = "managedSystem", fetch = FetchType.LAZY)
    private Set<ContentProviderEntity> contentProviders;
    
    public List<ManagedSysRuleEntity> getRules() {
        return rules;
    }

    public void setRules(List<ManagedSysRuleEntity> rules) {
        this.rules = rules;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(String connectorId) {
        this.connectorId = connectorId;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getCommProtocol() {
        return commProtocol;
    }

    public void setCommProtocol(String commProtocol) {
        this.commProtocol = commProtocol;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getAttributeNamesLookup() {
        return attributeNamesLookup;
    }

    public void setAttributeNamesLookup(String attributeNamesLookup) {
        this.attributeNamesLookup = attributeNamesLookup;
    }

    public SearchScopeType getSearchScope() {
        return searchScope;
    }

    public void setSearchScope(SearchScopeType searchScope) {
        this.searchScope = searchScope;
    }

    public ResourceEntity getResource() {
		return resource;
	}

	public void setResource(ResourceEntity resource) {
		this.resource = resource;
	}

	public Integer getPrimaryRepository() {
        return primaryRepository;
    }

    public void setPrimaryRepository(Integer primaryRepository) {
        this.primaryRepository = primaryRepository;
    }

    public String getSecondaryRepositoryId() {
        return secondaryRepositoryId;
    }

    public void setSecondaryRepositoryId(String secondaryRepositoryId) {
        this.secondaryRepositoryId = secondaryRepositoryId;
    }

    public Integer getUpdateSecondary() {
        return updateSecondary;
    }

    public void setUpdateSecondary(Integer updateSecondary) {
        this.updateSecondary = updateSecondary;
    }

    public String getDriverUrl() {
        return driverUrl;
    }

    public void setDriverUrl(String driverUrl) {
        this.driverUrl = driverUrl;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getAddHandler() {
        return addHandler;
    }

    public void setAddHandler(String addHandler) {
        this.addHandler = addHandler;
    }

    public String getModifyHandler() {
        return modifyHandler;
    }

    public void setModifyHandler(String modifyHandler) {
        this.modifyHandler = modifyHandler;
    }

    public String getDeleteHandler() {
        return deleteHandler;
    }

    public void setDeleteHandler(String deleteHandler) {
        this.deleteHandler = deleteHandler;
    }

    public String getPasswordHandler() {
        return passwordHandler;
    }

    public void setPasswordHandler(String passwordHandler) {
        this.passwordHandler = passwordHandler;
    }

    public String getSuspendHandler() {
        return suspendHandler;
    }

    public void setSuspendHandler(String suspendHandler) {
        this.suspendHandler = suspendHandler;
    }

    public String getResumeHandler() {
        return resumeHandler;
    }

    public void setResumeHandler(String resumeHandler) {
        this.resumeHandler = resumeHandler;
    }

    public String getSearchHandler() {
        return searchHandler;
    }

    public void setSearchHandler(String searchHandler) {
        this.searchHandler = searchHandler;
    }

    public String getLookupHandler() {
        return lookupHandler;
    }

    public void setLookupHandler(String lookupHandler) {
        this.lookupHandler = lookupHandler;
    }

    public String getTestConnectionHandler() {
        return testConnectionHandler;
    }

    public void setTestConnectionHandler(String testConnectionHandler) {
        this.testConnectionHandler = testConnectionHandler;
    }

    public String getReconcileResourceHandler() {
        return reconcileResourceHandler;
    }

    public void setReconcileResourceHandler(String reconcileResourceHandler) {
        this.reconcileResourceHandler = reconcileResourceHandler;
    }

    public String getAttributeNamesHandler() {
        return attributeNamesHandler;
    }

    public void setAttributeNamesHandler(String attributeNamesHandler) {
        this.attributeNamesHandler = attributeNamesHandler;
    }

    public String getHandler5() {
        return handler5;
    }

    public void setHandler5(String handler5) {
        this.handler5 = handler5;
    }

    public Set<ManagedSystemObjectMatchEntity> getMngSysObjectMatchs() {
        return mngSysObjectMatchs;
    }

    public void setMngSysObjectMatchs(Set<ManagedSystemObjectMatchEntity> mngSysObjectMatchs) {
        this.mngSysObjectMatchs = mngSysObjectMatchs;
    }

    public Set<GroupEntity> getGroups() {
        return groups;
    }

    public void setGroups(Set<GroupEntity> groups) {
        this.groups = groups;
    }

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }

	public Set<AuthProviderEntity> getAuthProviders() {
		return authProviders;
	}

	public void setAuthProviders(Set<AuthProviderEntity> authProviders) {
		this.authProviders = authProviders;
	}

	public Set<ContentProviderEntity> getContentProviders() {
		return contentProviders;
	}

	public void setContentProviders(Set<ContentProviderEntity> contentProviders) {
		this.contentProviders = contentProviders;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((addHandler == null) ? 0 : addHandler.hashCode());
		result = prime
				* result
				+ ((attributeNamesHandler == null) ? 0 : attributeNamesHandler
						.hashCode());
		result = prime
				* result
				+ ((attributeNamesLookup == null) ? 0 : attributeNamesLookup
						.hashCode());
		result = prime * result
				+ ((commProtocol == null) ? 0 : commProtocol.hashCode());
		result = prime
				* result
				+ ((connectionString == null) ? 0 : connectionString.hashCode());
		result = prime * result
				+ ((connectorId == null) ? 0 : connectorId.hashCode());
		result = prime * result
				+ ((deleteHandler == null) ? 0 : deleteHandler.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((driverUrl == null) ? 0 : driverUrl.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result
				+ ((handler5 == null) ? 0 : handler5.hashCode());
		result = prime * result + ((hostUrl == null) ? 0 : hostUrl.hashCode());
		result = prime * result
				+ ((lookupHandler == null) ? 0 : lookupHandler.hashCode());
		result = prime * result
				+ ((modifyHandler == null) ? 0 : modifyHandler.hashCode());
		result = prime * result
				+ ((passwordHandler == null) ? 0 : passwordHandler.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		result = prime
				* result
				+ ((primaryRepository == null) ? 0 : primaryRepository
						.hashCode());
		result = prime * result + ((pswd == null) ? 0 : pswd.hashCode());
		result = prime
				* result
				+ ((reconcileResourceHandler == null) ? 0
						: reconcileResourceHandler.hashCode());
		result = prime * result
				+ ((resource == null) ? 0 : resource.hashCode());
		result = prime * result
				+ ((resumeHandler == null) ? 0 : resumeHandler.hashCode());
		result = prime * result
				+ ((searchHandler == null) ? 0 : searchHandler.hashCode());
		result = prime * result
				+ ((searchScope == null) ? 0 : searchScope.hashCode());
		result = prime
				* result
				+ ((secondaryRepositoryId == null) ? 0 : secondaryRepositoryId
						.hashCode());
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result
				+ ((suspendHandler == null) ? 0 : suspendHandler.hashCode());
		result = prime
				* result
				+ ((testConnectionHandler == null) ? 0 : testConnectionHandler
						.hashCode());
		result = prime * result
				+ ((updateSecondary == null) ? 0 : updateSecondary.hashCode());
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
		ManagedSysEntity other = (ManagedSysEntity) obj;
		if (addHandler == null) {
			if (other.addHandler != null)
				return false;
		} else if (!addHandler.equals(other.addHandler))
			return false;
		if (attributeNamesHandler == null) {
			if (other.attributeNamesHandler != null)
				return false;
		} else if (!attributeNamesHandler.equals(other.attributeNamesHandler))
			return false;
		if (attributeNamesLookup == null) {
			if (other.attributeNamesLookup != null)
				return false;
		} else if (!attributeNamesLookup.equals(other.attributeNamesLookup))
			return false;
		if (commProtocol == null) {
			if (other.commProtocol != null)
				return false;
		} else if (!commProtocol.equals(other.commProtocol))
			return false;
		if (connectionString == null) {
			if (other.connectionString != null)
				return false;
		} else if (!connectionString.equals(other.connectionString))
			return false;
		if (connectorId == null) {
			if (other.connectorId != null)
				return false;
		} else if (!connectorId.equals(other.connectorId))
			return false;
		if (deleteHandler == null) {
			if (other.deleteHandler != null)
				return false;
		} else if (!deleteHandler.equals(other.deleteHandler))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (driverUrl == null) {
			if (other.driverUrl != null)
				return false;
		} else if (!driverUrl.equals(other.driverUrl))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (handler5 == null) {
			if (other.handler5 != null)
				return false;
		} else if (!handler5.equals(other.handler5))
			return false;
		if (hostUrl == null) {
			if (other.hostUrl != null)
				return false;
		} else if (!hostUrl.equals(other.hostUrl))
			return false;
		if (lookupHandler == null) {
			if (other.lookupHandler != null)
				return false;
		} else if (!lookupHandler.equals(other.lookupHandler))
			return false;
		if (modifyHandler == null) {
			if (other.modifyHandler != null)
				return false;
		} else if (!modifyHandler.equals(other.modifyHandler))
			return false;
		if (passwordHandler == null) {
			if (other.passwordHandler != null)
				return false;
		} else if (!passwordHandler.equals(other.passwordHandler))
			return false;
		if (port == null) {
			if (other.port != null)
				return false;
		} else if (!port.equals(other.port))
			return false;
		if (primaryRepository == null) {
			if (other.primaryRepository != null)
				return false;
		} else if (!primaryRepository.equals(other.primaryRepository))
			return false;
		if (pswd == null) {
			if (other.pswd != null)
				return false;
		} else if (!pswd.equals(other.pswd))
			return false;
		if (reconcileResourceHandler == null) {
			if (other.reconcileResourceHandler != null)
				return false;
		} else if (!reconcileResourceHandler
				.equals(other.reconcileResourceHandler))
			return false;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		if (resumeHandler == null) {
			if (other.resumeHandler != null)
				return false;
		} else if (!resumeHandler.equals(other.resumeHandler))
			return false;
		if (searchHandler == null) {
			if (other.searchHandler != null)
				return false;
		} else if (!searchHandler.equals(other.searchHandler))
			return false;
		if (searchScope != other.searchScope)
			return false;
		if (secondaryRepositoryId == null) {
			if (other.secondaryRepositoryId != null)
				return false;
		} else if (!secondaryRepositoryId.equals(other.secondaryRepositoryId))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (suspendHandler == null) {
			if (other.suspendHandler != null)
				return false;
		} else if (!suspendHandler.equals(other.suspendHandler))
			return false;
		if (testConnectionHandler == null) {
			if (other.testConnectionHandler != null)
				return false;
		} else if (!testConnectionHandler.equals(other.testConnectionHandler))
			return false;
		if (updateSecondary == null) {
			if (other.updateSecondary != null)
				return false;
		} else if (!updateSecondary.equals(other.updateSecondary))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	
}
