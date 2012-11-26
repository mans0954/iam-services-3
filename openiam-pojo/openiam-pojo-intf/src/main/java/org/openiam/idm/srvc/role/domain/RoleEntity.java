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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.res.domain.ResourceRoleEntity;
import org.openiam.idm.srvc.role.dto.Role;

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
    private String roleName;
    
    @Column(name="DESCRIPTION")
    private String description;
    
    @Column(name="STATUS",length=20)
    private String status;
    
    @Column(name="TYPE_ID",length=20)
    private String metadataTypeId;

    @Column(name="OWNER_ID",length=32)
    private String ownerId;
    
    @Column(name="INTERNAL_ROLE_ID")
    private String internalRoleId;
    
    @Column(name="SERVICE_ID",length=32)
    private String serviceId;

	@ManyToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinTable(name="GRP_ROLE",
	    joinColumns={@JoinColumn(name="ROLE_ID")},
	    inverseJoinColumns={@JoinColumn(name="GRP_ID")})
	@Fetch(FetchMode.SUBSELECT)
    private Set<GroupEntity> groups;
	
	@OneToMany(fetch=FetchType.LAZY,orphanRemoval=true,cascade={CascadeType.ALL})
	@JoinColumn(name="ROLE_ID")
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
    
	@ManyToMany(cascade={CascadeType.ALL},fetch=FetchType.LAZY)
    @JoinTable(name="role_to_role_membership",
        joinColumns={@JoinColumn(name="ROLE_ID")},
        inverseJoinColumns={@JoinColumn(name="MEMBER_ROLE_ID")})
    @Fetch(FetchMode.SUBSELECT)
    private Set<RoleEntity> childRoles;
	
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ROLE_ID")
    @Fetch(FetchMode.SUBSELECT)
    private Set<ResourceRoleEntity> resourceRoles;
    
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

	public String getMetadataTypeId() {
		return metadataTypeId;
	}

	public void setMetadataTypeId(String metadataTypeId) {
		this.metadataTypeId = metadataTypeId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getInternalRoleId() {
		return internalRoleId;
	}

	public void setInternalRoleId(String internalRoleId) {
		this.internalRoleId = internalRoleId;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
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

	public Set<ResourceRoleEntity> getResourceRoles() {
		return resourceRoles;
	}

	public void setResourceRoles(Set<ResourceRoleEntity> resourceRoles) {
		this.resourceRoles = resourceRoles;
	}
	
	public void addResourceRole(final ResourceRoleEntity entity) {
		if(entity != null) {
			if(resourceRoles == null) {
				resourceRoles = new LinkedHashSet<ResourceRoleEntity>();
			}
			resourceRoles.add(entity);
		}
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
				+ ((internalRoleId == null) ? 0 : internalRoleId.hashCode());
		result = prime * result
				+ ((metadataTypeId == null) ? 0 : metadataTypeId.hashCode());
		result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
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
		if (internalRoleId == null) {
			if (other.internalRoleId != null)
				return false;
		} else if (!internalRoleId.equals(other.internalRoleId))
			return false;
		if (metadataTypeId == null) {
			if (other.metadataTypeId != null)
				return false;
		} else if (!metadataTypeId.equals(other.metadataTypeId))
			return false;
		if (ownerId == null) {
			if (other.ownerId != null)
				return false;
		} else if (!ownerId.equals(other.ownerId))
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
				.format("RoleEntity [roleId=%s, roleName=%s, description=%s, status=%s, metadataTypeId=%s, ownerId=%s, internalRoleId=%s, serviceId=%s, createDate=%s, createdBy=%s]",
						roleId, roleName, description,
						status, metadataTypeId, ownerId, internalRoleId,
						serviceId, createDate, createdBy);
	}
    
    
}
