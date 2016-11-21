package org.openiam.idm.srvc.grp.domain;

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
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;
import org.openiam.base.domain.AbstractEntitlementPolicyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.elasticsearch.annotation.DocumentRepresentation;
import org.openiam.elasticsearch.annotation.ElasticsearchFieldBridge;
import org.openiam.elasticsearch.bridge.ManagedSysBridge;
import org.openiam.elasticsearch.converter.GroupDocumentToEntityConverter;
import org.openiam.elasticsearch.model.GroupDoc;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.org.domain.GroupToOrgMembershipXrefEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.role.domain.RoleToGroupMembershipXrefEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.domain.UserToGroupMembershipXrefEntity;
import org.openiam.internationalization.Internationalized;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Entity
@Table(name = "GRP")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "GRP_ID"))
@DozerDTOCorrespondence(Group.class)
@Internationalized
@DocumentRepresentation(value=GroupDoc.class, converter=GroupDocumentToEntityConverter.class)
public class GroupEntity extends AbstractEntitlementPolicyEntity {

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
    
    @Column(name = "GROUP_DESC", length = 512)
    @Size(max = 512, message = "group.description.too.long")
    private String description;

    @Column(name = "STATUS", length = 20)
    private String status;

    @Column(name = "LAST_UPDATE", length = 19)
    private Date lastUpdate;

    @Column(name = "LAST_UPDATED_BY", length = 32)
    private String lastUpdatedBy;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="memberEntity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
    private Set<GroupToGroupMembershipXrefEntity> parentGroups = new HashSet<GroupToGroupMembershipXrefEntity>(0);

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="entity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
    private Set<GroupToGroupMembershipXrefEntity> childGroups = new HashSet<GroupToGroupMembershipXrefEntity>(0);

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = { CascadeType.ALL }, mappedBy="group")
    //@JoinColumn(name = "GRP_ID", referencedColumnName = "GRP_ID")
    @MapKeyColumn(name = "name")
    @Fetch(FetchMode.SUBSELECT)
    @Internationalized
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<GroupAttributeEntity> attributes;

    

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="entity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
    private Set<UserToGroupMembershipXrefEntity> users = new HashSet<UserToGroupMembershipXrefEntity>(0);
	
	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy="associationEntityId", orphanRemoval=true)
	@Where(clause="ASSOCIATION_TYPE='GROUP'")
	private Set<ApproverAssociationEntity> approverAssociations;


    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch= FetchType.LAZY)
    @JoinColumn(name = "GRP_CLASSIFICATION", referencedColumnName ="TYPE_ID", insertable = true, updatable = true, nullable=true)
    @Internationalized
    protected MetadataTypeEntity classification;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch= FetchType.LAZY)
    @JoinColumn(name = "AD_GRP_TYPE", referencedColumnName ="TYPE_ID", insertable = true, updatable = true, nullable=true)
    @Internationalized
    protected MetadataTypeEntity adGroupType;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch= FetchType.LAZY)
    @JoinColumn(name = "AD_GRP_SCOPE", referencedColumnName ="TYPE_ID", insertable = true, updatable = true, nullable=true)
    @Internationalized
    protected MetadataTypeEntity adGroupScope;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch= FetchType.LAZY)
    @JoinColumn(name = "GRP_RISK", referencedColumnName ="TYPE_ID", insertable = true, updatable = true, nullable=true)
    @Internationalized
    protected MetadataTypeEntity risk;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="memberEntity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
    private Set<GroupToOrgMembershipXrefEntity> organizations = new HashSet<GroupToOrgMembershipXrefEntity>(0);
    
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="memberEntity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
    private Set<RoleToGroupMembershipXrefEntity> roles;

    @Column(name = "MAX_USER_NUMBER")
    private Integer maxUserNumber;
    
    @Column(name = "MEMBERSHIP_DURATION_SECONDS")
    private Long membershipDuration;
    
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="entity", orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
	private Set<GroupToResourceMembershipXrefEntity> resources;

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

    public Set<GroupToGroupMembershipXrefEntity> getParentGroups() {
        return parentGroups;
    }

    public void addChildGroup(final GroupEntity entity, final AccessRightEntity right, final Date startDate, final Date endDate) {
    	if(entity != null && right != null) {
    		final Set<AccessRightEntity> rights = new HashSet<AccessRightEntity>();
    		rights.add(right);
    		addChildGroup(entity, rights, startDate, endDate);
    	}
    }
    
    public void addChildGroup(final GroupEntity entity, final Collection<AccessRightEntity> rights, final Date startDate, final Date endDate) {
    	if(entity != null) {
			if(this.childGroups == null) {
				this.childGroups = new LinkedHashSet<GroupToGroupMembershipXrefEntity>();
			}
			GroupToGroupMembershipXrefEntity theXref = null;
			for(final GroupToGroupMembershipXrefEntity xref : this.childGroups) {
				if(xref.getEntity().getId().equals(getId()) && xref.getMemberEntity().getId().equals(entity.getId())) {
					theXref = xref;
					break;
				}
			}
			
			if(theXref == null) {
				theXref = new GroupToGroupMembershipXrefEntity();
				theXref.setEntity(this);
				theXref.setMemberEntity(entity);
			}
			if(rights != null) {
				theXref.setRights(new HashSet<AccessRightEntity>(rights));
			}
			theXref.setStartDate(startDate);
			theXref.setEndDate(endDate);
			this.childGroups.add(theXref);
		}
    }
    
    public void setParentGroups(Set<GroupToGroupMembershipXrefEntity> parentGroups) {
        this.parentGroups = parentGroups;
    }

    public boolean hasChildGroup(final String groupId) {
    	boolean contains = false;
		if(childGroups != null) {
			contains = (childGroups.stream().map(e -> e.getMemberEntity()).filter(e -> e.getId().equals(groupId)).count() > 0);
		}
		return contains;
    }

    public void removeChildGroup(final GroupEntity entity) {
    	if(entity != null) {
			if(this.childGroups != null) {
				this.childGroups.removeIf(e -> e.getMemberEntity().getId().equals(entity.getId()));
			}
		}
    }
    
    public GroupToOrgMembershipXrefEntity getOrganization(final String organizationId) {
    	final Optional<GroupToOrgMembershipXrefEntity> xref = 
    			this.getOrganizations()
				.stream()
				.filter(e -> organizationId.equals(e.getEntity().getId()))
				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
    }
    
    public RoleToGroupMembershipXrefEntity getRole(final String roleId) {
    	final Optional<RoleToGroupMembershipXrefEntity> xref = 
    			this.getRoles()
				.stream()
				.filter(e -> roleId.equals(e.getEntity().getId()))
				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
    }
    
	public GroupToGroupMembershipXrefEntity getChild(final String childId) {
		final Optional<GroupToGroupMembershipXrefEntity> xref = 
    			this.getChildGroups()
    				.stream()
    				.filter(e -> childId.equals(e.getMemberEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
	}
    
    public GroupToGroupMembershipXrefEntity getParent(final String parentId) {
    	final Optional<GroupToGroupMembershipXrefEntity> xref = 
    			this.getParentGroups()
    				.stream()
    				.filter(e -> parentId.equals(e.getEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
    }

	public void removeAttribute(final String id) {
		if (id != null && this.attributes != null) {
			final Set<GroupAttributeEntity> entrySet = this.attributes;
			if (this.attributes != null) {
				for (final Iterator<GroupAttributeEntity> it = this.attributes.iterator(); it.hasNext(); ) {
					final GroupAttributeEntity entry = it.next();
					if (entry != null && StringUtils.equals(entry.getId(), id)) {
						it.remove();
						break;
					}
				}
			}
		}
	}

	public void addAttribute(final GroupAttributeEntity entity) {
		if (entity != null) {
			if (this.attributes == null) {
				this.attributes = new HashSet<>();
			}
			entity.setGroup(this);
			this.attributes.add(entity);
		}
	}

    public Set<GroupToGroupMembershipXrefEntity> getChildGroups() {
        return childGroups;
    }

    public void setChildGroups(Set<GroupToGroupMembershipXrefEntity> childGroups) {
        this.childGroups = childGroups;
    }

    public Set<GroupAttributeEntity> getAttributes() {
		return attributes;
	}

	public void setAttributes(Set<GroupAttributeEntity> attributes) {
		this.attributes = attributes;
	}

    public Set<GroupToResourceMembershipXrefEntity> getResources() {
		return resources;
	}

	public void setResources(Set<GroupToResourceMembershipXrefEntity> resources) {
		this.resources = resources;
	}
	
	public Set<RoleToGroupMembershipXrefEntity> getRoles() {
		return roles;
	}

	public void setRoles(Set<RoleToGroupMembershipXrefEntity> roles) {
		this.roles = roles;
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
				this.users = new LinkedHashSet<UserToGroupMembershipXrefEntity>();
			}
			UserToGroupMembershipXrefEntity theXref = null;
			for(final UserToGroupMembershipXrefEntity xref : this.users) {
				if(xref.getEntity().getId().equals(getId()) && xref.getMemberEntity().getId().equals(entity.getId())) {
					theXref = xref;
					break;
				}
			}
			
			if(theXref == null) {
				theXref = new UserToGroupMembershipXrefEntity();
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

	public GroupToResourceMembershipXrefEntity getResource(final String resourceId) {
		final Optional<GroupToResourceMembershipXrefEntity> xref = 
    			this.getResources()
    				.stream()
    				.filter(e -> resourceId.equals(e.getMemberEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
	}
	
	public void addResource(final ResourceEntity entity, final Collection<AccessRightEntity> rights, final Date startDate, final Date endDate) {
		if(entity != null) {
			if(this.resources == null) {
				this.resources = new LinkedHashSet<GroupToResourceMembershipXrefEntity>();
			}
			GroupToResourceMembershipXrefEntity theXref = null;
			for(final GroupToResourceMembershipXrefEntity xref : this.resources) {
				if(xref.getEntity().getId().equals(getId()) && xref.getMemberEntity().getId().equals(entity.getId())) {
					theXref = xref;
					break;
				}
			}
			
			if(theXref == null) {
				theXref = new GroupToResourceMembershipXrefEntity();
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
	
	public UserToGroupMembershipXrefEntity getUser(final String userId) {
		final Optional<UserToGroupMembershipXrefEntity> xref = 
    			this.getUsers()
    				.stream()
    				.filter(e -> userId.equals(e.getMemberEntity().getId()))
    				.findFirst();
    	return xref.isPresent() ? xref.get() : null;
	}

    public Set<UserToGroupMembershipXrefEntity> getUsers() {
        return users;
    }

    public void setUsers(Set<UserToGroupMembershipXrefEntity> users) {
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

    public MetadataTypeEntity getClassification() {
        return classification;
    }

    public void setClassification(MetadataTypeEntity classification) {
        this.classification = classification;
    }

    public MetadataTypeEntity getAdGroupType() {
        return adGroupType;
    }

    public void setAdGroupType(MetadataTypeEntity adGroupType) {
        this.adGroupType = adGroupType;
    }

    public MetadataTypeEntity getAdGroupScope() {
        return adGroupScope;
    }

    public void setAdGroupScope(MetadataTypeEntity adGroupScope) {
        this.adGroupScope = adGroupScope;
    }

    public MetadataTypeEntity getRisk() {
        return risk;
    }

    public void setRisk(MetadataTypeEntity risk) {
        this.risk = risk;
    }

    public Integer getMaxUserNumber() {
        return maxUserNumber;
    }

    public void setMaxUserNumber(Integer maxUserNumber) {
        this.maxUserNumber = maxUserNumber;
    }

    public Long getMembershipDuration() {
        return membershipDuration;
    }

    public void setMembershipDuration(Long membershipDuration) {
        this.membershipDuration = membershipDuration;
    }

    public Set<GroupToOrgMembershipXrefEntity> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(Set<GroupToOrgMembershipXrefEntity> organizations) {
		this.organizations = organizations;
	}
	
	public void addOrganization(final OrganizationEntity entity, final Collection<AccessRightEntity> rights, final Date startDate, final Date endDate) {
		if(entity != null) {
			if(this.organizations == null) {
				this.organizations = new LinkedHashSet<GroupToOrgMembershipXrefEntity>();
			}
			GroupToOrgMembershipXrefEntity theXref = null;
			for(final GroupToOrgMembershipXrefEntity xref : this.organizations) {
				if(xref.getEntity().getId().equals(entity.getId()) && xref.getMemberEntity().getId().equals(getId())) {
					theXref = xref;
					break;
				}
			}
			
			if(theXref == null) {
				theXref = new GroupToOrgMembershipXrefEntity();
				theXref.setEntity(entity);
				theXref.setMemberEntity(this);
			}
			if(rights != null) {
				theXref.setRights(new HashSet<AccessRightEntity>(rights));
			}
			theXref.setStartDate(startDate);
			theXref.setEndDate(endDate);
			this.organizations.add(theXref);
		}
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupEntity)) return false;
        if (!super.equals(o)) return false;

        GroupEntity that = (GroupEntity) o;

        if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null) return false;
        if (createdBy != null ? !createdBy.equals(that.createdBy) : that.createdBy != null) return false;
        if (managedSystem != null ? !managedSystem.equals(that.managedSystem) : that.managedSystem != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;

        if (classification != null ? !classification.equals(that.classification) : that.classification != null) return false;
        if (adGroupType != null ? !adGroupType.equals(that.adGroupType) : that.adGroupType != null) return false;
        if (adGroupScope != null ? !adGroupScope.equals(that.adGroupScope) : that.adGroupScope != null) return false;
        return !(risk != null ? !risk.equals(that.risk) : that.risk != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (managedSystem != null ? managedSystem.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);

        result = 31 * result + (classification != null ? classification.hashCode() : 0);
        result = 31 * result + (adGroupType != null ? adGroupType.hashCode() : 0);
        result = 31 * result + (adGroupScope != null ? adGroupScope.hashCode() : 0);
        result = 31 * result + (risk != null ? risk.hashCode() : 0);
        return result;
    }

    @Override
	public String toString() {
		return String
				.format("GroupEntity [id=%s, name=%s, createDate=%s, createdBy=%s, managedSystem=%s, description=%s, status=%s, lastUpdate=%s, lastUpdatedBy=%s, classification=%s, adGroupType=%s, adGroupScope=%s, risk=%s]",
						id, name, createDate, createdBy, managedSystem,
						description, status, lastUpdate, lastUpdatedBy,
                        classification, adGroupType, adGroupScope, risk);
	}

    
}
