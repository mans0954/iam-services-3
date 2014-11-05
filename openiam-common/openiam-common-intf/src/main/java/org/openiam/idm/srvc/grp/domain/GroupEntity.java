package org.openiam.idm.srvc.grp.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;
import org.openiam.base.domain.AbstractMetdataTypeEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.internationalization.Internationalized;

@Entity
@Table(name = "GRP")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "GRP_ID"))
@DozerDTOCorrespondence(Group.class)
@Internationalized
public class GroupEntity extends AbstractMetdataTypeEntity {

    @Column(name = "GRP_NAME", length = 255)
    @Size(max = 255, message = "group.name.too.long")
    private String name;

    @Column(name = "CREATE_DATE", length = 19)
    private Date createDate;

    @Column(name = "CREATED_BY", length = 32)
    private String createdBy;
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "MANAGED_SYS_ID", referencedColumnName = "MANAGED_SYS_ID", insertable = true, updatable = true, nullable=true)
    private ManagedSysEntity managedSystem;
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "COMPANY_ID", referencedColumnName = "COMPANY_ID", insertable = true, updatable = true)
    private OrganizationEntity company;

    @Column(name = "GROUP_DESC", length = 512)
    @Size(max = 512, message = "group.description.too.long")
    private String description;

    @Column(name = "STATUS", length = 20)
    private String status;

    @Column(name = "LAST_UPDATE", length = 19)
    private Date lastUpdate;

    @Column(name = "LAST_UPDATED_BY", length = 32)
    private String lastUpdatedBy;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "RESOURCE_GROUP",
            joinColumns = { @JoinColumn(name = "GRP_ID") },
            inverseJoinColumns = { @JoinColumn(name = "RESOURCE_ID") })
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
    @Internationalized
    private Set<GroupAttributeEntity> attributes;

    @ManyToMany(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH }, fetch = FetchType.LAZY)
    @JoinTable(name = "GRP_ROLE", joinColumns = { @JoinColumn(name = "GRP_ID") }, inverseJoinColumns = { @JoinColumn(name = "ROLE_ID") })
    @Fetch(FetchMode.SUBSELECT)
    private Set<RoleEntity> roles;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "USER_GRP", joinColumns = { @JoinColumn(name = "GRP_ID") }, inverseJoinColumns = { @JoinColumn(name = "USER_ID") })
    private Set<UserEntity> users = new HashSet<UserEntity>(0);
    
	@ManyToOne(fetch = FetchType.EAGER,cascade={CascadeType.ALL})
    @JoinColumn(name="ADMIN_RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = true, updatable = true, nullable=true)
	private ResourceEntity adminResource;
	
	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy="associationEntityId", orphanRemoval=true)
	@Where(clause="ASSOCIATION_TYPE='GROUP'")
	private Set<ApproverAssociationEntity> approverAssociations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public OrganizationEntity getCompany() {
		return company;
	}

	public void setCompany(OrganizationEntity company) {
		this.company = company;
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
                    if (entity.getId().equals(groupId)) {
                        retVal = true;
                        break;
                    }
                }
            }
        }
        return retVal;
    }

    public void removeParentGroup(String parentGroupId) {
        if (parentGroupId != null) {
            if (parentGroups != null) {
                Iterator<GroupEntity> it = parentGroups.iterator();
                while (it.hasNext()) {
                    final GroupEntity g = it.next();
                    if (g.getId().equals(parentGroupId)) {
                        it.remove();
                        break;
                    }
                }
            }
        }
    }

    public void removeChildGroup(final String groupId) {
        if (groupId != null) {
            if (childGroups != null) {
                for (final Iterator<GroupEntity> it = childGroups.iterator(); it.hasNext();) {
                    final GroupEntity group = it.next();
                    if (group.getId().equals(groupId)) {
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
    public void addResource(final ResourceEntity entity) {
        if(entity != null) {
            if(this.resources == null) {
                this.resources = new HashSet<ResourceEntity>();
            }
            this.resources.add(entity);
        }
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

	public ResourceEntity getAdminResource() {
		return adminResource;
	}

	public void setAdminResource(ResourceEntity adminResource) {
		this.adminResource = adminResource;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupEntity)) return false;
        if (!super.equals(o)) return false;

        GroupEntity that = (GroupEntity) o;

        if (company != null ? !company.equals(that.company) : that.company != null) return false;
        if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null) return false;
        if (createdBy != null ? !createdBy.equals(that.createdBy) : that.createdBy != null) return false;
        if (managedSystem != null ? !managedSystem.equals(that.managedSystem) : that.managedSystem != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (managedSystem != null ? managedSystem.hashCode() : 0);
        result = 31 * result + (company != null ? company.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
	public String toString() {
		return String
				.format("GroupEntity [id=%s, name=%s, createDate=%s, createdBy=%s, managedSystem=%s, description=%s, status=%s, lastUpdate=%s, lastUpdatedBy=%s]",
						id, name, createDate, createdBy, managedSystem,
						description, status, lastUpdate, lastUpdatedBy);
	}

    
}
