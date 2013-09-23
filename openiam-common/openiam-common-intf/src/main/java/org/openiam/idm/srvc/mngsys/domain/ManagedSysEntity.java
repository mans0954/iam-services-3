package org.openiam.idm.srvc.mngsys.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;

import javax.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "MANAGED_SYS")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(ManagedSysDto.class)
public class ManagedSysEntity implements Serializable {
    private static final long serialVersionUID = -648884785253890053L;
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "MANAGED_SYS_ID", length = 32, nullable = false)
    private String managedSysId;
    @Column(name = "NAME", length = 40)
    private String name;
    @Column(name = "DESCRIPTION", length = 80)
    private String description;
    @Column(name = "STATUS", length = 20)
    private String status;
    @Column(name = "CONNECTOR_ID", length = 32, nullable = false)
    private String connectorId;
    @Column(name = "DOMAIN_ID", length = 20, nullable = false)
    private String domainId;
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
    @Column(name = "RESOURCE_ID", length = 32)
    private String resourceId;
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
    @Column(name = "SEARCH_HNDLR", length = 100)
    private String searchHandler;
    @Column(name = "LOOKUP_HNDLR", length = 100)
    private String lookupHandler;
    @Column(name = "TEST_CONNECTION_HNDLR", length = 100)
    private String testConnectionHandler;
    @Column(name = "RECONCILE_RESOURCE_HNDLR", length = 100)
    private String reconcileResourceHandler;
    @Column(name = "HNDLR_5", length = 100)
    private String handler5;

    @OneToMany(mappedBy="managedSys")
    private Set<ManagedSystemObjectMatchEntity> mngSysObjectMatchs = new HashSet<ManagedSystemObjectMatchEntity>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "MANAGED_SYS_ID", referencedColumnName = "MANAGED_SYS_ID")
    private List<ManagedSysRuleEntity> rules = new ArrayList<ManagedSysRuleEntity>(
            0);
    
    @OneToMany(orphanRemoval = false, cascade = {CascadeType.DETACH, CascadeType.REFRESH}, mappedBy = "managedSystem", fetch = FetchType.LAZY)
    private Set<GroupEntity> groups;

    public List<ManagedSysRuleEntity> getRules() {
        return rules;
    }

    public void setRules(List<ManagedSysRuleEntity> rules) {
        this.rules = rules;
    }

    public String getManagedSysId() {
        return managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
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

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
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

    public String getHandler5() {
        return handler5;
    }

    public void setHandler5(String handler5) {
        this.handler5 = handler5;
    }

    public Set<ManagedSystemObjectMatchEntity> getMngSysObjectMatchs() {
        return mngSysObjectMatchs;
    }

    public void setMngSysObjectMatchs(
            Set<ManagedSystemObjectMatchEntity> mngSysObjectMatchs) {
        this.mngSysObjectMatchs = mngSysObjectMatchs;
    }

    public Set<GroupEntity> getGroups() {
		return groups;
	}

	public void setGroups(Set<GroupEntity> groups) {
		this.groups = groups;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ManagedSysEntity that = (ManagedSysEntity) o;

        if (addHandler != null ? !addHandler.equals(that.addHandler)
                : that.addHandler != null)
            return false;
        if (commProtocol != null ? !commProtocol.equals(that.commProtocol)
                : that.commProtocol != null)
            return false;
        if (connectionString != null ? !connectionString
                .equals(that.connectionString) : that.connectionString != null)
            return false;
        if (connectorId != null ? !connectorId.equals(that.connectorId)
                : that.connectorId != null)
            return false;
        if (deleteHandler != null ? !deleteHandler.equals(that.deleteHandler)
                : that.deleteHandler != null)
            return false;
        if (description != null ? !description.equals(that.description)
                : that.description != null)
            return false;
        if (domainId != null ? !domainId.equals(that.domainId)
                : that.domainId != null)
            return false;
        if (driverUrl != null ? !driverUrl.equals(that.driverUrl)
                : that.driverUrl != null)
            return false;
        if (endDate != null ? !endDate.equals(that.endDate)
                : that.endDate != null)
            return false;
        if (handler5 != null ? !handler5.equals(that.handler5)
                : that.handler5 != null)
            return false;
        if (hostUrl != null ? !hostUrl.equals(that.hostUrl)
                : that.hostUrl != null)
            return false;
        if (lookupHandler != null ? !lookupHandler.equals(that.lookupHandler)
                : that.lookupHandler != null)
            return false;
        if (managedSysId != null ? !managedSysId.equals(that.managedSysId)
                : that.managedSysId != null)
            return false;
        if (modifyHandler != null ? !modifyHandler.equals(that.modifyHandler)
                : that.modifyHandler != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;
        if (passwordHandler != null ? !passwordHandler
                .equals(that.passwordHandler) : that.passwordHandler != null)
            return false;
        if (port != null ? !port.equals(that.port) : that.port != null)
            return false;
        if (primaryRepository != null ? !primaryRepository
                .equals(that.primaryRepository)
                : that.primaryRepository != null)
            return false;
        if (pswd != null ? !pswd.equals(that.pswd) : that.pswd != null)
            return false;
        if (reconcileResourceHandler != null ? !reconcileResourceHandler
                .equals(that.reconcileResourceHandler)
                : that.reconcileResourceHandler != null)
            return false;
        if (resourceId != null ? !resourceId.equals(that.resourceId)
                : that.resourceId != null)
            return false;
        if (searchHandler != null ? !searchHandler.equals(that.searchHandler)
                : that.searchHandler != null)
            return false;
        if (secondaryRepositoryId != null ? !secondaryRepositoryId
                .equals(that.secondaryRepositoryId)
                : that.secondaryRepositoryId != null)
            return false;
        if (startDate != null ? !startDate.equals(that.startDate)
                : that.startDate != null)
            return false;
        if (status != null ? !status.equals(that.status) : that.status != null)
            return false;
        if (suspendHandler != null ? !suspendHandler
                .equals(that.suspendHandler) : that.suspendHandler != null)
            return false;
        if (testConnectionHandler != null ? !testConnectionHandler
                .equals(that.testConnectionHandler)
                : that.testConnectionHandler != null)
            return false;
        if (updateSecondary != null ? !updateSecondary
                .equals(that.updateSecondary) : that.updateSecondary != null)
            return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = managedSysId != null ? managedSysId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result
                + (description != null ? description.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result
                + (connectorId != null ? connectorId.hashCode() : 0);
        result = 31 * result + (domainId != null ? domainId.hashCode() : 0);
        result = 31 * result + (hostUrl != null ? hostUrl.hashCode() : 0);
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result
                + (commProtocol != null ? commProtocol.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (pswd != null ? pswd.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (resourceId != null ? resourceId.hashCode() : 0);
        result = 31
                * result
                + (primaryRepository != null ? primaryRepository.hashCode() : 0);
        result = 31
                * result
                + (secondaryRepositoryId != null ? secondaryRepositoryId
                        .hashCode() : 0);
        result = 31 * result
                + (updateSecondary != null ? updateSecondary.hashCode() : 0);
        result = 31 * result + (driverUrl != null ? driverUrl.hashCode() : 0);
        result = 31 * result
                + (connectionString != null ? connectionString.hashCode() : 0);
        result = 31 * result + (addHandler != null ? addHandler.hashCode() : 0);
        result = 31 * result
                + (modifyHandler != null ? modifyHandler.hashCode() : 0);
        result = 31 * result
                + (deleteHandler != null ? deleteHandler.hashCode() : 0);
        result = 31 * result
                + (passwordHandler != null ? passwordHandler.hashCode() : 0);
        result = 31 * result
                + (suspendHandler != null ? suspendHandler.hashCode() : 0);
        result = 31 * result
                + (searchHandler != null ? searchHandler.hashCode() : 0);
        result = 31 * result
                + (lookupHandler != null ? lookupHandler.hashCode() : 0);
        result = 31
                * result
                + (testConnectionHandler != null ? testConnectionHandler
                        .hashCode() : 0);
        result = 31
                * result
                + (reconcileResourceHandler != null ? reconcileResourceHandler
                        .hashCode() : 0);
        result = 31 * result + (handler5 != null ? handler5.hashCode() : 0);
        return result;
    }
}
