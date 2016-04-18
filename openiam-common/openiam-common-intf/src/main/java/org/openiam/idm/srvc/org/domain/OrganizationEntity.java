package org.openiam.idm.srvc.org.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.openiam.base.domain.AbstractMetdataTypeEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.loc.domain.LocationEntity;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.internationalization.Internationalized;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Table(name = "COMPANY")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "OrganizationEntity")
@DozerDTOCorrespondence(Organization.class)
@AttributeOverride(name = "id", column = @Column(name = "COMPANY_ID"))
@Internationalized
public class OrganizationEntity extends AbstractMetdataTypeEntity {

    @Column(name = "ALIAS", length = 100)
    @Size(max = 100, message = "organization.alias.too.long")
    private String alias;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "organization", fetch = FetchType.LAZY)
    @OrderBy("name asc")
//    @Fetch(FetchMode.SUBSELECT)
    @Internationalized
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<OrganizationAttributeEntity> attributes;

    @Column(name = "CREATE_DATE", length = 19)
    private Date createDate;

    @Column(name = "CREATED_BY", length = 32)
    private String createdBy;

    @Column(name = "DESCRIPTION", length = 512)
    @Size(max = 512, message = "organization.description.too.long")
    private String description;

    @Column(name = "DOMAIN_NAME", length = 250)
    @Size(max = 250, message = "organization.domain.name.too.long")
    private String domainName;

    @Column(name = "LDAP_STR")
    private String ldapStr;

    @Column(name = "LST_UPDATE", length = 19)
    private Date lstUpdate;

    @Column(name = "LST_UPDATED_BY", length = 32)
    private String lstUpdatedBy;

    @Column(name = "COMPANY_NAME", length = 200)
    @Size(max = 200, message = "organization.name.too.long")
    private String name;

    @Column(name = "INTERNAL_COMPANY_ID")
    private String internalOrgId;

    @Column(name = "STATUS", length = 20)
    private String status;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "ORG_TYPE_ID", referencedColumnName = "ORG_TYPE_ID", insertable = true, updatable = true)
    @Internationalized
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private OrganizationTypeEntity organizationType;

    @Column(name = "ABBREVIATION", length = 20)
    @Size(max = 20, message = "organization.abbreviation.too.long")
    private String abbreviation;

    @Column(name = "SYMBOL", length = 10)
    @Size(max = 10, message = "organization.symbol.too.long")
    private String symbol;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(name = "COMPANY_TO_COMPANY_MEMBERSHIP",
            joinColumns = {@JoinColumn(name = "MEMBER_COMPANY_ID")},
            inverseJoinColumns = {@JoinColumn(name = "COMPANY_ID")})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<OrganizationEntity> parentOrganizations;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(name = "COMPANY_TO_COMPANY_MEMBERSHIP",
            joinColumns = {@JoinColumn(name = "COMPANY_ID")},
            inverseJoinColumns = {@JoinColumn(name = "MEMBER_COMPANY_ID")})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<OrganizationEntity> childOrganizations;

    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "primaryKey.organization")
    //@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public Set<OrganizationUserEntity> organizationUser;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name = "ADMIN_RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = true, updatable = true, nullable = true)
    private ResourceEntity adminResource;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "associationEntityId", orphanRemoval = true)
    @Where(clause = "ASSOCIATION_TYPE='ORGANIZATION'")
    private Set<ApproverAssociationEntity> approverAssociations;

    @Column(name = "IS_SELECTABLE")
    @Type(type = "yes_no")
    private boolean selectable = true;

    @Column(name = "CLASSIFICATION")
    private String classification;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "organization", fetch = FetchType.LAZY)
    @OrderBy("name asc")
//    @Fetch(FetchMode.SUBSELECT)
    @Internationalized
    private Set<LocationEntity> locations;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(name = "GROUP_ORGANIZATION", joinColumns = {@JoinColumn(name = "COMPANY_ID")}, inverseJoinColumns = {@JoinColumn(name = "GRP_ID")})
//    @Fetch(FetchMode.SUBSELECT)
    private Set<GroupEntity> groups;


    public OrganizationEntity() {
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Set<OrganizationAttributeEntity> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<OrganizationAttributeEntity> attributes) {
        this.attributes = attributes;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getLdapStr() {
        return ldapStr;
    }

    public void setLdapStr(String ldapStr) {
        this.ldapStr = ldapStr;
    }

    public Date getLstUpdate() {
        return lstUpdate;
    }

    public void setLstUpdate(Date lstUpdate) {
        this.lstUpdate = lstUpdate;
    }

    public String getLstUpdatedBy() {
        return lstUpdatedBy;
    }

    public void setLstUpdatedBy(String lstUpdatedBy) {
        this.lstUpdatedBy = lstUpdatedBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInternalOrgId() {
        return internalOrgId;
    }

    public void setInternalOrgId(String internalOrgId) {
        this.internalOrgId = internalOrgId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OrganizationTypeEntity getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(OrganizationTypeEntity organizationType) {
        this.organizationType = organizationType;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Set<OrganizationEntity> getParentOrganizations() {
        return parentOrganizations;
    }

    public void setParentOrganizations(Set<OrganizationEntity> parentOrganizations) {
        this.parentOrganizations = parentOrganizations;
    }

    public Set<OrganizationEntity> getChildOrganizations() {
        return childOrganizations;
    }

    public void setChildOrganizations(Set<OrganizationEntity> childOrganizations) {
        this.childOrganizations = childOrganizations;
    }

    public void addChildOrganization(final OrganizationEntity entity) {
        if (entity != null) {
            if (childOrganizations == null) {
                childOrganizations = new LinkedHashSet<OrganizationEntity>();
            }
            childOrganizations.add(entity);
        }
    }

    public void removeChildOrganization(final String organizationId) {
        if (organizationId != null) {
            if (childOrganizations != null) {
                for (final Iterator<OrganizationEntity> it = childOrganizations.iterator(); it.hasNext(); ) {
                    final OrganizationEntity entity = it.next();
                    if (entity.getId().equals(organizationId)) {
                        it.remove();
                        break;
                    }
                }
            }
        }
    }

    public boolean hasChildOrganization(final String organizationId) {
        boolean retval = false;
        if (organizationId != null) {
            if (childOrganizations != null) {
                for (final OrganizationEntity entity : childOrganizations) {
                    if (entity.getId().equals(organizationId)) {
                        retval = true;
                        break;
                    }
                }
            }
        }
        return retval;
    }

    public Set<OrganizationUserEntity> getOrganizationUser() {
        return organizationUser;
    }

    public void setOrganizationUser(Set<OrganizationUserEntity> organizationUser) {
        this.organizationUser = organizationUser;
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

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public void addApproverAssociation(final ApproverAssociationEntity entity) {
        if (entity != null) {
            if (this.approverAssociations == null) {
                this.approverAssociations = new HashSet<ApproverAssociationEntity>();
            }
            this.approverAssociations.add(entity);
        }
    }


    public Set<LocationEntity> getLocations() {
        return locations;
    }

    public void setLocations(Set<LocationEntity> locations) {
        this.locations = locations;
    }

    public Set<GroupEntity> getGroups() {
        return groups;
    }

    public void setGroups(Set<GroupEntity> groups) {
        this.groups = groups;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((abbreviation == null) ? 0 : abbreviation.hashCode());
//        result = prime * result
//                + ((adminResource == null) ? 0 : adminResource.hashCode());
        result = prime * result + ((alias == null) ? 0 : alias.hashCode());
        result = prime * result
                + ((createDate == null) ? 0 : createDate.hashCode());
        result = prime * result
                + ((createdBy == null) ? 0 : createdBy.hashCode());
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result
                + ((domainName == null) ? 0 : domainName.hashCode());
        result = prime * result
                + ((internalOrgId == null) ? 0 : internalOrgId.hashCode());
        result = prime * result + ((ldapStr == null) ? 0 : ldapStr.hashCode());
        result = prime * result
                + ((lstUpdate == null) ? 0 : lstUpdate.hashCode());
        result = prime * result
                + ((lstUpdatedBy == null) ? 0 : lstUpdatedBy.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
//        result = prime
//                * result
//                + ((organizationType == null) ? 0 : organizationType.hashCode());
        result = prime * result + (selectable ? 1231 : 1237);
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        OrganizationEntity other = (OrganizationEntity) obj;
        if (abbreviation == null) {
            if (other.abbreviation != null)
                return false;
        } else if (!abbreviation.equals(other.abbreviation))
            return false;
        if (adminResource == null) {
            if (other.adminResource != null)
                return false;
        } else if (!adminResource.equals(other.adminResource))
            return false;
        if (alias == null) {
            if (other.alias != null)
                return false;
        } else if (!alias.equals(other.alias))
            return false;
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
        if (domainName == null) {
            if (other.domainName != null)
                return false;
        } else if (!domainName.equals(other.domainName))
            return false;
        if (internalOrgId == null) {
            if (other.internalOrgId != null)
                return false;
        } else if (!internalOrgId.equals(other.internalOrgId))
            return false;
        if (ldapStr == null) {
            if (other.ldapStr != null)
                return false;
        } else if (!ldapStr.equals(other.ldapStr))
            return false;
        if (lstUpdate == null) {
            if (other.lstUpdate != null)
                return false;
        } else if (!lstUpdate.equals(other.lstUpdate))
            return false;
        if (lstUpdatedBy == null) {
            if (other.lstUpdatedBy != null)
                return false;
        } else if (!lstUpdatedBy.equals(other.lstUpdatedBy))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (organizationType == null) {
            if (other.organizationType != null)
                return false;
        } else if (!organizationType.equals(other.organizationType))
            return false;
        if (selectable != other.selectable)
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (symbol == null) {
            if (other.symbol != null)
                return false;
        } else if (!symbol.equals(other.symbol))
            return false;
        return true;
    }


}
