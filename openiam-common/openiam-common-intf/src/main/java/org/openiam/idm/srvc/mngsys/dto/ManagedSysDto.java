package org.openiam.idm.srvc.mngsys.dto;

// Generated Nov 3, 2008 12:14:43 AM by Hibernate Tools 3.2.2.GA

import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.constants.SearchScopeType;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysRuleEntity;
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
@XmlType(name = "ManagedSysDto", propOrder = { "id", "name",
        "description", "status", "connectorId", "hostUrl", "port",
        "commProtocol", "userId", "pswd", "decryptPassword", "endDate",
        "startDate", "attributeNamesLookup", "searchScope", "resourceId", "primaryRepository",
        "secondaryRepositoryId", "updateSecondary", "mngSysObjectMatchs",
        "driverUrl", "connectionString", "addHandler", "modifyHandler",
        "deleteHandler", "passwordHandler", "suspendHandler", "searchHandler",
        "lookupHandler", "testConnectionHandler", "reconcileResourceHandler",
        "attributeNamesHandler", "handler5", "rules", "groups", "roles" })
@DozerDTOCorrespondence(ManagedSysEntity.class)
public class ManagedSysDto implements java.io.Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -648884785253890053L;
    private String id;
    private String name;
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
    private String resourceId;
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
    private Set<ManagedSystemObjectMatch> mngSysObjectMatchs = new HashSet<ManagedSystemObjectMatch>(
            0);
    
    private Set<Group> groups;
    
    private Set<Role> roles;

    public ManagedSysDto() {
    }

    public ManagedSysDto(String id, String connectorId) {
        this.id = id;
        this.connectorId = connectorId;
    }

    public ManagedSysDto(String id, String name, String description,
            String status, String connectorId,  String hostUrl,
            Integer port, String commProtocol, String userId, String pswd,
            Date startDate, Date endDate) {
        this.id = id;
        this.name = name;
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

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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
    public ManagedSystemObjectMatch getObjectMatchDetailsByType(
            String objectType) {
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
    	if(match != null && this.mngSysObjectMatchs != null) {
    		for(final Iterator<ManagedSystemObjectMatch> it = this.mngSysObjectMatchs.iterator(); it.hasNext();) {
    			final ManagedSystemObjectMatch next = it.next();
    			if(StringUtils.equals(next.getObjectSearchId(), match.getObjectSearchId())) {
    				it.remove();
    			}
    		}
    	}
    }
    
    public void addManagedSysObjectMatch(final ManagedSystemObjectMatch match) {
    	if(match != null) {
    		if(this.mngSysObjectMatchs == null) {
    			this.mngSysObjectMatchs = new HashSet<ManagedSystemObjectMatch>();
    		}
    		this.mngSysObjectMatchs.add(match);
    	}
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

    @Override
    public String toString() {
        return "ManagedSysDto{" + "managedSysId='" + id + '\''
                + ", name='" + name + '\'' + ", description='" + description
                + '\'' + ", status='" + status + '\'' + ", connectorId='"
                + connectorId + '\'' + ", hostUrl='" + hostUrl + '\'' + ", port=" + port
                + ", commProtocol='" + commProtocol + '\'' + ", userId='"
                + userId + '\'' + ", pswd='" + pswd + '\''
                + ", decryptPassword='" + decryptPassword + '\''
                + ", startDate=" + startDate + ", endDate=" + endDate
                + ", attributeNamesLookup='" + attributeNamesLookup + '\''
                + ", searchScope='" + searchScope + '\''
                + ", resourceId='" + resourceId + '\'' + ", primaryRepository="
                + primaryRepository + ", secondaryRepositoryId='"
                + secondaryRepositoryId + '\'' + ", updateSecondary="
                + updateSecondary + ", driverUrl='" + driverUrl + '\''
                + ", connectionString='" + connectionString + '\''
                + ", addHandler='" + addHandler + '\'' + ", modifyHandler='"
                + modifyHandler + '\'' + ", deleteHandler='" + deleteHandler
                + '\'' + ", passwordHandler='" + passwordHandler + '\''
                + ", suspendHandler='" + suspendHandler + '\''
                + ", searchHandler='" + searchHandler + '\''
                + ", lookupHandler='" + lookupHandler + '\''
                + ", testConnectionHandler='" + testConnectionHandler + '\''
                + ", reconcileResourceHandler='" + reconcileResourceHandler
                + ", attributeNamesHandler='" + attributeNamesHandler
                + '\'' + ", handler5='" + handler5 + '\''
                + ", mngSysObjectMatchs=" + mngSysObjectMatchs + '}';
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
    
    
}
