package org.openiam.idm.srvc.grp.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
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
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;

@Entity
@Table(name = "GRP")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(Group.class)
public class GroupEntity {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "GRP_ID", length = 32)
    private String grpId;

    @Column(name = "GRP_NAME", length = 80)
    @Size(max = 80, message = "group.name.too.long")
    private String grpName;

    @Column(name = "CREATE_DATE", length = 19)
    private Date createDate;

    @Column(name = "CREATED_BY", length = 20)
    private String createdBy;
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "MANAGED_SYS_ID", referencedColumnName = "MANAGED_SYS_ID", insertable = true, updatable = true, nullable=true)
    private ManagedSysEntity managedSystem;

    @Column(name = "COMPANY_ID", length = 32)
    private String companyId;

    @Column(name = "GROUP_DESC", length = 80)
    @Size(max = 80, message = "group.description.too.long")
    private String description;

    @Column(name = "STATUS", length = 20)
    private String status;

    @Column(name = "LAST_UPDATE", length = 19)
    private Date lastUpdate;

    @Column(name = "LAST_UPDATED_BY", length = 20)
    private String lastUpdatedBy;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "RESOURCE_GROUP", joinColumns = { @JoinColumn(name = "GRP_ID") }, inverseJoinColumns = { @JoinColumn(name = "RESOURCE_ID") })
    private Set<ResourceEntity> resources;

    @ManyToMany(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH }, fetch = FetchType.LAZY)
    @JoinTable(name = "grp_to_grp_membership",
               joinColumns = { @JoinColumn(name = "MEMBER_GROUP_ID") },
               inverseJoinColumns = { @JoinColumn(name = "GROUP_ID") })
    @Fetch(FetchMode.SUBSELECT)
    private Set<GroupEntity> parentGroups;

    @ManyToMany(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH }, fetch = FetchType.LAZY)
    @JoinTable(name = "grp_to_grp_membership",
               joinColumns = { @JoinColumn(name = "GROUP_ID") },
               inverseJoinColumns = { @JoinColumn(name = "MEMBER_GROUP_ID") })
    @Fetch(FetchMode.SUBSELECT)
    private Set<GroupEntity> childGroups;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = { CascadeType.ALL }, mappedBy="group")
    //@JoinColumn(name = "GRP_ID", referencedColumnName = "GRP_ID")
    @MapKeyColumn(name = "name")
    @Fetch(FetchMode.SUBSELECT)
    private Set<GroupAttributeEntity> attributes;

    @ManyToMany(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH }, fetch = FetchType.LAZY)
    @JoinTable(name = "GRP_ROLE", joinColumns = { @JoinColumn(name = "GRP_ID") }, inverseJoinColumns = { @JoinColumn(name = "ROLE_ID") })
    @Fetch(FetchMode.SUBSELECT)
    private Set<RoleEntity> roles;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "USER_GRP", joinColumns = { @JoinColumn(name = "GRP_ID") }, inverseJoinColumns = { @JoinColumn(name = "USER_ID") })
    private Set<UserEntity> users = new HashSet<UserEntity>(0);

    public String getGrpId() {
        return grpId;
    }

    public void setGrpId(String grpId) {
        this.grpId = grpId;
    }

    public String getGrpName() {
        return grpName;
    }

    public void setGrpName(String grpName) {
        this.grpName = grpName;
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

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    /*
    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getProvisionMethod() {
        return provisionMethod;
    }

    public void setProvisionMethod(String provisionMethod) {
        this.provisionMethod = provisionMethod;
    }

    public String getProvisionObjName() {
        return provisionObjName;
    }

    public void setProvisionObjName(String provisionObjName) {
        this.provisionObjName = provisionObjName;
    }
    */

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

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /*
    public String getMetadataTypeId() {
        return metadataTypeId;
    }

    public void setMetadataTypeId(String metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
    }

    public String getInternalGroupId() {
        return internalGroupId;
    }

    public void setInternalGroupId(String internalGroupId) {
        this.internalGroupId = internalGroupId;
    }
    */

    public Set<GroupEntity> getParentGroups() {
        return parentGroups;
    }

    public void addChildGroup(final GroupEntity entity) {
        if (entity != null) {
            if (childGroups == null) {
                childGroups = new HashSet<GroupEntity>();
            }
            childGroups.add(entity);
        }
    }

    public void setParentGroups(Set<GroupEntity> parentGroups) {
        this.parentGroups = parentGroups;
    }

    public boolean hasChildGroup(final String groupId) {
        boolean retVal = false;
        if (groupId != null) {
            if (childGroups != null) {
                for (final GroupEntity entity : childGroups) {
                    if (entity.getGrpId().equals(groupId)) {
                        retVal = true;
                        break;
                    }
                }
            }
        }
        return retVal;
    }

    public void removeChildGroup(final String groupId) {
        if (groupId != null) {
            if (childGroups != null) {
                for (final Iterator<GroupEntity> it = childGroups.iterator(); it.hasNext();) {
                    final GroupEntity group = it.next();
                    if (group.getGrpId().equals(groupId)) {
                        it.remove();
                        break;
                    }
                }
            }
        }
    }

    public Set<GroupEntity> getChildGroups() {
        return childGroups;
    }

    public void setChildGroups(Set<GroupEntity> childGroups) {
        this.childGroups = childGroups;
    }

    public Set<GroupAttributeEntity> getAttributes() {
		return attributes;
	}

	public void setAttributes(Set<GroupAttributeEntity> attributes) {
		this.attributes = attributes;
	}

	public Set<ResourceEntity> getResources() {
        return resources;
    }

    public void setResources(Set<ResourceEntity> resources) {
        this.resources = resources;
    }

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
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
		result = prime * result + ((grpId == null) ? 0 : grpId.hashCode());
		result = prime * result + ((grpName == null) ? 0 : grpName.hashCode());
		result = prime * result
				+ ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
		result = prime * result
				+ ((lastUpdatedBy == null) ? 0 : lastUpdatedBy.hashCode());
		result = prime * result
				+ ((managedSystem == null) ? 0 : managedSystem.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		
		result = prime * result
				+ ((companyId == null) ? 0 : companyId.hashCode());
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
		GroupEntity other = (GroupEntity) obj;
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
		if (grpId == null) {
			if (other.grpId != null)
				return false;
		} else if (!grpId.equals(other.grpId))
			return false;
		if (grpName == null) {
			if (other.grpName != null)
				return false;
		} else if (!grpName.equals(other.grpName))
			return false;
		if (lastUpdate == null) {
			if (other.lastUpdate != null)
				return false;
		} else if (!lastUpdate.equals(other.lastUpdate))
			return false;
		if (lastUpdatedBy == null) {
			if (other.lastUpdatedBy != null)
				return false;
		} else if (!lastUpdatedBy.equals(other.lastUpdatedBy))
			return false;
		if (managedSystem == null) {
			if (other.managedSystem != null)
				return false;
		} else if (!managedSystem.equals(other.managedSystem))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (companyId == null) {
			if (other.companyId != null)
				return false;
		} else if (!companyId.equals(other.companyId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("GroupEntity [grpId=%s, grpName=%s, createDate=%s, createdBy=%s, managedSystem=%s, description=%s, status=%s, lastUpdate=%s, lastUpdatedBy=%s]",
						grpId, grpName, createDate, createdBy, managedSystem,
						description, status, lastUpdate, lastUpdatedBy);
	}

    
}
