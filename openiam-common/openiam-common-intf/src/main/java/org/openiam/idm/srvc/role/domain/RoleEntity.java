package org.openiam.idm.srvc.role.domain;

import java.io.Serializable;
import java.util.Date;
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
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.domain.UserEntity;

@Entity
@Table(name="ROLE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(Role.class)
public class RoleEntity implements Serializable {
	
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="ROLE_ID", length=32)
	private String roleId;
    
    @Column(name="ROLE_NAME",length=80)
    @Size(max = 80, message = "role.name.too.long")
    private String roleName;
    
    @Column(name="DESCRIPTION")
    @Size(max = 255, message = "role.description.too.long")
    private String description;
    
    @Column(name="STATUS",length=20)
    private String status;
    
    @Column(name="SERVICE_ID",length=32)
    private String serviceId;
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "MANAGED_SYS_ID", referencedColumnName = "MANAGED_SYS_ID", insertable = true, updatable = true, nullable=true)
    private ManagedSysEntity managedSystem;

    @ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.LAZY)
    @JoinTable(name="GRP_ROLE",
	    joinColumns={@JoinColumn(name="ROLE_ID")},
	    inverseJoinColumns={@JoinColumn(name="GRP_ID")})
	@Fetch(FetchMode.SUBSELECT)
    private Set<GroupEntity> groups;
	
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="role", orphanRemoval=true)
    @OrderBy("name asc")
    //@JoinColumn(name = "ROLE_ID")
    @Fetch(FetchMode.SUBSELECT)
	private Set<RoleAttributeEntity> roleAttributes;
	
	@OneToMany(fetch=FetchType.LAZY,orphanRemoval=true,cascade=CascadeType.ALL)
	@JoinColumn(name="ROLE_ID")
	@Fetch(FetchMode.SUBSELECT)
	private Set<RolePolicyEntity> rolePolicy;
	
	@ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.LAZY)
    @JoinTable(name="role_to_role_membership",
        joinColumns={@JoinColumn(name="MEMBER_ROLE_ID")},
        inverseJoinColumns={@JoinColumn(name="ROLE_ID")})
    @Fetch(FetchMode.SUBSELECT)
    private Set<RoleEntity> parentRoles;
    
	@ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.LAZY)
    @JoinTable(name="role_to_role_membership",
        joinColumns={@JoinColumn(name="ROLE_ID")},
        inverseJoinColumns={@JoinColumn(name="MEMBER_ROLE_ID")})
    @Fetch(FetchMode.SUBSELECT)
    private Set<RoleEntity> childRoles;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "RESOURCE_ROLE", joinColumns = { @JoinColumn(name = "ROLE_ID") }, inverseJoinColumns = { @JoinColumn(name = "RESOURCE_ID") })
    private Set<ResourceEntity> resources;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "USER_ROLE", joinColumns = { @JoinColumn(name = "ROLE_ID") }, inverseJoinColumns = { @JoinColumn(name = "USER_ID") })
    private Set<UserEntity> users = new HashSet<UserEntity>(0);

    @Column(name="CREATE_DATE",length=19)
	private Date createDate;
    
    @Column(name="CREATED_BY",length=20)
	private String createdBy;

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
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

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
	public boolean hasGroup(final String groupId) {
		boolean retVal = false;
		if(groups != null) {
			for(final GroupEntity entity : groups) {
				if(entity.getGrpId().equals(groupId)) {
					retVal = true;
					break;
				}
			}
		}
		return retVal;
	}

	public Set<GroupEntity> getGroups() {
		return groups;
	}

	public void setGroups(Set<GroupEntity> groups) {
		this.groups = groups;
	}
	
	public void addGroup(final GroupEntity group) {
		if(group != null) {
			if(this.groups == null) {
				this.groups = new LinkedHashSet<GroupEntity>();
			}
			this.groups.add(group);
		}
	}
	
	public void removeGroup(final String groupId) {
		if(groupId != null) {
			if(groups != null) {
				for(final Iterator<GroupEntity> it = groups.iterator(); it.hasNext();) {
					final GroupEntity entity = it.next();
					if(entity.getGrpId().equals(groupId)) {
						it.remove();
						break;
					}
				}
			}
		}
	}

	public Set<RoleAttributeEntity> getRoleAttributes() {
		return roleAttributes;
	}

	public void setRoleAttributes(Set<RoleAttributeEntity> roleAttributes) {
		this.roleAttributes = roleAttributes;
	}

	public Set<RolePolicyEntity> getRolePolicy() {
		return rolePolicy;
	}

	public void setRolePolicy(Set<RolePolicyEntity> rolePolicy) {
		this.rolePolicy = rolePolicy;
	}

	public Set<RoleEntity> getParentRoles() {
		return parentRoles;
	}

	public void setParentRoles(Set<RoleEntity> parentRoles) {
		this.parentRoles = parentRoles;
	}

	public Set<RoleEntity> getChildRoles() {
		return childRoles;
	}
	
	public boolean hasChildRole(final String roleId) {
		boolean retVal = false;
		if(roleId != null) {
			if(childRoles != null) {
				for(final RoleEntity role : childRoles) {
					if(role.getRoleId().equals(roleId)) {
						retVal = true;
						break;
					}
				}
			}
		}
		return retVal;
	}
	
	public void addChildRole(final RoleEntity role) {
		if(role != null) {
			if(childRoles == null) {
				childRoles = new LinkedHashSet<RoleEntity>();
			}
			childRoles.add(role);
		}
	}
	
	public void removeChildRole(final String roleId) {
		if(roleId != null) {
			if(childRoles != null) {
				for(final Iterator<RoleEntity> it = childRoles.iterator(); it.hasNext();) {
					final RoleEntity role = it.next();
					if(role.getRoleId().equals(roleId)) {
						it.remove();
						break;
					}
				}
			}
		}
	}

	public void setChildRoles(Set<RoleEntity> childRoles) {
		this.childRoles = childRoles;
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

    public Set<ResourceEntity> getResources() {
        return resources;
    }

    public void setResources(Set<ResourceEntity> resources) {
        this.resources = resources;
    }

    public Set<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(Set<UserEntity> users) {
        this.users = users;
    }
    
	public ManagedSysEntity getManagedSystem() {
		return managedSystem;
	}

	public void setManagedSystem(ManagedSysEntity managedSystem) {
		this.managedSystem = managedSystem;
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
		result = prime * result + ((roleId == null) ? 0 : roleId.hashCode());
		result = prime * result
				+ ((roleName == null) ? 0 : roleName.hashCode());
		result = prime * result
				+ ((serviceId == null) ? 0 : serviceId.hashCode());
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
		if (roleId == null) {
			if (other.roleId != null)
				return false;
		} else if (!roleId.equals(other.roleId))
			return false;
		if (roleName == null) {
			if (other.roleName != null)
				return false;
		} else if (!roleName.equals(other.roleName))
			return false;
		if (serviceId == null) {
			if (other.serviceId != null)
				return false;
		} else if (!serviceId.equals(other.serviceId))
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
				.format("RoleEntity [roleId=%s, roleName=%s, description=%s, status=%s, serviceId=%s, managedSystem=%s, createDate=%s, createdBy=%s]",
						roleId, roleName, description, status, serviceId,
						managedSystem, createDate, createdBy);
	}


}
