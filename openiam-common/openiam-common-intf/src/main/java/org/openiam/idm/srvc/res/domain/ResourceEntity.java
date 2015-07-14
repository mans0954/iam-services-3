package org.openiam.idm.srvc.res.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.openiam.am.srvc.domain.OAuthUserClientXrefEntity;
import org.openiam.base.domain.AbstractMetdataTypeEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.domain.GroupToGroupMembershipXrefEntity;
import org.openiam.idm.srvc.grp.domain.GroupToResourceMembershipXrefEntity;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.org.domain.GroupToOrgMembershipXrefEntity;
import org.openiam.idm.srvc.org.domain.ResourceToOrgMembershipXrefEntity;
import org.openiam.idm.srvc.org.domain.RoleToOrgMembershipXrefEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceRisk;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.domain.RoleToResourceMembershipXrefEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.domain.UserToResourceMembershipXrefEntity;
import org.openiam.idm.srvc.user.domain.UserToRoleMembershipXrefEntity;
import org.openiam.internationalization.Internationalized;
import org.openiam.internationalization.InternationalizedCollection;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "RES")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(Resource.class)
@AttributeOverride(name = "id", column = @Column(name = "RESOURCE_ID"))
@Internationalized
public class ResourceEntity extends AbstractMetdataTypeEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RESOURCE_TYPE_ID")
    @Internationalized
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

    
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="memberEntity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
    private Set<ResourceToResourceMembershipXrefEntity> parentResources = new HashSet<ResourceToResourceMembershipXrefEntity>(0);

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="entity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
    private Set<ResourceToResourceMembershipXrefEntity> childResources = new HashSet<ResourceToResourceMembershipXrefEntity>(0);

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="resource", orphanRemoval=true)
    @OrderBy("name asc")
    //@JoinColumn(name = "RESOURCE_ID")
    @Fetch(FetchMode.SUBSELECT)
    @Internationalized
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<ResourcePropEntity> resourceProps = new HashSet<ResourcePropEntity>(0); // defined as a Set in Hibernate map

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="entity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
    private Set<UserToResourceMembershipXrefEntity> users = new HashSet<UserToResourceMembershipXrefEntity>(0);

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="memberEntity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
    private Set<RoleToResourceMembershipXrefEntity> roles = new HashSet<RoleToResourceMembershipXrefEntity>(0);

    @Column(name = "MIN_AUTH_LEVEL")
    private String minAuthLevel;

    @Column(name = "IS_PUBLIC")
    @Type(type = "yes_no")
    private boolean isPublic = true;
    
    @Column(name = "COORELATED_NAME", length=250)
    private String coorelatedName;
	
	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy="associationEntityId", orphanRemoval=true)
	@Where(clause="ASSOCIATION_TYPE='RESOURCE'")
	private Set<ApproverAssociationEntity> approverAssociations;
	
    
    @Transient
    @InternationalizedCollection(targetField="displayName")
    private Map<String, LanguageMappingEntity> displayNameMap;
    
    @Transient
    private String displayName;
    
    @Column(name="REFERENCE_ID")
    private String referenceId;
    
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="memberEntity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
    private Set<ResourceToOrgMembershipXrefEntity> organizations = new HashSet<ResourceToOrgMembershipXrefEntity>(0);

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="memberEntity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
    private Set<GroupToResourceMembershipXrefEntity> groups = new HashSet<GroupToResourceMembershipXrefEntity>(0);

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="scope", orphanRemoval=true)
	@Fetch(FetchMode.SUBSELECT)
	private Set<OAuthUserClientXrefEntity> oAuthClientAuthorizations = new HashSet<OAuthUserClientXrefEntity>(0);


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
    
    public void remove(final RoleEntity entity) {
    	if(entity != null) {
    		if(this.roles != null) {
    			this.roles.remove(entity);
    		}
    	}
    }
  
    public Set<RoleToResourceMembershipXrefEntity> getRoles() {
		return roles;
	}

	public void setRoles(Set<RoleToResourceMembershipXrefEntity> roles) {
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
    
	public ResourceToResourceMembershipXrefEntity getChild(final String childId) {
		final Optional<ResourceToResourceMembershipXrefEntity> xref = 
    			this.getChildResources()
    				.stream()
    				.filter(e -> childId.equals(e.getMemberEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
	}
    
    
    public ResourceToResourceMembershipXrefEntity getParent(final String parentId) {
    	final Optional<ResourceToResourceMembershipXrefEntity> xref = 
    			this.getParentResources()
    				.stream()
    				.filter(e -> parentId.equals(e.getEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
    }

    public Set<ResourceToResourceMembershipXrefEntity> getParentResources() {
        return parentResources;
    }

    public void setParentResources(Set<ResourceToResourceMembershipXrefEntity> parentResources) {
        this.parentResources = parentResources;
    }

    public Set<ResourceToResourceMembershipXrefEntity> getChildResources() {
        return childResources;
    }

    public void setChildResources(Set<ResourceToResourceMembershipXrefEntity> childResources) {
        this.childResources = childResources;
    }

    public Set<ResourcePropEntity> getResourceProps() {
        return resourceProps;
    }

    public void setResourceProps(Set<ResourcePropEntity> resourceProps) {
        this.resourceProps = resourceProps;
    }
    
	public void addUser(final UserEntity entity, final AccessRightEntity right) {
		if(entity != null && right != null) {
			final Set<AccessRightEntity> rightSet = new HashSet<AccessRightEntity>();
			rightSet.add(right);
			addUser(entity, rightSet);
		}
	}
	
    public Set<GroupToResourceMembershipXrefEntity> getGroups() {
        return groups;
    }

    public void setGroups(Set<GroupToResourceMembershipXrefEntity> groups) {
        this.groups = groups;
    }
    
    public void addGroup(final GroupEntity entity, final Collection<AccessRightEntity> rights) {
		if(entity != null) {
			if(this.groups == null) {
				this.groups = new LinkedHashSet<GroupToResourceMembershipXrefEntity>();
			}
			GroupToResourceMembershipXrefEntity theXref = null;
			for(final GroupToResourceMembershipXrefEntity xref : this.groups) {
				if(xref.getEntity().getId().equals(getId()) && xref.getMemberEntity().getId().equals(entity.getId())) {
					theXref = xref;
					break;
				}
			}
			
			if(theXref == null) {
				theXref = new GroupToResourceMembershipXrefEntity();
				theXref.setEntity(entity);
				theXref.setMemberEntity(this);
			}
			if(rights != null) {
				theXref.setRights(new HashSet<AccessRightEntity>(rights));
			}
			this.groups.add(theXref);
		}
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
    
	public Set<UserToResourceMembershipXrefEntity> getUsers() {
        return users;
    }

    public void setUsers(Set<UserToResourceMembershipXrefEntity> users) {
        this.users = users;
    }
    
    public void removeUser(final UserEntity entity) {
    	if(entity != null) {
			if(this.users != null) {
				this.users.removeIf(e -> e.getMemberEntity().getId().equals(entity.getId()));
			}
		}
    }
    
    public void addUser(final UserEntity entity, final Collection<AccessRightEntity> rights) {
    	if(entity != null) {
			if(this.users == null) {
				this.users = new LinkedHashSet<UserToResourceMembershipXrefEntity>();
			}
			UserToResourceMembershipXrefEntity theXref = null;
			for(final UserToResourceMembershipXrefEntity xref : this.users) {
				if(xref.getEntity().getId().equals(getId()) && xref.getMemberEntity().getId().equals(entity.getId())) {
					theXref = xref;
					break;
				}
			}
			
			if(theXref == null) {
				theXref = new UserToResourceMembershipXrefEntity();
				theXref.setEntity(this);
				theXref.setMemberEntity(entity);
			}
			if(rights != null) {
				theXref.setRights(new HashSet<AccessRightEntity>(rights));
			}
			this.users.add(theXref);
		}
    }
    
    public UserToResourceMembershipXrefEntity getUser(final String userId) {
		final Optional<UserToResourceMembershipXrefEntity> xref = 
    			this.getUsers()
    				.stream()
    				.filter(e -> userId.equals(e.getMemberEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
	}

    
	public void addChildResource(final ResourceEntity resource, final Collection<AccessRightEntity> rights) {
		if(resource != null) {
			if(this.childResources == null) {
				this.childResources = new LinkedHashSet<ResourceToResourceMembershipXrefEntity>();
			}
			ResourceToResourceMembershipXrefEntity theXref = null;
			for(final ResourceToResourceMembershipXrefEntity xref : this.childResources) {
				if(xref.getEntity().getId().equals(getId()) && xref.getMemberEntity().getId().equals(resource.getId())) {
					theXref = xref;
					break;
				}
			}
			
			if(theXref == null) {
				theXref = new ResourceToResourceMembershipXrefEntity();
				theXref.setEntity(this);
				theXref.setMemberEntity(resource);
			}
			if(rights != null) {
				theXref.setRights(new HashSet<AccessRightEntity>(rights));
			}
			this.childResources.add(theXref);
		}
	}
	
	public boolean hasChildResoruce(final ResourceEntity entity) {
		boolean contains = false;
		if(childResources != null) {
			contains = (childResources.stream().map(e -> e.getMemberEntity()).filter(e -> e.getId().equals(entity.getId())).count() > 0);
		}
		return contains;
	}
	
	public void removeChildResource(final ResourceEntity entity) {
		if(entity != null) {
			if(this.childResources != null) {
				this.childResources.removeIf(e -> e.getMemberEntity().getId().equals(entity.getId()));
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

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

    public Set<ResourceToOrgMembershipXrefEntity> getOrganizations() {
		return organizations;
	}

	public Set<OAuthUserClientXrefEntity> getoAuthClientAuthorizations() {
		return oAuthClientAuthorizations;
	}

	public void setoAuthClientAuthorizations(Set<OAuthUserClientXrefEntity> oAuthClientAuthorizations) {
		this.oAuthClientAuthorizations = oAuthClientAuthorizations;
	}

	public void setOrganizations(
			Set<ResourceToOrgMembershipXrefEntity> organizations) {
		this.organizations = organizations;
	}
	
	public ResourceToOrgMembershipXrefEntity getOrganization(final String organizationId) {
    	final Optional<ResourceToOrgMembershipXrefEntity> xref = 
    			this.getOrganizations()
				.stream()
				.filter(e -> organizationId.equals(e.getEntity().getId()))
				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
    }
	
	public GroupToResourceMembershipXrefEntity getGroup(final String groupId) {
		final Optional<GroupToResourceMembershipXrefEntity> xref = 
    			this.getGroups()
    				.stream()
    				.filter(e -> groupId.equals(e.getEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
	}
	
	public RoleToResourceMembershipXrefEntity getRole(final String groupId) {
		final Optional<RoleToResourceMembershipXrefEntity> xref = 
    			this.getRoles()
    				.stream()
    				.filter(e -> groupId.equals(e.getEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
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
        if (referenceId != null ? !referenceId.equals(that.referenceId) : that.referenceId != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
        if (displayOrder != null ? !displayOrder.equals(that.displayOrder) : that.displayOrder != null) return false;
        if (minAuthLevel != null ? !minAuthLevel.equals(that.minAuthLevel) : that.minAuthLevel != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (resourceType != null ? !resourceType.equals(that.resourceType) : that.resourceType != null) return false;
        return risk == that.risk;
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
        result = 31 * result + (referenceId != null ? referenceId.hashCode() : 0);
        return result;
    }
}
