package org.openiam.idm.srvc.org.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.org.dto.Organization;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.DocumentId;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;
import org.openiam.idm.srvc.role.domain.RoleEntity;

@Entity
@Table(name = "COMPANY")
@DozerDTOCorrespondence(Organization.class)
public class OrganizationEntity {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="COMPANY_ID", length=32, nullable = false)
    @DocumentId
    private String id;

    @Column(name="ALIAS", length=100)
    private String alias;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "organization", fetch = FetchType.LAZY)
    @MapKeyColumn(name="name")
    @Fetch(FetchMode.SUBSELECT)
    private Map<String, OrganizationAttributeEntity> attributes = new HashMap<String, OrganizationAttributeEntity>(0);

    @Column(name="CREATE_DATE", length=19)
    private Date createDate;

    @Column(name="CREATED_BY", length=20)
    private String createdBy;

    @Column(name="DESCRIPTION", length=100)
    private String description;

    @Column(name="DOMAIN_NAME", length=40)
    private String domainName;

    @Column(name="LDAP_STR")
    private String ldapStr;

    @Column(name="LST_UPDATE", length=19)
    private Date lstUpdate;

    @Column(name="LST_UPDATED_BY", length=20)
    private String lstUpdatedBy;

    @Column(name="COMPANY_NAME", length=200)
    private String organizationName;

    @Column(name="INTERNAL_COMPANY_ID")
    private String internalOrgId;

    @Column(name="STATUS", length=20)
    private String status;
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "ORG_TYPE_ID", referencedColumnName = "ORG_TYPE_ID", insertable = true, updatable = true)
    private OrganizationTypeEntity organizationType;

    @Column(name="ABBREVIATION", length=20)
    private String abbreviation;

    @Column(name="SYMBOL", length=10)
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
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "organization")
	private Set<UserAffiliationEntity> affiliations;

    public OrganizationEntity() {
    }

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Map<String, OrganizationAttributeEntity> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, OrganizationAttributeEntity> attributes) {
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

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
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

	public Set<UserAffiliationEntity> getAffiliations() {
		return affiliations;
	}

	public void setAffiliations(Set<UserAffiliationEntity> affiliations) {
		this.affiliations = affiliations;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((abbreviation == null) ? 0 : abbreviation.hashCode());
		result = prime * result + ((alias == null) ? 0 : alias.hashCode());
		result = prime * result
				+ ((createDate == null) ? 0 : createDate.hashCode());
		result = prime * result
				+ ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((domainName == null) ? 0 : domainName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((internalOrgId == null) ? 0 : internalOrgId.hashCode());
		result = prime * result + ((ldapStr == null) ? 0 : ldapStr.hashCode());
		result = prime * result
				+ ((lstUpdate == null) ? 0 : lstUpdate.hashCode());
		result = prime * result
				+ ((lstUpdatedBy == null) ? 0 : lstUpdatedBy.hashCode());
		result = prime
				* result
				+ ((organizationName == null) ? 0 : organizationName.hashCode());
		result = prime
				* result
				+ ((organizationType == null) ? 0 : organizationType.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
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
		OrganizationEntity other = (OrganizationEntity) obj;
		if (abbreviation == null) {
			if (other.abbreviation != null)
				return false;
		} else if (!abbreviation.equals(other.abbreviation))
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
		if (organizationName == null) {
			if (other.organizationName != null)
				return false;
		} else if (!organizationName.equals(other.organizationName))
			return false;
		if (organizationType == null) {
			if (other.organizationType != null)
				return false;
		} else if (!organizationType.equals(other.organizationType))
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
