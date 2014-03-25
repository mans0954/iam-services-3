package org.openiam.idm.srvc.org.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.hibernate.search.annotations.DocumentId;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;

@Entity
@Table(name = "COMPANY")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(Organization.class)
@AttributeOverride(name = "id", column = @Column(name = "COMPANY_ID"))
public class OrganizationEntity extends KeyEntity {
    
    @Column(name="ALIAS", length=100)
    @Size(max = 100, message = "organization.alias.too.long")
    private String alias;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "organization", fetch = FetchType.LAZY)
    @OrderBy("name asc")
    @Fetch(FetchMode.SUBSELECT)
    private Set<OrganizationAttributeEntity> attributes;

    @Column(name="CREATE_DATE", length=19)
    private Date createDate;

    @Column(name="CREATED_BY", length=20)
    private String createdBy;

    @Column(name="DESCRIPTION", length=100)
    @Size(max = 100, message = "organization.description.too.long")
    private String description;

    @Column(name="DOMAIN_NAME", length=250)
    @Size(max = 250, message = "organization.domain.name.too.long")
    private String domainName;

    @Column(name="LDAP_STR")
    private String ldapStr;

    @Column(name="LST_UPDATE", length=19)
    private Date lstUpdate;

    @Column(name="LST_UPDATED_BY", length=20)
    private String lstUpdatedBy;

    @Column(name="COMPANY_NAME", length=200)
    @Size(max = 200, message = "organization.name.too.long")
    private String name;

    @Column(name="INTERNAL_COMPANY_ID")
    private String internalOrgId;

    @Column(name="STATUS", length=20)
    private String status;
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "ORG_TYPE_ID", referencedColumnName = "ORG_TYPE_ID", insertable = true, updatable = true)
    private OrganizationTypeEntity organizationType;

    @Column(name="ABBREVIATION", length=20)
    @Size(max = 20, message = "organization.abbreviation.too.long")
    private String abbreviation;

    @Column(name="SYMBOL", length=10)
    @Size(max = 10, message = "organization.symbol.too.long")
    private String symbol;
    
	@ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.LAZY)
    @JoinTable(name="COMPANY_TO_COMPANY_MEMBERSHIP",
        joinColumns={@JoinColumn(name="MEMBER_COMPANY_ID")},
        inverseJoinColumns={@JoinColumn(name="COMPANY_ID")})
    @Fetch(FetchMode.SUBSELECT)
    private Set<OrganizationEntity> parentOrganizations;
    
	@ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.LAZY)
    @JoinTable(name="COMPANY_TO_COMPANY_MEMBERSHIP",
        joinColumns={@JoinColumn(name="COMPANY_ID")},
        inverseJoinColumns={@JoinColumn(name="MEMBER_COMPANY_ID")})
    @Fetch(FetchMode.SUBSELECT)
    private Set<OrganizationEntity> childOrganizations;

	@ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.LAZY)
    @JoinTable(name = "USER_AFFILIATION", joinColumns = { @JoinColumn(name = "COMPANY_ID") }, inverseJoinColumns = { @JoinColumn(name = "USER_ID") })
	private Set<UserEntity> users;
	
	@ManyToOne(fetch = FetchType.EAGER,cascade={CascadeType.ALL})
    @JoinColumn(name="ADMIN_RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = true, updatable = true, nullable=true)
	private ResourceEntity adminResource;
	
	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy="associationEntityId", orphanRemoval=true)
	@Where(clause="ASSOCIATION_TYPE='ORGANIZATION'")
	private Set<ApproverAssociationEntity> approverAssociations;
	
	@Column(name = "IS_SELECTABLE")
    @Type(type = "yes_no")
	private boolean selectable = true;

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
		if(entity != null) {
			if(childOrganizations == null) {
				childOrganizations = new LinkedHashSet<OrganizationEntity>();
			}
			childOrganizations.add(entity);
		}
	}
	
	public void removeChildOrganization(final String organizationId) {
		if(organizationId != null) {
			if(childOrganizations != null) {
				for(final Iterator<OrganizationEntity> it = childOrganizations.iterator(); it.hasNext();) {
					final OrganizationEntity entity = it.next();
					if(entity.getId().equals(organizationId)) {
						it.remove();
						break;
					}
				}
			}
		}
	}
	
	public boolean hasChildOrganization(final String organizationId) {
		boolean retval = false;
		if(organizationId != null) {
			if(childOrganizations != null) {
				for(final OrganizationEntity entity : childOrganizations) {
					if(entity.getId().equals(organizationId)) {
						retval = true;
						break;
					}
				}
			}
		}
		return retval;
	}

    public Set<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(Set<UserEntity> users) {
        this.users = users;
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
        if (o == null || getClass() != o.getClass()) return false;

        OrganizationEntity that = (OrganizationEntity) o;

        if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null) return false;
        if (createdBy != null ? !createdBy.equals(that.createdBy) : that.createdBy != null) return false;
        if (domainName != null ? !domainName.equals(that.domainName) : that.domainName != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (selectable != that.selectable) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (domainName != null ? domainName.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (selectable ? 1 : 0);
        return result;
    }
}
