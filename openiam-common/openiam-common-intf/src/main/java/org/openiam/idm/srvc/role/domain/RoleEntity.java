package org.openiam.idm.srvc.role.domain;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;
import org.openiam.am.srvc.domain.OAuthUserClientXrefEntity;
import org.openiam.base.domain.AbstractMetdataTypeEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.org.domain.RoleToOrgMembershipXrefEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.domain.UserToRoleMembershipXrefEntity;
import org.openiam.internationalization.Internationalized;

@Entity
@Table(name="ROLE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(Role.class)
@AttributeOverride(name = "id", column = @Column(name = "ROLE_ID"))
@Internationalized
public class RoleEntity extends AbstractMetdataTypeEntity {

    @Column(name="ROLE_NAME",length=80)
    @Size(max = 80, message = "role.name.too.long")
    private String name;
    
    @Column(name="DESCRIPTION")
    @Size(max = 255, message = "role.description.too.long")
    private String description;
    
    @Column(name="STATUS",length=20)
    private String status;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "MANAGED_SYS_ID", referencedColumnName = "MANAGED_SYS_ID", insertable = true, updatable = true, nullable=true)
    private ManagedSysEntity managedSystem;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="entity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
	private Set<RoleToGroupMembershipXrefEntity> groups;
	
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="role", orphanRemoval=true)
    @OrderBy("name asc")
    //@JoinColumn(name = "ROLE_ID")
    @Fetch(FetchMode.SUBSELECT)
    @Internationalized
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<RoleAttributeEntity> roleAttributes;
	
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="memberEntity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
    private Set<RoleToRoleMembershipXrefEntity> parentRoles = new HashSet<RoleToRoleMembershipXrefEntity>(0);

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="entity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
    private Set<RoleToRoleMembershipXrefEntity> childRoles = new HashSet<RoleToRoleMembershipXrefEntity>(0);

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="entity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
	private Set<RoleToResourceMembershipXrefEntity> resources;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="entity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
    private Set<UserToRoleMembershipXrefEntity> users = new HashSet<UserToRoleMembershipXrefEntity>(0);
    
    @Column(name="CREATE_DATE",length=19)
	private Date createDate;
    
    @Column(name="CREATED_BY",length=20)
	private String createdBy;
	
	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy="associationEntityId", orphanRemoval=true)
	@Where(clause="ASSOCIATION_TYPE='ROLE'")
	private Set<ApproverAssociationEntity> approverAssociations;
	
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="memberEntity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
    private Set<RoleToOrgMembershipXrefEntity> organizations = new HashSet<RoleToOrgMembershipXrefEntity>(0);

	@ManyToMany(cascade = {CascadeType.ALL})
	@JoinTable(name = "OAUTH_AUTHORIZED_SCOPE", joinColumns = { @JoinColumn(name = "ROLE_ID") }, inverseJoinColumns = { @JoinColumn(name = "OAUTH_AUTHORIZATION_ID") })
	private Set<OAuthUserClientXrefEntity> oAuthAuthorizationSet = new HashSet<OAuthUserClientXrefEntity>(0);


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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public Set<RoleAttributeEntity> getRoleAttributes() {
		return roleAttributes;
	}

	public void setRoleAttributes(Set<RoleAttributeEntity> roleAttributes) {
		this.roleAttributes = roleAttributes;
	}

	public Set<RoleToRoleMembershipXrefEntity> getParentRoles() {
		return parentRoles;
	}

	public void setParentRoles(Set<RoleToRoleMembershipXrefEntity> parentRoles) {
		this.parentRoles = parentRoles;
	}

	public Set<RoleToRoleMembershipXrefEntity> getChildRoles() {
		return childRoles;
	}
	
	public void setChildRoles(Set<RoleToRoleMembershipXrefEntity> childRoles) {
		this.childRoles = childRoles;
	}
	
	public RoleToOrgMembershipXrefEntity getOrganization(final String organizationId) {
    	final Optional<RoleToOrgMembershipXrefEntity> xref = 
    			this.getOrganizations()
				.stream()
				.filter(e -> organizationId.equals(e.getEntity().getId()))
				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
    }
	
	public RoleToRoleMembershipXrefEntity getChild(final String childId) {
		final Optional<RoleToRoleMembershipXrefEntity> xref = 
    			this.getChildRoles()
    				.stream()
    				.filter(e -> childId.equals(e.getMemberEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
	}
	
	public RoleToRoleMembershipXrefEntity getParent(final String parentId) {
    	final Optional<RoleToRoleMembershipXrefEntity> xref = 
    			this.getParentRoles()
    				.stream()
    				.filter(e -> parentId.equals(e.getEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
    }

	public void addChild(final RoleEntity entity, final Collection<AccessRightEntity> rights) {
		if(entity != null) {
			if(this.childRoles == null) {
				this.childRoles = new LinkedHashSet<RoleToRoleMembershipXrefEntity>();
			}
			RoleToRoleMembershipXrefEntity theXref = null;
			for(final RoleToRoleMembershipXrefEntity xref : this.childRoles) {
				if(xref.getEntity().getId().equals(getId()) && xref.getMemberEntity().getId().equals(entity.getId())) {
					theXref = xref;
					break;
				}
			}
			
			if(theXref == null) {
				theXref = new RoleToRoleMembershipXrefEntity();
				theXref.setEntity(this);
				theXref.setMemberEntity(entity);
			}
			if(rights != null) {
				theXref.setRights(new HashSet<AccessRightEntity>(rights));
			}
			this.childRoles.add(theXref);
		}
	}
	
	public boolean hasChild(final RoleEntity entity) {
		boolean contains = false;
		if(childRoles != null) {
			contains = (childRoles.stream().map(e -> e.getMemberEntity()).filter(e -> e.getId().equals(entity.getId())).count() > 0);
		}
		return contains;
	}
	
	public void removeChild(final RoleEntity entity) {
		if(entity != null) {
			if(this.childRoles != null) {
				this.childRoles.removeIf(e -> e.getMemberEntity().getId().equals(entity.getId()));
			}
		}
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

    public Set<RoleToResourceMembershipXrefEntity> getResources() {
        return resources;
    }

    public void setResources(Set<RoleToResourceMembershipXrefEntity> resources) {
        this.resources = resources;
    }
    
    public RoleToGroupMembershipXrefEntity getGroup(final String groupId) {
		final Optional<RoleToGroupMembershipXrefEntity> xref = 
    			this.getGroups()
    				.stream()
    				.filter(e -> groupId.equals(e.getMemberEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
	}
    
	public RoleToResourceMembershipXrefEntity getResource(final String resourceId) {
		final Optional<RoleToResourceMembershipXrefEntity> xref = 
    			this.getResources()
    				.stream()
    				.filter(e -> resourceId.equals(e.getMemberEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
	}
	
	public void addUser(final UserEntity entity, final AccessRightEntity right) {
		if(entity != null && right != null) {
			final Set<AccessRightEntity> rightSet = new HashSet<AccessRightEntity>();
			rightSet.add(right);
			addUser(entity, rightSet);
		}
	}
	
	public void addUser(final UserEntity entity, final Collection<AccessRightEntity> rights) {
		if(entity != null) {
			if(this.users == null) {
				this.users = new LinkedHashSet<UserToRoleMembershipXrefEntity>();
			}
			UserToRoleMembershipXrefEntity theXref = null;
			for(final UserToRoleMembershipXrefEntity xref : this.users) {
				if(xref.getEntity().getId().equals(getId()) && xref.getMemberEntity().getId().equals(entity.getId())) {
					theXref = xref;
					break;
				}
			}
			
			if(theXref == null) {
				theXref = new UserToRoleMembershipXrefEntity();
				theXref.setEntity(this);
				theXref.setMemberEntity(entity);
			}
			if(rights != null) {
				theXref.setRights(new HashSet<AccessRightEntity>(rights));
			}
			this.users.add(theXref);
		}
	}
	
	public void addResource(final ResourceEntity entity, final Collection<AccessRightEntity> rights) {
		if(entity != null) {
			if(this.resources == null) {
				this.resources = new LinkedHashSet<RoleToResourceMembershipXrefEntity>();
			}
			RoleToResourceMembershipXrefEntity theXref = null;
			for(final RoleToResourceMembershipXrefEntity xref : this.resources) {
				if(xref.getEntity().getId().equals(getId()) && xref.getMemberEntity().getId().equals(entity.getId())) {
					theXref = xref;
					break;
				}
			}
			
			if(theXref == null) {
				theXref = new RoleToResourceMembershipXrefEntity();
				theXref.setEntity(this);
				theXref.setMemberEntity(entity);
			}
			if(rights != null) {
				theXref.setRights(new HashSet<AccessRightEntity>(rights));
			}
			this.resources.add(theXref);
		}
	}
	
	public void removeResource(final ResourceEntity entity) {
		if(entity != null) {
			if(this.resources != null) {
				this.resources.removeIf(e -> e.getMemberEntity().getId().equals(entity.getId()));
			}
		}
	}
	
	public void addGroup(final GroupEntity entity, final Collection<AccessRightEntity> rights) {
		if(entity != null) {
			if(this.groups == null) {
				this.groups = new LinkedHashSet<RoleToGroupMembershipXrefEntity>();
			}
			RoleToGroupMembershipXrefEntity theXref = null;
			for(final RoleToGroupMembershipXrefEntity xref : this.groups) {
				if(xref.getEntity().getId().equals(getId()) && xref.getMemberEntity().getId().equals(entity.getId())) {
					theXref = xref;
					break;
				}
			}
			
			if(theXref == null) {
				theXref = new RoleToGroupMembershipXrefEntity();
				theXref.setEntity(this);
				theXref.setMemberEntity(entity);
			}
			if(rights != null) {
				theXref.setRights(new HashSet<AccessRightEntity>(rights));
			}
			this.groups.add(theXref);
		}
	}
	
	public void removeGroup(final GroupEntity entity) {
		if(entity != null) {
			if(this.groups != null) {
				this.groups.removeIf(e -> e.getMemberEntity().getId().equals(entity.getId()));
			}
		}
	}

    public Set<RoleToGroupMembershipXrefEntity> getGroups() {
		return groups;
	}

	public void setGroups(Set<RoleToGroupMembershipXrefEntity> groups) {
		this.groups = groups;
	}
	
	public UserToRoleMembershipXrefEntity getUser(final String userId) {
		final Optional<UserToRoleMembershipXrefEntity> xref = 
    			this.getUsers()
    				.stream()
    				.filter(e -> userId.equals(e.getMemberEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
	}


	public Set<UserToRoleMembershipXrefEntity> getUsers() {
        return users;
    }

    public void setUsers(Set<UserToRoleMembershipXrefEntity> users) {
        this.users = users;
    }
    
	public ManagedSysEntity getManagedSystem() {
		return managedSystem;
	}

	public void setManagedSystem(ManagedSysEntity managedSystem) {
		this.managedSystem = managedSystem;
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

	public Set<RoleToOrgMembershipXrefEntity> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(Set<RoleToOrgMembershipXrefEntity> organizations) {
		this.organizations = organizations;
    }

	public Set<OAuthUserClientXrefEntity> getoAuthAuthorizationSet() {
		return oAuthAuthorizationSet;
	}

	public void setoAuthAuthorizationSet(Set<OAuthUserClientXrefEntity> oAuthAuthorizationSet) {
		this.oAuthAuthorizationSet = oAuthAuthorizationSet;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((createDate == null) ? 0 : createDate.hashCode());
		result = prime * result
				+ ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((managedSystem == null) ? 0 : managedSystem.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoleEntity other = (RoleEntity) obj;
		if (createDate == null) {
			if (other.createDate != null)
				return false;
		} else if (!createDate.equals(other.createDate))
			return false;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (managedSystem == null) {
			if (other.managedSystem != null)
				return false;
		} else if (!managedSystem.equals(other.managedSystem))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("RoleEntity [id=%s, name=%s, description=%s, status=%s, managedSystem=%s, createDate=%s, createdBy=%s]",
						id, name, description, status, managedSystem, createDate, createdBy);
	}


}
