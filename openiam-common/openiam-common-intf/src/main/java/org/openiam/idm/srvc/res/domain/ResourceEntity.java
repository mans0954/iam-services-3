package org.openiam.idm.srvc.res.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.openiam.base.domain.AbstractMetdataTypeEntity;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceRisk;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.internationalization.Internationalized;
import org.openiam.internationalization.InternationalizedCollection;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "RES")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ResourceEntity")
@DozerDTOCorrespondence(Resource.class)
@AttributeOverride(name = "id", column = @Column(name = "RESOURCE_ID"))
@Internationalized
public class ResourceEntity extends AbstractMetdataTypeEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RESOURCE_TYPE_ID")
    @Internationalized
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private ResourceTypeEntity resourceType;

    @Column(name = "NAME", length = 255)
    @Size(max = 255, message = "resource.name.too.long")
    private String name;

    @Column(name = "DESCRIPTION", length = 512)
    @Size(max = 512, message = "resource.description.too.long")
    private String description;

    @Column(name = "DISPLAY_ORDER")
    private Integer displayOrder;

    @Column(name = "URL", length = 255)
    @Size(max = 255, message = "resource.url.too.long")
    private String URL;

    @Column(name = "RISK", length = 10)
    @Enumerated(EnumType.STRING)
    private ResourceRisk risk;

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

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="resource", orphanRemoval=true)
    @OrderBy("name asc")
    //@JoinColumn(name = "RESOURCE_ID")
    @Fetch(FetchMode.SUBSELECT)
    @Internationalized
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<ResourcePropEntity> resourceProps = new HashSet<ResourcePropEntity>(0); // defined as a Set in Hibernate map

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "RESOURCE_USER", joinColumns = { @JoinColumn(name = "RESOURCE_ID") }, inverseJoinColumns = { @JoinColumn(name = "USER_ID") })
    private Set<UserEntity> users;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "RESOURCE_GROUP",
            joinColumns = {@JoinColumn(name = "RESOURCE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "GRP_ID")})
    private Set<GroupEntity> groups;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "RESOURCE_ROLE", joinColumns = { @JoinColumn(name = "RESOURCE_ID") }, inverseJoinColumns = { @JoinColumn(name = "ROLE_ID") })
    private Set<RoleEntity> roles;

    @Column(name = "MIN_AUTH_LEVEL")
    private String minAuthLevel;

    @Column(name = "IS_PUBLIC")
    @Type(type = "yes_no")
    private boolean isPublic;
    
    @Column(name = "COORELATED_NAME", length=250)
    private String coorelatedName;
    
	@ManyToOne(fetch = FetchType.EAGER,cascade={CascadeType.ALL})
    @JoinColumn(name="ADMIN_RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = true, updatable = true, nullable=true)
	private ResourceEntity adminResource;
	
	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy="associationEntityId", orphanRemoval=true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@Where(clause="ASSOCIATION_TYPE='RESOURCE'")
	private Set<ApproverAssociationEntity> approverAssociations;
	
    
    @Transient
    @InternationalizedCollection(targetField="displayName")
    private Map<String, LanguageMappingEntity> displayNameMap;
    
    @Transient
    private String displayName;

    @OneToMany(mappedBy = "referenceId")
    private Set<LanguageMappingEntity> languageMappings;

    public ResourceRisk getRisk() {
        return risk;
    }

    public void setRisk(ResourceRisk risk) {
        this.risk = risk;
    }

    public ResourceEntity() {
    }
    
    public ResourceEntity(String id) {
    	this.id = id;
    }
    
    public void addRole(final RoleEntity entity) {
    	if(entity != null) {
    		if(this.roles == null) {
    			this.roles = new HashSet<RoleEntity>();
    		}
    		this.roles.add(entity);
    	}
    }
    
    public void remove(final RoleEntity entity) {
    	if(entity != null) {
    		if(this.roles != null) {
    			this.roles.remove(entity);
    		}
    	}
    }
  
    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
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

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
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
    
    public void addGroup(final GroupEntity entity) {
    	if(entity != null) {
    		if(this.groups == null) {
    			this.groups = new HashSet<GroupEntity>();
    		}
    		this.groups.add(entity);
    	}
    }
    
    public void remove(final GroupEntity entity) {
    	if(entity != null) {
    		if(this.groups != null) {
    			this.groups.remove(entity);
    		}
    	}
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

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean aPublic) {
        isPublic = aPublic;
    }
    
    public ResourceEntity getAdminResource() {
		return adminResource;
	}

	public void setAdminResource(ResourceEntity adminResource) {
		this.adminResource = adminResource;
	}

	public Set<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(Set<UserEntity> users) {
        this.users = users;
    }
    
    public void addUser(final UserEntity user) {
    	if(user != null) {
    		if(this.users == null) {
    			this.users = new HashSet<UserEntity>();
    		}
    		this.users.add(user);
    	}
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
				if(resource.getId().equals(resourceId)) {
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

	public Set<ApproverAssociationEntity> getApproverAssociations() {
		return approverAssociations;
	}

	public void setApproverAssociations(
			Set<ApproverAssociationEntity> approverAssociations) {
		this.approverAssociations = approverAssociations;
	}
	
	public void addApproverAssociation(final ApproverAssociationEntity entity) {
		if(entity != null) {
			if(this.approverAssociations == null) {
				this.approverAssociations = new HashSet<ApproverAssociationEntity>();
			}
			this.approverAssociations.add(entity);
		}
	}

	public Map<String, LanguageMappingEntity> getDisplayNameMap() {
		return displayNameMap;
	}

	public void setDisplayNameMap(Map<String, LanguageMappingEntity> displayNameMap) {
		this.displayNameMap = displayNameMap;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getCoorelatedName() {
		return coorelatedName;
	}

	public void setCoorelatedName(String coorelatedName) {
		this.coorelatedName = coorelatedName;
	}

    public Set<LanguageMappingEntity> getLanguageMappings() {
        return languageMappings;
    }

    public void setLanguageMappings(Set<LanguageMappingEntity> languageMappings) {
        this.languageMappings = languageMappings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResourceEntity)) return false;
        if (!super.equals(o)) return false;

        ResourceEntity that = (ResourceEntity) o;

        if (isPublic != that.isPublic) return false;
        if (URL != null ? !URL.equals(that.URL) : that.URL != null) return false;
        if (coorelatedName != null ? !coorelatedName.equals(that.coorelatedName) : that.coorelatedName != null)
            return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
        if (displayOrder != null ? !displayOrder.equals(that.displayOrder) : that.displayOrder != null) return false;
        if (minAuthLevel != null ? !minAuthLevel.equals(that.minAuthLevel) : that.minAuthLevel != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (resourceType != null ? !resourceType.equals(that.resourceType) : that.resourceType != null) return false;
        if (risk != that.risk) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (resourceType != null ? resourceType.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (displayOrder != null ? displayOrder.hashCode() : 0);
        result = 31 * result + (URL != null ? URL.hashCode() : 0);
        result = 31 * result + (risk != null ? risk.hashCode() : 0);
        result = 31 * result + (minAuthLevel != null ? minAuthLevel.hashCode() : 0);
        result = 31 * result + (isPublic ? 1 : 0);
        result = 31 * result + (coorelatedName != null ? coorelatedName.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        return result;
    }
}
