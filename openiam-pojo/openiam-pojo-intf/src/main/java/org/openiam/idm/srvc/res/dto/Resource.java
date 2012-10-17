package org.openiam.idm.srvc.res.dto;

// Generated Mar 8, 2009 12:54:32 PM by Hibernate Tools 3.2.2.GA

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Type;
import org.openiam.base.BaseObject;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.HashSet;
import java.util.Set;

/**
 * Resources are items that need to be managed or protected. These can be both logic and physical in nature.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Resource", propOrder = {
        "resourceType",
        "resourceId",
        "name",
        "description",
        "branchId",
        "categoryId",
        "displayOrder",
        "nodeLevel",
        "sensitiveApp",
        "managedSysId",
        "URL",
        "resourceRoles",
        "resourceProps",
        "resourceGroups",
        "entitlements",
        "resOwnerUserId",
        "resOwnerGroupId",
        "childResources",
        "parentResources",
        "minAuthLevel",
        "domain",
        "isPublic",
        "isSSL"
})
@Entity
@Table(name="RES")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Resource extends BaseObject {

    private String resourceId;
    private ResourceType resourceType;
    private String name;
    private String description;
    private String branchId;
    private String categoryId;
    private Integer displayOrder;
    private Integer nodeLevel;
    private Integer sensitiveApp;
    private String managedSysId;
    private String URL;

    private String resOwnerUserId;
    private String resOwnerGroupId;

    private Set<Resource> parentResources;
    private Set<Resource> childResources;

    private Set<ResourceRole> resourceRoles = new HashSet<ResourceRole>(0);

    private Set<ResourceProp> resourceProps = new HashSet<ResourceProp>(0); // defined as a Set in Hibernate map

    private Set<ResourceGroup> resourceGroups = new HashSet<ResourceGroup>(0);

    private Set<ResourcePrivilege> entitlements = new HashSet<ResourcePrivilege>(0);
    
    private String minAuthLevel;
    private String domain;
    private boolean isPublic = true;
    private boolean isSSL = false;


    public Resource() {
    }

    public Resource(String resourceId) {
        this.resourceId = resourceId;
    }

    public Resource(String resourceId, String managedSysId) {
        this.resourceId = resourceId;
        this.managedSysId = managedSysId;
    }

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="RESOURCE_ID", length=32)
    public String getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="RESOURCE_TYPE_ID")
    public ResourceType getResourceType() {
        return this.resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    @Column(name="DESCRIPTION",length=100)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name="NAME",length=40)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="BRANCH_ID",length=20)
    public String getBranchId() {
        return this.branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    @Column(name="CATEGORY_ID",length=20)
    public String getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @Column(name="DISPLAY_ORDER")
    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
    
    @Column(name="NODE_LEVEL")
    public Integer getNodeLevel() {
        return this.nodeLevel;
    }

    public void setNodeLevel(Integer nodeLevel) {
        this.nodeLevel = nodeLevel;
    }

    @Column(name="SENSITIVE_APP")
    public Integer getSensitiveApp() {
        return this.sensitiveApp;
    }

    public void setSensitiveApp(Integer sensitiveApp) {
        this.sensitiveApp = sensitiveApp;
    }

	@OneToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name="RESOURCE_ID")
    public Set<ResourceRole> getResourceRoles() {
        return this.resourceRoles;
    }

    public void setResourceRoles(Set<ResourceRole> resourceRoles) {
        this.resourceRoles = resourceRoles;
    }

	@OneToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
	@OrderBy("name asc")
	@JoinColumn(name="RESOURCE_ID")
    public Set<ResourceProp> getResourceProps() {
        return resourceProps;
    }

    public void setResourceProps(Set<ResourceProp> resourceProps) {
        this.resourceProps = resourceProps;
    }

    @Transient
    public ResourceProp getResourceProperty(String propName) {
        if (resourceProps == null) {
            return null;
        }
        for (ResourceProp prop : resourceProps) {
            if (prop.getName().equalsIgnoreCase(propName)) {
                return prop;
            }
        }
        return null;
    }
    
    @Column(name="MIN_AUTH_LEVEL")
	public String getMinAuthLevel() {
		return minAuthLevel;
	}

	public void setMinAuthLevel(String minAuthLevel) {
		this.minAuthLevel = minAuthLevel;
	}

	@Column(name="DOMAIN")
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	@Column(name="IS_PUBLIC")
	@Type(type="yes_no")
	public boolean getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	
	@Column(name="IS_SSL")
	@Type(type="yes_no")
	public boolean getIsSSL() {
		return this.isSSL;
	}
	
	public void setIsSSL(final boolean isSSL) {
		this.isSSL = isSSL;
	}

	@Override
    public String toString() {
        return "Resource{" +
                "resourceId='" + resourceId + '\'' +
                ", resourceType=" + resourceType +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", branchId='" + branchId + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", displayOrder=" + displayOrder +
                ", nodeLevel=" + nodeLevel +
                ", sensitiveApp=" + sensitiveApp +
                ", managedSysId='" + managedSysId + '\'' +
                ", URL='" + URL + '\'' +
                ", resOwnerUserId='" + resOwnerUserId + '\'' +
                ", resOwnerGroupId='" + resOwnerGroupId + '\'' +
                ", resourceRoles=" + resourceRoles +
                ", resourceProps=" + resourceProps +
                ", resourceGroups=" + resourceGroups +
                ", entitlements=" + entitlements +
                '}';
    }

	@Column(name="MANAGED_SYS_ID")
    public String getManagedSysId() {
        return managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
    }

    @Column(name="URL",length=255)
    public String getURL() {
        return URL;
    }

    public void setURL(String uRL) {
        URL = uRL;
    }

    @Transient
    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

	@OneToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name="RESOURCE_ID")
    public Set<ResourceGroup> getResourceGroups() {
        return resourceGroups;
    }

    public void setResourceGroups(Set<ResourceGroup> resourceGroups) {
        this.resourceGroups = resourceGroups;
    }

    @Column(name="RES_OWNER_USER_ID")
    public String getResOwnerUserId() {
        return resOwnerUserId;
    }

    public void setResOwnerUserId(String resOwnerUserId) {
        this.resOwnerUserId = resOwnerUserId;
    }

    @Column(name="RES_OWNER_GROUP_ID")
    public String getResOwnerGroupId() {
        return resOwnerGroupId;
    }

    public void setResOwnerGroupId(String resOwnerGroupId) {
        this.resOwnerGroupId = resOwnerGroupId;
    }

	@OneToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name="RESOURCE_ID")
    public Set<ResourcePrivilege> getEntitlements() {
        return entitlements;
    }

    public void setEntitlements(Set<ResourcePrivilege> entitlements) {
        this.entitlements = entitlements;
    }

	@ManyToMany(cascade={CascadeType.ALL},fetch=FetchType.LAZY)
	@JoinTable(name="res_to_res_membership",
	    joinColumns={@JoinColumn(name="MEMBER_RESOURCE_ID")},
	    inverseJoinColumns={@JoinColumn(name="RESOURCE_ID")})
	@Fetch(FetchMode.SUBSELECT)
	public Set<Resource> getParentResources() {
		return parentResources;
	}

	public void setParentResources(Set<Resource> parentResources) {
		this.parentResources = parentResources;
	}

	@ManyToMany(cascade={CascadeType.ALL},fetch=FetchType.LAZY)
    @JoinTable(name="res_to_res_membership",
        joinColumns={@JoinColumn(name="RESOURCE_ID")},
        inverseJoinColumns={@JoinColumn(name="MEMBER_RESOURCE_ID")})
    @Fetch(FetchMode.SUBSELECT)
	public Set<Resource> getChildResources() {
		return childResources;
	}

	public void setChildResources(Set<Resource> childResources) {
		this.childResources = childResources;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
		return result;
	}
}
