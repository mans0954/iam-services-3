package org.openiam.idm.srvc.res.dto;

// Generated Mar 8, 2009 12:54:32 PM by Hibernate Tools 3.2.2.GA

import org.openiam.base.BaseObject;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.dto.User;

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
        "managedSysId",
        "URL",
        "roles",
        "resourceProps",
        "groups",
        "users",
        "resOwnerUserId",
        "resOwnerGroupId",
        "childResources",
        "parentResources",
        "minAuthLevel",
        "domain",
        "isPublic",
        "isSSL"
})
@XmlSeeAlso({
        Role.class,
        User.class
})
@DozerDTOCorrespondence(ResourceEntity.class)
public class Resource extends BaseObject {

    private String resourceId;
    private ResourceType resourceType;
    private String name;
    private String description;
    private String branchId;
    private String categoryId;
    private Integer displayOrder;
    private String managedSysId;
    private String URL;

    private String resOwnerUserId;
    private String resOwnerGroupId;

    private Set<Resource> parentResources = new HashSet<Resource>(0);
    private Set<Resource> childResources = new HashSet<Resource>(0);

    private Set<Role> roles = new HashSet<Role>(0);

    private Set<ResourceProp> resourceProps = new HashSet<ResourceProp>(0); // defined as a Set in Hibernate map

    private Set<Group> groups = new HashSet<Group>(0);
    private Set<User> users = new HashSet<User>(0);
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

    public String getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public ResourceType getResourceType() {
        return this.resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBranchId() {
        return this.branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Set<ResourceProp> getResourceProps() {
        return resourceProps;
    }

    public void setResourceProps(Set<ResourceProp> resourceProps) {
        this.resourceProps = resourceProps;
    }

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
    
    public void addParentResource(final Resource resource) {
    	if(this.parentResources == null) {
    		this.parentResources = new LinkedHashSet<Resource>();
    	}
    	this.parentResources.add(resource);
    }
    
	public String getMinAuthLevel() {
		return minAuthLevel;
	}

	public void setMinAuthLevel(String minAuthLevel) {
		this.minAuthLevel = minAuthLevel;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public boolean getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	
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
                ", managedSysId='" + managedSysId + '\'' +
                ", URL='" + URL + '\'' +
                ", resOwnerUserId='" + resOwnerUserId + '\'' +
                ", resOwnerGroupId='" + resOwnerGroupId + '\'' +
                /*", entitlements=" + entitlements +*/
                '}';
    }

    public String getManagedSysId() {
        return managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String uRL) {
        URL = uRL;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public String getResOwnerUserId() {
        return resOwnerUserId;
    }

    public void setResOwnerUserId(String resOwnerUserId) {
        this.resOwnerUserId = resOwnerUserId;
    }

    public String getResOwnerGroupId() {
        return resOwnerGroupId;
    }

    public void setResOwnerGroupId(String resOwnerGroupId) {
        this.resOwnerGroupId = resOwnerGroupId;
    }

    /*
    public Set<ResourcePrivilege> getEntitlements() {
        return entitlements;
    }

    public void setEntitlements(Set<ResourcePrivilege> entitlements) {
        this.entitlements = entitlements;
    }
    */

	public Set<Resource> getParentResources() {
		return parentResources;
	}

	public void setParentResources(Set<Resource> parentResources) {
		this.parentResources = parentResources;
	}

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
