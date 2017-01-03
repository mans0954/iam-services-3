package org.openiam.idm.srvc.org.domain;

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
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.openiam.elasticsearch.annotation.ElasticsearchFieldBridge;
import org.openiam.elasticsearch.bridge.OrganizationBridge;
import org.openiam.elasticsearch.constants.ESIndexName;
import org.openiam.elasticsearch.constants.ESIndexType;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.membership.domain.AbstractMembershipXrefEntity;
import org.openiam.idm.srvc.membership.domain.OrganizationAwareMembershipXref;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Entity
@Table(name = "COMPANY_TO_COMPANY_MEMBERSHIP")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "MEMBERSHIP_ID"))
@Document(indexName = ESIndexName.ORG_TO_ORG_XREF, type= ESIndexType.ORG_TO_ORG_XREF)
public class OrgToOrgMembershipXrefEntity extends AbstractMembershipXrefEntity<OrganizationEntity, OrganizationEntity> implements OrganizationAwareMembershipXref {
    
	@ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "COMPANY_ID", referencedColumnName = "COMPANY_ID", insertable = true, updatable = false, nullable=false)
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= false)
    @ElasticsearchFieldBridge(impl = OrganizationBridge.class)
    private OrganizationEntity entity;
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "MEMBER_COMPANY_ID", referencedColumnName = "COMPANY_ID", insertable = true, updatable = false, nullable=false)

    @Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= false)
    @ElasticsearchFieldBridge(impl = OrganizationBridge.class)
    private OrganizationEntity memberEntity;

    /* this is eager.  If you're loading the XREF - it's to get the rights */
    @ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.EAGER)
    @JoinTable(name = "ORG_TO_ORG_MEMBERSHIP_RIGHTS",
            joinColumns = {@JoinColumn(name = "MEMBERSHIP_ID")},
            inverseJoinColumns = {@JoinColumn(name = "ACCESS_RIGHT_ID")})
    @Fetch(FetchMode.SUBSELECT)
    private Set<AccessRightEntity> rights;

	public OrganizationEntity getEntity() {
		return entity;
	}

	public void setEntity(OrganizationEntity entity) {
		this.entity = entity;
	}

	public OrganizationEntity getMemberEntity() {
		return memberEntity;
	}

	public void setMemberEntity(OrganizationEntity memberEntity) {
		this.memberEntity = memberEntity;
	}

	public Set<AccessRightEntity> getRights() {
		return rights;
	}

	public void setRights(Set<AccessRightEntity> rights) {
		this.rights = rights;
	}

	@Override
	@Transient
	public OrganizationEntity getOrganization() {
		return entity;
	}
	
	@Override
	@Transient
	public Class<OrganizationEntity> getEntityClass() {
		return OrganizationEntity.class;
	}

	@Override
	@Transient
	public Class<OrganizationEntity> getMemberClass() {
		return OrganizationEntity.class;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		result = prime * result
				+ ((memberEntity == null) ? 0 : memberEntity.hashCode());
		result = prime * result + ((rights == null) ? 0 : rights.hashCode());
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
		OrgToOrgMembershipXrefEntity other = (OrgToOrgMembershipXrefEntity) obj;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		if (memberEntity == null) {
			if (other.memberEntity != null)
				return false;
		} else if (!memberEntity.equals(other.memberEntity))
			return false;
		if (rights == null) {
			if (other.rights != null)
				return false;
		} else if (!rights.equals(other.rights))
			return false;
		return true;
	}
}
