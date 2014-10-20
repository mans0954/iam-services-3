package org.openiam.idm.srvc.mngsys.dto;

// Generated Nov 3, 2008 12:14:43 AM by Hibernate Tools 3.2.2.GA

import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.constants.SearchScopeType;
import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysRuleEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Domain object representing a managed resource. Managed systems include items
 * such as AD, LDAP, etc which are managed by the IDM system. Managed Resource
 * can also be forms
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ManagedSysDto", propOrder = {"description", "status", "connectorId", "hostUrl", "port",
        "commProtocol", "userId", "pswd", "decryptPassword", "endDate", "startDate", "attributeNamesLookup",
        "searchScope", "resource", "primaryRepository", "secondaryRepositoryId", "updateSecondary",
        "mngSysObjectMatchs", "driverUrl", "connectionString", "addHandler", "modifyHandler", "deleteHandler",
        "passwordHandler", "suspendHandler", "resumeHandler", "searchHandler", "lookupHandler",
        "testConnectionHandler", "reconcileResourceHandler", "attributeNamesHandler", "handler5", "rules", "groups",
        "roles" })
@DozerDTOCorrespondence(ManagedSysEntity.class)
public class ManagedSysDto extends KeyNameDTO {

    /**
	 * 
	 */
    private static final long serialVersionUID = -648884785253890053L;
    private String description;
    private String status;
    private String connectorId;
    private String hostUrl;
    private Integer port;
    private String commProtocol;
    private String userId;
    private String pswd;
    private String decryptPassword;
    private Date startDate;
    @XmlSchemaType(name = "dateTime")
    private Date endDate;
    private String attributeNamesLookup;
    private SearchScopeType searchScope = SearchScopeType.SUBTREE_SCOPE;
    private Resource resource;
    private Integer primaryRepository;
    private String secondaryRepositoryId;
    private Integer updateSecondary;
    private String driverUrl;
    private String connectionString;
    private String addHandler;
    private String modifyHandler;
    private String deleteHandler;
    private String passwordHandler;
    private String suspendHandler;
    private String resumeHandler;
    private String searchHandler;
    private String lookupHandler;
    private String testConnectionHandler;
    private String reconcileResourceHandler;
    private String attributeNamesHandler;
    private String handler5;

    private List<ManagedSysRuleDto> rules = new ArrayList<ManagedSysRuleDto>(0);

    // private Set<ApproverAssociation> resourceApprovers = new
    // HashSet<ApproverAssociation>(0);
    /*
     * private Set<AttributeMap> systemAttributeMap = new
     * HashSet<AttributeMap>(0);
     */
    private Set<ManagedSystemObjectMatch> mngSysObjectMatchs = new HashSet<ManagedSystemObjectMatch>(0);

    private Set<Group> groups;

    private Set<Role> roles;

    public ManagedSysDto() {
    }

    @Deprecated
    public ManagedSysDto(String id, String connectorId) {
        setId(id);
        this.connectorId = connectorId;
    }

    @Deprecated
    public ManagedSysDto(String id, String name, String description, String status, String connectorId, String hostUrl,
            Integer port, String commProtocol, String userId, String pswd, Date startDate, Date endDate) {
        setId(id);
        setName(name);
        this.description = description;
        this.status = status;
        this.connectorId = connectorId;
        this.hostUrl = hostUrl;
        this.port = port;
        this.commProtocol = commProtocol;
        this.userId = userId;
        this.pswd = pswd;
        this.startDate = startDate;
        this.endDate = endDate;

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
     * Return a ManagedSystemObjectMatch for an object type. Return null is an
     * object for the specified objectType is not found.
     * 
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

    public void removeManagedSysObjectMatch(final ManagedSystemObjectMatch match) {
        if (match != null && this.mngSysObjectMatchs != null) {
            for (final Iterator<ManagedSystemObjectMatch> it = this.mngSysObjectMatchs.iterator(); it.hasNext();) {
                final ManagedSystemObjectMatch next = it.next();
                if (StringUtils.equals(next.getObjectSearchId(), match.getObjectSearchId())) {
                    it.remove();
                }
            }
        }
    }

    public void addManagedSysObjectMatch(final ManagedSystemObjectMatch match) {
        if (match != null) {
            if (this.mngSysObjectMatchs == null) {
                this.mngSysObjectMatchs = new HashSet<ManagedSystemObjectMatch>();
            }
            this.mngSysObjectMatchs.add(match);
        }
    }

    public Set<ManagedSystemObjectMatch> getMngSysObjectMatchs() {
        return mngSysObjectMatchs;
    }

    public void setMngSysObjectMatchs(Set<ManagedSystemObjectMatch> mngSysObjectMatchs) {
        this.mngSysObjectMatchs = mngSysObjectMatchs;
    }

    public String getDecryptPassword() {
        return decryptPassword;
    }

    public void setDecryptPassword(String decryptPassword) {
        this.decryptPassword = decryptPassword;
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

    /* use getResource/setResource */
    @Deprecated
    public String getResourceId() {
        return (resource != null) ? resource.getId() : null;
    }

    /* use getResource/setResource */
    @Deprecated
    public void setResourceId(String resourceId) {
        if(resource != null) {
        	resource.setId(resourceId);
        }
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

    public List<ManagedSysRuleDto> getRules() {
        return rules;
    }

    public void setRules(List<ManagedSysRuleDto> rules) {
        this.rules = rules;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
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
				+ ((decryptPassword == null) ? 0 : decryptPassword.hashCode());
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
		ManagedSysDto other = (ManagedSysDto) obj;
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
		if (decryptPassword == null) {
			if (other.decryptPassword != null)
				return false;
		} else if (!decryptPassword.equals(other.decryptPassword))
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
