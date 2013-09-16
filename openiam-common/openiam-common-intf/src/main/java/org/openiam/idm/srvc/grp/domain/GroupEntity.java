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

    @Column(name = "COMPANY_ID", length = 20)
    private String companyId;

    @Column(name = "OWNER_ID", length = 20)
    private String ownerId;

    @Column(name = "PROVISION_METHOD", length = 20)
    private String provisionMethod;

    @Column(name = "PROVISION_OBJ_NAME", length = 80)
    private String provisionObjName;

    @Column(name = "GROUP_DESC", length = 80)
    @Size(max = 80, message = "group.description.too.long")
    private String description;

    @Column(name = "STATUS", length = 20)
    private String status;

    @Column(name = "LAST_UPDATE", length = 19)
    private Date lastUpdate;

    @Column(name = "LAST_UPDATED_BY", length = 20)
    private String lastUpdatedBy;

    @Column(name = "TYPE_ID", length = 20)
    private String metadataTypeId;

    @Column(name = "INTERNAL_GROUP_ID", length = 32)
    private String internalGroupId;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupEntity that = (GroupEntity) o;

        if (companyId != null ? !companyId.equals(that.companyId) : that.companyId != null) return false;
        if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null) return false;
        if (createdBy != null ? !createdBy.equals(that.createdBy) : that.createdBy != null) return false;
        if (grpId != null ? !grpId.equals(that.grpId) : that.grpId != null) return false;
        if (grpName != null ? !grpName.equals(that.grpName) : that.grpName != null) return false;
        if (ownerId != null ? !ownerId.equals(that.ownerId) : that.ownerId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = grpId != null ? grpId.hashCode() : 0;
        result = 31 * result + (grpName != null ? grpName.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (companyId != null ? companyId.hashCode() : 0);
        result = 31 * result + (ownerId != null ? ownerId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("GroupEntity [grpId=%s, grpName=%s, createDate=%s, createdBy=%s, companyId=%s, ownerId=%s, provisionMethod=%s, provisionObjName=%s, description=%s, status=%s, lastUpdate=%s, lastUpdatedBy=%s, metadataTypeId=%s, internalGroupId=%s]",
                             grpId, grpName, createDate, createdBy, companyId, ownerId, provisionMethod, provisionObjName, description, status,
                             lastUpdate, lastUpdatedBy, metadataTypeId, internalGroupId);
    }

}
