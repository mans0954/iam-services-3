package org.openiam.idm.searchbeans;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.KeyNameDTO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by: Alexander Duckardt
 * Date: 8/4/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EntitlementsSearchBean", propOrder = {
        "parentIdSet",
        "childIdSet",
        "groupIdSet",
        "roleIdSet",
        "resourceIdSet",
        "userIdSet",
        "organizationIdSet",
        "includeAccessRights"
})
public abstract class EntitlementsSearchBean<T extends KeyNameDTO, KeyType extends Serializable> extends AbstractKeyNameSearchBean<T, KeyType> {
    private Set<String> parentIdSet;
    private Set<String> childIdSet;

    /** requires custom service logic */
    protected boolean includeAccessRights;

    /**
     * Set of Group IDs that this object belongs to.
     * The obj must belong to at leat one of these groups
     */
    protected Set<String> groupIdSet;
    /**
     * Set of Role IDs that this object belongs to.
     * The obj must belong to at least one of these roles
     */
    protected Set<String> roleIdSet;
    /**
     * Set of Resource IDs that this object is entitled to.
     * The obj must be entitled to at least one of these
     */
    protected Set<String> resourceIdSet;
    protected Set<String> userIdSet;

    /**
     * Set of Organization IDs that this object belongs to.
     * The object must belong to at leat one of these organizations
     */
    protected Set<String> organizationIdSet;

    public Set<String> getParentIdSet() {
        return parentIdSet;
    }

    public void setParentIdSet(Set<String> parentIdSet) {
        this.parentIdSet = parentIdSet;
    }

    public Set<String> getChildIdSet() {
        return childIdSet;
    }

    public void setChildIdSet(Set<String> childIdSet) {
        this.childIdSet = childIdSet;
    }

    public Set<String> getGroupIdSet() {
        return groupIdSet;
    }

    public void setGroupIdSet(Set<String> groupIdSet) {
        this.groupIdSet = groupIdSet;
    }

    public Set<String> getRoleIdSet() {
        return roleIdSet;
    }

    public void setRoleIdSet(Set<String> roleIdSet) {
        this.roleIdSet = roleIdSet;
    }

    public Set<String> getResourceIdSet() {
        return resourceIdSet;
    }

    public void setResourceIdSet(Set<String> resourceIdSet) {
        this.resourceIdSet = resourceIdSet;
    }

    public Set<String> getUserIdSet() {
        return userIdSet;
    }

    public void setUserIdSet(Set<String> userIdSet) {
        this.userIdSet = userIdSet;
    }

    public Set<String> getOrganizationIdSet() {
        return organizationIdSet;
    }

    public void setOrganizationIdSet(Set<String> organizationIdSet) {
        this.organizationIdSet = organizationIdSet;
    }

    public boolean isIncludeAccessRights() {
		return includeAccessRights;
	}

	public void setIncludeAccessRights(boolean includeAccessRights) {
		this.includeAccessRights = includeAccessRights;
	}

	public void addParentId(String parentId){
        if(StringUtils.isNotBlank(parentId)){
            if(this.parentIdSet==null)
                this.parentIdSet = new HashSet<>();
            this.parentIdSet.add(parentId);
        }
    }
    public void addChildId(String childId){
        if(StringUtils.isNotBlank(childId)){
            if(this.childIdSet==null)
                this.childIdSet = new HashSet<>();
            this.childIdSet.add(childId);
        }
    }


    public void addGroupId(String groupId){
        if(StringUtils.isNotBlank(groupId)){
            if(this.groupIdSet==null)
                this.groupIdSet = new HashSet<>();
            this.groupIdSet.add(groupId);
        }
    }
    public void addRoleId(String roleId){
        if(StringUtils.isNotBlank(roleId)){
            if(this.roleIdSet==null)
                this.roleIdSet = new HashSet<>();
            this.roleIdSet.add(roleId);
        }
    }
    public void addResourceId(String resourceId){
        if(StringUtils.isNotBlank(resourceId)){
            if(this.resourceIdSet==null)
                this.resourceIdSet = new HashSet<>();
            this.resourceIdSet.add(resourceId);
        }
    }
    public void addUserId(String userId){
        if(StringUtils.isNotBlank(userId)){
            if(this.userIdSet==null)
                this.userIdSet = new HashSet<>();
            this.userIdSet.add(userId);
        }
    }
    public void addOrganizationId(String organizationId){
        if(StringUtils.isNotBlank(organizationId)){
            if(this.organizationIdSet==null)
                this.organizationIdSet = new HashSet<>();
            this.organizationIdSet.add(organizationId);
        }
    }

    public void addOrganizationIdList(final Collection<String> organizationIdList) {
        if(organizationIdList != null) {
            if(this.organizationIdSet==null) {
                this.organizationIdSet = new HashSet<String>();
            }
            this.organizationIdSet.addAll(organizationIdList);
        }
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((childIdSet == null) ? 0 : childIdSet.hashCode());
		result = prime * result
				+ ((groupIdSet == null) ? 0 : groupIdSet.hashCode());
		result = prime * result + (includeAccessRights ? 1231 : 1237);
		result = prime
				* result
				+ ((organizationIdSet == null) ? 0 : organizationIdSet
						.hashCode());
		result = prime * result
				+ ((parentIdSet == null) ? 0 : parentIdSet.hashCode());
		result = prime * result
				+ ((resourceIdSet == null) ? 0 : resourceIdSet.hashCode());
		result = prime * result
				+ ((roleIdSet == null) ? 0 : roleIdSet.hashCode());
		result = prime * result
				+ ((userIdSet == null) ? 0 : userIdSet.hashCode());
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
		EntitlementsSearchBean other = (EntitlementsSearchBean) obj;
		if (childIdSet == null) {
			if (other.childIdSet != null)
				return false;
		} else if (!childIdSet.equals(other.childIdSet))
			return false;
		if (groupIdSet == null) {
			if (other.groupIdSet != null)
				return false;
		} else if (!groupIdSet.equals(other.groupIdSet))
			return false;
		if (includeAccessRights != other.includeAccessRights)
			return false;
		if (organizationIdSet == null) {
			if (other.organizationIdSet != null)
				return false;
		} else if (!organizationIdSet.equals(other.organizationIdSet))
			return false;
		if (parentIdSet == null) {
			if (other.parentIdSet != null)
				return false;
		} else if (!parentIdSet.equals(other.parentIdSet))
			return false;
		if (resourceIdSet == null) {
			if (other.resourceIdSet != null)
				return false;
		} else if (!resourceIdSet.equals(other.resourceIdSet))
			return false;
		if (roleIdSet == null) {
			if (other.roleIdSet != null)
				return false;
		} else if (!roleIdSet.equals(other.roleIdSet))
			return false;
		if (userIdSet == null) {
			if (other.userIdSet != null)
				return false;
		} else if (!userIdSet.equals(other.userIdSet))
			return false;
		return true;
	}
    
    
}
