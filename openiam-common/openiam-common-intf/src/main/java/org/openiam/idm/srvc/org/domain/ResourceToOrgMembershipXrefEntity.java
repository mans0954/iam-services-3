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
import org.openiam.elasticsearch.bridge.ResourceBridge;
import org.openiam.elasticsearch.constants.ESIndexName;
import org.openiam.elasticsearch.constants.ESIndexType;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.membership.domain.AbstractMembershipXrefEntity;
import org.openiam.idm.srvc.membership.domain.OrganizationAwareMembershipXref;
import org.openiam.idm.srvc.membership.domain.ResourceAwareMembershipXref;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Entity
@Table(name = "RES_ORG_MEMBERSHIP")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "MEMBERSHIP_ID"))
@Document(indexName = ESIndexName.RES_TO_ORG_XREF, type= ESIndexType.RES_TO_ORG_XREF)
public class ResourceToOrgMembershipXrefEntity extends AbstractMembershipXrefEntity<OrganizationEntity, ResourceEntity> implements OrganizationAwareMembershipXref, ResourceAwareMembershipXref {

	@ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "COMPANY_ID", referencedColumnName = "COMPANY_ID", insertable = true, updatable = false, nullable=false)
	//@ElasticsearchField(name = "entityId", bridge=@ElasticsearchFieldBridge(impl = OrganizationBridge.class), store = ElasticsearchStore.Yes, index = Index.Not_Analyzed)
	
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
    @ElasticsearchFieldBridge(impl = OrganizationBridge.class)
    private OrganizationEntity entity;
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = true, updatable = false, nullable=false)
    //@ElasticsearchField(name = "memberEntityId", bridge=@ElasticsearchFieldBridge(impl = ResourceBridge.class), store = ElasticsearchStore.Yes, index = Index.Not_Analyzed)
    
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
    @ElasticsearchFieldBridge(impl = ResourceBridge.class)
    private ResourceEntity memberEntity;

    /* this is eager.  If you're loading the XREF - it's to get the rights */
    @ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.EAGER)
    @JoinTable(name = "RES_ORG_MEMBERSHIP_RIGHTS",
            joinColumns = {@JoinColumn(name = "MEMBERSHIP_ID")},
            inverseJoinColumns = {@JoinColumn(name = "ACCESS_RIGHT_ID")})
    @Fetch(FetchMode.SUBSELECT)
    private Set<AccessRightEntity> rights;

	@Override
	@Transient
	public ResourceEntity getResource() {
		return memberEntity;
	}

	@Override
	@Transient
	public OrganizationEntity getOrganization() {
		return entity;
	}

	public OrganizationEntity getEntity() {
		return entity;
	}

	public void setEntity(OrganizationEntity entity) {
		this.entity = entity;
	}

	public ResourceEntity getMemberEntity() {
		return memberEntity;
	}

	public void setMemberEntity(ResourceEntity memberEntity) {
		this.memberEntity = memberEntity;
	}

	public Set<AccessRightEntity> getRights() {
		return rights;
	}

	public void setRights(Set<AccessRightEntity> rights) {
		this.rights = rights;
	}

	@Override
	public String toString() {
		return "ResourceToOrgMembershipXrefEntity [entity=" + entity
				+ ", memberEntity=" + memberEntity + ", rights=" + rights + "]";
	}
}
