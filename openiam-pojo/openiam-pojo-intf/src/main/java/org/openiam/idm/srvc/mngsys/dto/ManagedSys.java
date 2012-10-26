package org.openiam.idm.srvc.mngsys.dto;

// Generated Nov 3, 2008 12:14:43 AM by Hibernate Tools 3.2.2.GA

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * Domain object representing a managed resource. Managed systems include items such as AD, LDAP, etc which are
 * managed by the IDM system. Managed Resource can also be forms
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ManagedSys", propOrder = {
    "managedSysId",
    "name",
    "description",
    "status",
    "connectorId",
    "domainId",
    "hostUrl",
    "port",
    "commProtocol",
    "userId",
    "pswd",
    "decryptPassword",
    "endDate",
    "startDate",
    "resourceId",
    "primaryRepository",
    "secondaryRepositoryId",
    "updateSecondary",
    "mngSysObjectMatchs",
    "driverUrl",
    "connectionString",
    "addHandler",
    "modifyHandler",
    "deleteHandler",
    "passwordHandler",
    "suspendHandler",
    "handler1",
    "handler2",
    "handler3",
    "handler4",
    "handler5"
})
@Entity
@Table(name = "MANAGED_SYS")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ManagedSys implements java.io.Serializable {

	/**
	 * 
	 */
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
    @Transient
	private String decryptPassword;
    @XmlSchemaType(name = "dateTime")
    @Column(name="START_DATE",length =10)
    @Temporal(TemporalType.DATE)
	private Date startDate;
    @XmlSchemaType(name = "dateTime")
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

	
	
	//private Set<ApproverAssociation> resourceApprovers = new HashSet<ApproverAssociation>(0);
	/*
	private Set<AttributeMap> systemAttributeMap = new HashSet<AttributeMap>(0);
	*/
    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinColumn(name="MANAGED_SYS_ID", referencedColumnName="MANAGED_SYS_ID")
	private Set<ManagedSystemObjectMatch> mngSysObjectMatchs = new HashSet<ManagedSystemObjectMatch>(0);

	public ManagedSys() {
	}

	public ManagedSys(String managedSysId, String connectorId, String domainId) {
		this.managedSysId = managedSysId;
		this.connectorId = connectorId;
		this.domainId = domainId;
	}

	public ManagedSys(String managedSysId, String name, String description, String status, String connectorId, String domainId, String hostUrl, Integer port, String commProtocol, String userId, String pswd, Date startDate, Date endDate) {
		this.managedSysId = managedSysId;
		this.name = name;
		this.description = description;
		this.status = status;
		this.connectorId = connectorId;
		this.domainId = domainId;
		this.hostUrl = hostUrl;
		this.port = port;
		this.commProtocol = commProtocol;
		this.userId = userId;
		this.pswd = pswd;
		this.startDate = startDate;
		this.endDate = endDate;
		
	}

	public String getManagedSysId() {
		return this.managedSysId;
	}

	public void setManagedSysId(String managedSysId) {
		this.managedSysId = managedSysId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getConnectorId() {
		return this.connectorId;
	}

	public void setConnectorId(String connectorId) {
		this.connectorId = connectorId;
	}

	public String getDomainId() {
		return this.domainId;
	}

	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}

	public String getHostUrl() {
		return this.hostUrl;
	}

	public void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}

	public Integer getPort() {
		return this.port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getCommProtocol() {
		return this.commProtocol;
	}

	public void setCommProtocol(String commProtocol) {
		this.commProtocol = commProtocol;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPswd() {
		return this.pswd;
	}

	public void setPswd(String pswd) {
		this.pswd = pswd;
	}


	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	/**
	 * Return a ManagedSystemObjectMatch for an object type.
	 * Return null is an object for the specified objectType is not found.
	 * @param objectType
	 * @return
	 */
	public ManagedSystemObjectMatch getObjectMatchDetailsByType(String objectType) {
		Set<ManagedSystemObjectMatch> matchSet = getMngSysObjectMatchs();
		if (matchSet == null || matchSet.isEmpty())
			return null;
		Iterator<ManagedSystemObjectMatch> it = matchSet.iterator();
		while (it.hasNext()) {
			ManagedSystemObjectMatch match = it.next();
			if (match.getObjectType().equalsIgnoreCase(objectType)) {
				return match;
			}
		}
		return null;
	}



	public Set<ManagedSystemObjectMatch> getMngSysObjectMatchs() {
		return mngSysObjectMatchs;
	}

	public void setMngSysObjectMatchs(
			Set<ManagedSystemObjectMatch> mngSysObjectMatchs) {
		this.mngSysObjectMatchs = mngSysObjectMatchs;
	}

	public String getDecryptPassword() {
		return decryptPassword;
	}

	public void setDecryptPassword(String decryptPassword) {
		this.decryptPassword = decryptPassword;
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

    @Override
    public String toString() {
        return "ManagedSys{" +
                "managedSysId='" + managedSysId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", connectorId='" + connectorId + '\'' +
                ", domainId='" + domainId + '\'' +
                ", hostUrl='" + hostUrl + '\'' +
                ", port=" + port +
                ", commProtocol='" + commProtocol + '\'' +
                ", userId='" + userId + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", resourceId='" + resourceId + '\'' +
                ", primaryRepository=" + primaryRepository +
                ", secondaryRepositoryId='" + secondaryRepositoryId + '\'' +
                ", updateSecondary=" + updateSecondary +
                ", driverUrl='" + driverUrl + '\'' +
                ", connectionString='" + connectionString + '\'' +
                ", addHandler='" + addHandler + '\'' +
                ", modifyHandler='" + modifyHandler + '\'' +
                ", deleteHandler='" + deleteHandler + '\'' +
                ", passwordHandler='" + passwordHandler + '\'' +
                ", suspendHandler='" + suspendHandler + '\'' +
                ", handler1='" + handler1 + '\'' +
                ", handler2='" + handler2 + '\'' +
                ", handler3='" + handler3 + '\'' +
                ", handler4='" + handler4 + '\'' +
                ", handler5='" + handler5 + '\'' +
                ", mngSysObjectMatchs=" + mngSysObjectMatchs +
                '}';
    }
}
