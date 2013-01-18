package org.openiam.idm.srvc.mngsys.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.dto.ManagedSys;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "MANAGED_SYS")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(ManagedSys.class)
public class ManagedSysEntity implements Serializable {
    private static final long serialVersionUID = -648884785253890053L;
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="MANAGED_SYS_ID", length=32, nullable = false)
    private String managedSysId;
    @Column(name="NAME", length=40)
    private String name;
    @Column(name="DESCRIPTION", length=80)
    private String description;
    @Column(name="STATUS", length=20)
    private String status;
    @Column(name="CONNECTOR_ID", length=32, nullable = false)
    private String connectorId;
    @Column(name="DOMAIN_ID", length=20, nullable = false)
    private String domainId;
    @Column(name="HOST_URL",length =80)
    private String hostUrl;
    @Column(name="PORT")
    private Integer port;
    @Column(name="COMM_PROTOCOL",length =20)
    private String commProtocol;
    @Column(name="USER_ID",length =150)
    private String userId;
    @Column(name="PSWD",length =255)
    private String pswd;
    @Column(name="START_DATE",length =10)
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Column(name="END_DATE",length =10)
    @Temporal(TemporalType.DATE)
    private Date endDate;
    @Column(name="RESOURCE_ID",length =32)
    private String resourceId;
    @Column(name="PRIMARY_REPOSITORY")
    private Integer primaryRepository;
    @Column(name="SECONDARY_REPOSITORY_ID",length =32)
    private String secondaryRepositoryId;
    @Column(name="ALWAYS_UPDATE_SECONDARY")
    private Integer updateSecondary;
    @Column(name="DRIVER_URL",length =100)
    private  String driverUrl;
    @Column(name="CONNECTION_STRING",length =100)
    private  String connectionString;
    @Column(name="ADD_HNDLR",length =100)
    private  String addHandler;
    @Column(name="MODIFY_HNDLR",length =100)
    private  String modifyHandler;
    @Column(name="DELETE_HNDLR",length =100)
    private  String deleteHandler;
    @Column(name="SETPASS_HNDLR",length =100)
    private  String passwordHandler;
    @Column(name="SUSPEND_HNDLR",length =100)
    private  String suspendHandler;
    @Column(name="HNDLR_1",length =100)
    private  String handler1;
    @Column(name="HNDLR_2",length =100)
    private  String handler2;
    @Column(name="HNDLR_3",length =100)
    private  String handler3;
    @Column(name="HNDLR_4",length =100)
    private  String handler4;
    @Column(name="HNDLR_5",length =100)
    private  String handler5;

    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinColumn(name="MANAGED_SYS_ID", referencedColumnName="MANAGED_SYS_ID")
    private Set<ManagedSystemObjectMatchEntity> mngSysObjectMatchs = new HashSet<ManagedSystemObjectMatchEntity>(0);

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

    public String getHandler1() {
        return handler1;
    }

    public void setHandler1(String handler1) {
        this.handler1 = handler1;
    }

    public String getHandler2() {
        return handler2;
    }

    public void setHandler2(String handler2) {
        this.handler2 = handler2;
    }

    public String getHandler3() {
        return handler3;
    }

    public void setHandler3(String handler3) {
        this.handler3 = handler3;
    }

    public String getHandler4() {
        return handler4;
    }

    public void setHandler4(String handler4) {
        this.handler4 = handler4;
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
}
