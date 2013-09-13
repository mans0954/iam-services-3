package org.openiam.idm.srvc.res.domain;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;

@Entity
@Table(name = "RES")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(Resource.class)
public class ResourceEntity {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "RESOURCE_ID", length = 32)
    private String resourceId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RESOURCE_TYPE_ID")
    private ResourceTypeEntity resourceType;

    @Column(name = "NAME", length = 150)
    private String name;

    @Column(name = "DESCRIPTION", length = 100)
    private String description;

    @Column(name = "BRANCH_ID", length = 20)
    private String branchId;

    @Column(name = "CATEGORY_ID", length = 20)
    private String categoryId;

    @Column(name = "DISPLAY_ORDER")
    private Integer displayOrder;

    @Column(name = "MANAGED_SYS_ID")
    private String managedSysId;

    @Column(name = "URL", length = 255)
    private String URL;

    @Column(name = "RES_OWNER_USER_ID")
    private String resOwnerUserId;

    @Column(name = "RES_OWNER_GROUP_ID")
    private String resOwnerGroupId;

    @ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.LAZY)
    @JoinTable(name = "res_to_res_membership",
            joinColumns = {@JoinColumn(name = "MEMBER_RESOURCE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "RESOURCE_ID")})
    @Fetch(FetchMode.SUBSELECT)
    private Set<ResourceEntity> parentResources = new HashSet<ResourceEntity>(0);

    @ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.LAZY)
    @JoinTable(name = "res_to_res_membership",
            joinColumns = {@JoinColumn(name = "RESOURCE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "MEMBER_RESOURCE_ID")})
    @Fetch(FetchMode.SUBSELECT)
    private Set<ResourceEntity> childResources = new HashSet<ResourceEntity>(0);

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("name asc")
    @JoinColumn(name = "RESOURCE_ID")
    @Fetch(FetchMode.SUBSELECT)
    private Set<ResourcePropEntity> resourceProps = new HashSet<ResourcePropEntity>(0); // defined as a Set in Hibernate map

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "RESOURCE_USER", joinColumns = { @JoinColumn(name = "RESOURCE_ID") }, inverseJoinColumns = { @JoinColumn(name = "USER_ID") })
    private Set<UserEntity> users;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "RESOURCE_GROUP", joinColumns = { @JoinColumn(name = "RESOURCE_ID") }, inverseJoinColumns = { @JoinColumn(name = "GRP_ID") })
    private Set<GroupEntity> groups;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "RESOURCE_ROLE", joinColumns = { @JoinColumn(name = "RESOURCE_ID") }, inverseJoinColumns = { @JoinColumn(name = "ROLE_ID") })
    private Set<RoleEntity> roles;

    @Column(name = "MIN_AUTH_LEVEL")
    private String minAuthLevel;

    @Column(name = "DOMAIN")
    private String domain;

    @Column(name = "IS_PUBLIC")
    @Type(type = "yes_no")
    private boolean isPublic = true;

    /*
    @Column(name = "IS_SSL")
    @Type(type = "yes_no")
    private boolean isSSL = false;
    */
    
    public ResourceEntity() {
    }
  
    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public ResourceTypeEntity getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceTypeEntity resourceType) {
        this.resourceType = resourceType;
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

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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

    public void setURL(String URL) {
        this.URL = URL;
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

    public Set<ResourceEntity> getParentResources() {
        return parentResources;
    }

    public void setParentResources(Set<ResourceEntity> parentResources) {
        this.parentResources = parentResources;
    }

    public Set<ResourceEntity> getChildResources() {
        return childResources;
    }

    public void setChildResources(Set<ResourceEntity> childResources) {
        this.childResources = childResources;
    }

    public Set<ResourcePropEntity> getResourceProps() {
        return resourceProps;
    }

    public void setResourceProps(Set<ResourcePropEntity> resourceProps) {
        this.resourceProps = resourceProps;
    }

    public Set<GroupEntity> getGroups() {
        return groups;
    }

    public void setGroups(Set<GroupEntity> groups) {
        this.groups = groups;
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

    public void setIsPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    /*
    public boolean getIsSSL() {
        return isSSL;
    }

    public void setIsSSL(boolean SSL) {
        isSSL = SSL;
    }
    */
    
    public Set<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(Set<UserEntity> users) {
        this.users = users;
    }

    public void addParentResource(final ResourceEntity resource) {
    	if(resource != null) {
    		if(this.parentResources == null) {
    			this.parentResources = new LinkedHashSet<ResourceEntity>();
    		}
    		this.parentResources.add(resource);
    	}
    }
    
	public void addChildResource(final ResourceEntity resource) {
		if(resource != null) {
			if(this.childResources == null) {
				this.childResources = new LinkedHashSet<ResourceEntity>();
			}
			this.childResources.add(resource);
		}
	}
	
	public boolean hasChildResoruce(final ResourceEntity entity) {
		boolean contains = false;
		if(childResources != null) {
			contains = childResources.contains(entity);
		}
		return contains;
	}
	
	public void removeChildResource(final String resourceId) {
		if(resourceId != null && childResources != null) {
			for(final Iterator<ResourceEntity> it = childResources.iterator(); it.hasNext();) {
				final ResourceEntity resource = it.next();
				if(resource.getResourceId().equals(resourceId)) {
					it.remove();
					break;
				}
			}
		}
	}
	
	public void removeChildResource(final ResourceEntity resource) {
		if(resource != null) {
			if(this.childResources != null) {
				this.childResources.remove(resource);
			}
		}
	}
	
    public ResourcePropEntity getResourceProperty(String propName) {
        if (resourceProps == null) {
            return null;
        }
        for (ResourcePropEntity prop : resourceProps) {
            if (prop.getName().equalsIgnoreCase(propName)) {
                return prop;
            }
        }
        return null;
    }
    
    public void addResourceProperty(final ResourcePropEntity property) {
    	if(this.resourceProps == null) {
    		this.resourceProps = new LinkedHashSet<ResourcePropEntity>();
    	}
    	this.resourceProps.add(property);
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
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceEntity that = (ResourceEntity) o;

        if (isPublic != that.isPublic) return false;
        //if (isSSL != that.isSSL) return false;
        if (URL != null ? !URL.equals(that.URL) : that.URL != null) return false;
        if (branchId != null ? !branchId.equals(that.branchId) : that.branchId != null) return false;
        if (categoryId != null ? !categoryId.equals(that.categoryId) : that.categoryId != null) return false;
        if (domain != null ? !domain.equals(that.domain) : that.domain != null) return false;
        if (managedSysId != null ? !managedSysId.equals(that.managedSysId) : that.managedSysId != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (resourceId != null ? !resourceId.equals(that.resourceId) : that.resourceId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = resourceId != null ? resourceId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (branchId != null ? branchId.hashCode() : 0);
        result = 31 * result + (categoryId != null ? categoryId.hashCode() : 0);
        result = 31 * result + (managedSysId != null ? managedSysId.hashCode() : 0);
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        result = 31 * result + (isPublic ? 1 : 0);
        //result = 31 * result + (isSSL ? 1 : 0)
        return result;
    }
}
