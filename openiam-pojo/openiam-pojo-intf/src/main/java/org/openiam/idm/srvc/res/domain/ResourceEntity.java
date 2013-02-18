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
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceGroup;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.dto.ResourceRole;
import org.openiam.idm.srvc.res.dto.ResourceType;

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
    @JoinColumn(name = "RESOURCE_ID")
    @Fetch(FetchMode.SUBSELECT)
    private Set<ResourceRoleEntity> resourceRoles = new HashSet<ResourceRoleEntity>(0);

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("name asc")
    @JoinColumn(name = "RESOURCE_ID")
    @Fetch(FetchMode.SUBSELECT)
    private Set<ResourcePropEntity> resourceProps = new HashSet<ResourcePropEntity>(0); // defined as a Set in Hibernate map

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "RESOURCE_ID")
    @Fetch(FetchMode.SUBSELECT)
    private Set<ResourceGroupEntity> resourceGroups = new HashSet<ResourceGroupEntity>(0);

    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name="RESOURCE_ID", referencedColumnName="RESOURCE_ID")
    @Fetch(FetchMode.SUBSELECT)
    private Set<ResourceUserEntity> resourceUsers = new HashSet<ResourceUserEntity>();

    @Column(name = "MIN_AUTH_LEVEL")
    private String minAuthLevel;

    @Column(name = "DOMAIN")
    private String domain;

    @Column(name = "IS_PUBLIC")
    @Type(type = "yes_no")
    private boolean isPublic = true;

    @Column(name = "IS_SSL")
    @Type(type = "yes_no")
    private boolean isSSL = false;
    
    public ResourceEntity() {
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

    public Set<ResourceRoleEntity> getResourceRoles() {
        return resourceRoles;
    }

    public void setResourceRoles(Set<ResourceRoleEntity> resourceRoles) {
        this.resourceRoles = resourceRoles;
    }

    public Set<ResourcePropEntity> getResourceProps() {
        return resourceProps;
    }

    public void setResourceProps(Set<ResourcePropEntity> resourceProps) {
        this.resourceProps = resourceProps;
    }

    public Set<ResourceGroupEntity> getResourceGroups() {
        return resourceGroups;
    }

    public void setResourceGroups(Set<ResourceGroupEntity> resourceGroups) {
        this.resourceGroups = resourceGroups;
    }

   public void addResourceGroup(final ResourceGroupEntity entity) {
	   if(entity != null) {
		   if(resourceGroups == null) {
			   this.resourceGroups = new HashSet<ResourceGroupEntity>();
		   }
		   this.resourceGroups.add(entity);
 	   }
   }
   
   public void removeResourceGroup(final ResourceGroupEntity entity) {
	   if(entity != null) {
		   if(resourceGroups != null) {
			   for(final Iterator<ResourceGroupEntity> it = resourceGroups.iterator(); it.hasNext();) {
				   final ResourceGroupEntity rge = it.next();
				   if(rge.getGroupId().equals(entity.getGroupId()) && rge.getResourceId().equals(entity.getResourceId())) {
					   it.remove();
					   break;
				   }
			   }
		   }
	   }
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

    public boolean getIsSSL() {
        return isSSL;
    }

    public void setIsSSL(boolean SSL) {
        isSSL = SSL;
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

    public Set<ResourceUserEntity> getResourceUsers() {
		return resourceUsers;
	}

	public void setResourceUsers(Set<ResourceUserEntity> resourceUsers) {
		this.resourceUsers = resourceUsers;
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
        if (isSSL != that.isSSL) return false;
        if (URL != null ? !URL.equals(that.URL) : that.URL != null) return false;
        if (branchId != null ? !branchId.equals(that.branchId) : that.branchId != null) return false;
        if (categoryId != null ? !categoryId.equals(that.categoryId) : that.categoryId != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (displayOrder != null ? !displayOrder.equals(that.displayOrder) : that.displayOrder != null) return false;
        if (domain != null ? !domain.equals(that.domain) : that.domain != null) return false;
        if (managedSysId != null ? !managedSysId.equals(that.managedSysId) : that.managedSysId != null) return false;
        if (minAuthLevel != null ? !minAuthLevel.equals(that.minAuthLevel) : that.minAuthLevel != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (resOwnerGroupId != null ? !resOwnerGroupId.equals(that.resOwnerGroupId) : that.resOwnerGroupId != null)
            return false;
        if (resOwnerUserId != null ? !resOwnerUserId.equals(that.resOwnerUserId) : that.resOwnerUserId != null)
            return false;
        if (resourceId != null ? !resourceId.equals(that.resourceId) : that.resourceId != null) return false;
        if (resourceType != null ? !resourceType.equals(that.resourceType) : that.resourceType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = resourceId != null ? resourceId.hashCode() : 0;
        result = 31 * result + (resourceType != null ? resourceType.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (branchId != null ? branchId.hashCode() : 0);
        result = 31 * result + (categoryId != null ? categoryId.hashCode() : 0);
        result = 31 * result + (displayOrder != null ? displayOrder.hashCode() : 0);
        result = 31 * result + (managedSysId != null ? managedSysId.hashCode() : 0);
        result = 31 * result + (URL != null ? URL.hashCode() : 0);
        result = 31 * result + (resOwnerUserId != null ? resOwnerUserId.hashCode() : 0);
        result = 31 * result + (resOwnerGroupId != null ? resOwnerGroupId.hashCode() : 0);
        result = 31 * result + (minAuthLevel != null ? minAuthLevel.hashCode() : 0);
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        result = 31 * result + (isPublic ? 1 : 0);
        result = 31 * result + (isSSL ? 1 : 0);
        return result;
    }
}
