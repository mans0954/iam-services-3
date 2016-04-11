package org.openiam.idm.srvc.org.domain;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.openiam.base.domain.AbstractMetdataTypeEntity;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.elasticsearch.constants.ESIndexName;
import org.openiam.elasticsearch.constants.ESIndexType;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.loc.domain.LocationEntity;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.domain.RoleToRoleMembershipXrefEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.domain.UserToGroupMembershipXrefEntity;
import org.openiam.idm.srvc.user.domain.UserToOrganizationMembershipXrefEntity;
import org.openiam.idm.srvc.user.domain.UserToRoleMembershipXrefEntity;
import org.openiam.internationalization.Internationalized;
import org.springframework.data.elasticsearch.annotations.Document;

@Entity
@Table(name = "COMPANY")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(Organization.class)
@AttributeOverride(name = "id", column = @Column(name = "COMPANY_ID"))
@Internationalized
@Document(indexName = ESIndexName.ORGANIZATION, type= ESIndexType.ORGANIZATION)
public class OrganizationEntity extends AbstractMetdataTypeEntity {
    
    @Column(name="ALIAS", length=100)
    @Size(max = 100, message = "organization.alias.too.long")
    private String alias;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "organization", fetch = FetchType.LAZY)
    @OrderBy("name asc")
    @Fetch(FetchMode.SUBSELECT)
    @Internationalized
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<OrganizationAttributeEntity> attributes;

    @Column(name="CREATE_DATE", length=19)
    private Date createDate;

    @Column(name="CREATED_BY", length=32)
    private String createdBy;

    @Column(name="DESCRIPTION", length=512)
    @Size(max = 512, message = "organization.description.too.long")
    private String description;

    @Column(name="DOMAIN_NAME", length=250)
    @Size(max = 250, message = "organization.domain.name.too.long")
    private String domainName;

    @Column(name="LDAP_STR")
    private String ldapStr;

    @Column(name="LST_UPDATE", length=19)
    private Date lstUpdate;

    @Column(name="LST_UPDATED_BY", length=32)
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
    @Internationalized
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private OrganizationTypeEntity organizationType;

    @Column(name="ABBREVIATION", length=20)
    @Size(max = 20, message = "organization.abbreviation.too.long")
    private String abbreviation;

    @Column(name="SYMBOL", length=10)
    @Size(max = 10, message = "organization.symbol.too.long")
    private String symbol;
    
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="memberEntity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
    private Set<OrgToOrgMembershipXrefEntity> parentOrganizations = new HashSet<OrgToOrgMembershipXrefEntity>(0);

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="entity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
    private Set<OrgToOrgMembershipXrefEntity> childOrganizations = new HashSet<OrgToOrgMembershipXrefEntity>(0);

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="entity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
	private Set<UserToOrganizationMembershipXrefEntity> users;
	
	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy="associationEntityId", orphanRemoval=true)
	@Where(clause="ASSOCIATION_TYPE='ORGANIZATION'")
	private Set<ApproverAssociationEntity> approverAssociations;
	
	@Column(name = "IS_SELECTABLE")
    @Type(type = "yes_no")
	private boolean selectable = true;

    @OneToMany(orphanRemoval = true, cascade = {CascadeType.ALL}, mappedBy = "organization", fetch = FetchType.LAZY)
    @OrderBy("name asc")
    @Fetch(FetchMode.SUBSELECT)
    @Internationalized
    private Set<LocationEntity> locations;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="entity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
	private Set<GroupToOrgMembershipXrefEntity> groups;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="entity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
	private Set<RoleToOrgMembershipXrefEntity> roles;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="entity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
	private Set<ResourceToOrgMembershipXrefEntity> resources;

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

	public Set<OrgToOrgMembershipXrefEntity> getParentOrganizations() {
		return parentOrganizations;
	}

	public void setParentOrganizations(Set<OrgToOrgMembershipXrefEntity> parentOrganizations) {
		this.parentOrganizations = parentOrganizations;
	}

	public Set<OrgToOrgMembershipXrefEntity> getChildOrganizations() {
		return childOrganizations;
	}

	public void setChildOrganizations(Set<OrgToOrgMembershipXrefEntity> childOrganizations) {
		this.childOrganizations = childOrganizations;
	}
	
	public ResourceToOrgMembershipXrefEntity getResource(final String resourceId) {
		final Optional<ResourceToOrgMembershipXrefEntity> xref = 
    			this.getResources()
    				.stream()
    				.filter(e -> resourceId.equals(e.getMemberEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
	}
	
	public RoleToOrgMembershipXrefEntity getRole(final String roleId) {
		final Optional<RoleToOrgMembershipXrefEntity> xref = 
    			this.getRoles()
    				.stream()
    				.filter(e -> roleId.equals(e.getMemberEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
	}
	
	public GroupToOrgMembershipXrefEntity getGroup(final String groupId) {
		final Optional<GroupToOrgMembershipXrefEntity> xref = 
    			this.getGroups()
    				.stream()
    				.filter(e -> groupId.equals(e.getMemberEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
	}
	
	public OrgToOrgMembershipXrefEntity getChild(final String childId) {
		final Optional<OrgToOrgMembershipXrefEntity> xref = 
    			this.getChildOrganizations()
    				.stream()
    				.filter(e -> childId.equals(e.getMemberEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
	}
	
	public OrgToOrgMembershipXrefEntity getParent(final String parentId) {
    	final Optional<OrgToOrgMembershipXrefEntity> xref = 
    			this.getParentOrganizations()
    				.stream()
    				.filter(e -> parentId.equals(e.getEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
    }
	
	public void addUser(final UserEntity entity, final AccessRightEntity right, final Date startDate, final Date endDate) {
		if(entity != null && right != null) {
			final Set<AccessRightEntity> rightSet = new HashSet<AccessRightEntity>();
			rightSet.add(right);
			addUser(entity, rightSet, startDate, endDate);
		}
	}
	
	public void addUser(final UserEntity entity, final Collection<AccessRightEntity> rights, final Date startDate, final Date endDate) {
		if(entity != null) {
			if(this.users == null) {
				this.users = new LinkedHashSet<UserToOrganizationMembershipXrefEntity>();
			}
			UserToOrganizationMembershipXrefEntity theXref = null;
			for(final UserToOrganizationMembershipXrefEntity xref : this.users) {
				if(xref.getEntity().getId().equals(getId()) && xref.getMemberEntity().getId().equals(entity.getId())) {
					theXref = xref;
					break;
				}
			}
			
			if(theXref == null) {
				theXref = new UserToOrganizationMembershipXrefEntity();
				theXref.setEntity(this);
				theXref.setMemberEntity(entity);
			}
			if(rights != null) {
				theXref.setRights(new HashSet<AccessRightEntity>(rights));
			}
			theXref.setStartDate(startDate);
			theXref.setEndDate(endDate);
			this.users.add(theXref);
		}
	}

	public void addChild(final OrganizationEntity entity, final Collection<AccessRightEntity> rights, final Date startDate, final Date endDate) {
		if(entity != null) {
			if(this.childOrganizations == null) {
				this.childOrganizations = new LinkedHashSet<OrgToOrgMembershipXrefEntity>();
			}
			OrgToOrgMembershipXrefEntity theXref = null;
			for(final OrgToOrgMembershipXrefEntity xref : this.childOrganizations) {
				if(xref.getEntity().getId().equals(getId()) && xref.getMemberEntity().getId().equals(entity.getId())) {
					theXref = xref;
					break;
				}
			}
			
			if(theXref == null) {
				theXref = new OrgToOrgMembershipXrefEntity();
				theXref.setEntity(this);
				theXref.setMemberEntity(entity);
			}
			if(rights != null) {
				theXref.setRights(new HashSet<AccessRightEntity>(rights));
			}
			theXref.setStartDate(startDate);
			theXref.setEndDate(endDate);
			this.childOrganizations.add(theXref);
		}
	}
	
	public boolean hasChild(final OrganizationEntity entity) {
		boolean contains = false;
		if(childOrganizations != null) {
			contains = (childOrganizations.stream().map(e -> e.getMemberEntity()).filter(e -> e.getId().equals(entity.getId())).count() > 0);
		}
		return contains;
	}
	
	public void removeChild(final OrganizationEntity entity) {
		if(entity != null) {
			if(this.childOrganizations != null) {
				this.childOrganizations.removeIf(e -> e.getMemberEntity().getId().equals(entity.getId()));
			}
		}
	}
	
	public UserToOrganizationMembershipXrefEntity getUser(final String userId) {
		final Optional<UserToOrganizationMembershipXrefEntity> xref = 
    			this.getUsers()
    				.stream()
    				.filter(e -> userId.equals(e.getMemberEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
	}

    public Set<UserToOrganizationMembershipXrefEntity> getUsers() {
        return users;
    }

    public void setUsers(Set<UserToOrganizationMembershipXrefEntity> users) {
        this.users = users;
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


    public Set<LocationEntity> getLocations() {
        return locations;
    }

    public void setLocations(Set<LocationEntity> locations) {
        this.locations = locations;
    }

	public Set<GroupToOrgMembershipXrefEntity> getGroups() {
		return groups;
	}

	public void setGroups(Set<GroupToOrgMembershipXrefEntity> groups) {
		this.groups = groups;
	}
	
	public void addGroup(final GroupEntity entity, final Collection<AccessRightEntity> rights, final Date startDate, final Date endDate) {
		if(entity != null) {
			if(this.groups == null) {
				this.groups = new LinkedHashSet<GroupToOrgMembershipXrefEntity>();
			}
			GroupToOrgMembershipXrefEntity theXref = null;
			for(final GroupToOrgMembershipXrefEntity xref : this.groups) {
				if(xref.getEntity().getId().equals(getId()) && xref.getMemberEntity().getId().equals(entity.getId())) {
					theXref = xref;
					break;
				}
			}
			
			if(theXref == null) {
				theXref = new GroupToOrgMembershipXrefEntity();
				theXref.setEntity(this);
				theXref.setMemberEntity(entity);
			}
			if(rights != null) {
				theXref.setRights(new HashSet<AccessRightEntity>(rights));
			}
			theXref.setStartDate(startDate);
			theXref.setEndDate(endDate);
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

    public Set<RoleToOrgMembershipXrefEntity> getRoles() {
		return roles;
	}

	public void setRoles(Set<RoleToOrgMembershipXrefEntity> roles) {
		this.roles = roles;
	}
	
	public void addRole(final RoleEntity entity, final Collection<AccessRightEntity> rights, final Date startDate, final Date endDate) {
		if(entity != null) {
			if(this.roles == null) {
				this.roles = new LinkedHashSet<RoleToOrgMembershipXrefEntity>();
			}
			RoleToOrgMembershipXrefEntity theXref = null;
			for(final RoleToOrgMembershipXrefEntity xref : this.roles) {
				if(xref.getEntity().getId().equals(getId()) && xref.getMemberEntity().getId().equals(entity.getId())) {
					theXref = xref;
					break;
				}
			}
			
			if(theXref == null) {
				theXref = new RoleToOrgMembershipXrefEntity();
				theXref.setEntity(this);
				theXref.setMemberEntity(entity);
			}
			if(rights != null) {
				theXref.setRights(new HashSet<AccessRightEntity>(rights));
			}
			theXref.setStartDate(startDate);
			theXref.setEndDate(endDate);
			this.roles.add(theXref);
		}
	}
	
	public void removeRole(final RoleEntity entity) {
		if(entity != null) {
			if(this.roles != null) {
				this.roles.removeIf(e -> e.getMemberEntity().getId().equals(entity.getId()));
			}
		}
	}

	public Set<ResourceToOrgMembershipXrefEntity> getResources() {
		return resources;
	}

	public void setResources(Set<ResourceToOrgMembershipXrefEntity> resources) {
		this.resources = resources;
	}
	
	public void addResource(final ResourceEntity entity, final Collection<AccessRightEntity> rights, final Date startDate, final Date endDate) {
		if(entity != null) {
			if(this.resources == null) {
				this.resources = new LinkedHashSet<ResourceToOrgMembershipXrefEntity>();
			}
			ResourceToOrgMembershipXrefEntity theXref = null;
			for(final ResourceToOrgMembershipXrefEntity xref : this.resources) {
				if(xref.getEntity().getId().equals(getId()) && xref.getMemberEntity().getId().equals(entity.getId())) {
					theXref = xref;
					break;
				}
			}
			
			if(theXref == null) {
				theXref = new ResourceToOrgMembershipXrefEntity();
				theXref.setEntity(this);
				theXref.setMemberEntity(entity);
			}
			if(rights != null) {
				theXref.setRights(new HashSet<AccessRightEntity>(rights));
			}
			theXref.setStartDate(startDate);
			theXref.setEndDate(endDate);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
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
		result = prime * result
				+ ((internalOrgId == null) ? 0 : internalOrgId.hashCode());
		result = prime * result + ((ldapStr == null) ? 0 : ldapStr.hashCode());
		result = prime * result
				+ ((lstUpdate == null) ? 0 : lstUpdate.hashCode());
		result = prime * result
				+ ((lstUpdatedBy == null) ? 0 : lstUpdatedBy.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime
				* result
				+ ((organizationType == null) ? 0 : organizationType.hashCode());
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
