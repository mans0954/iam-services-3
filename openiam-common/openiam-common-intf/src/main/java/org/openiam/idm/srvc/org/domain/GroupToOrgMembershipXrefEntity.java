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
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.elasticsearch.annotation.ElasticsearchField;
import org.openiam.elasticsearch.annotation.ElasticsearchFieldBridge;
import org.openiam.elasticsearch.annotation.ElasticsearchIndex;
import org.openiam.elasticsearch.annotation.ElasticsearchMapping;
import org.openiam.elasticsearch.bridge.GroupBridge;
import org.openiam.elasticsearch.bridge.OrganizationBridge;
import org.openiam.elasticsearch.constants.ESIndexName;
import org.openiam.elasticsearch.constants.ESIndexType;
import org.openiam.elasticsearch.constants.ElasticsearchStore;
import org.openiam.elasticsearch.constants.Index;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.membership.domain.AbstractMembershipXrefEntity;
import org.openiam.idm.srvc.membership.domain.GroupAwareMembershipXref;
import org.openiam.idm.srvc.membership.domain.OrganizationAwareMembershipXref;
import org.openiam.idm.srvc.org.dto.GroupToOrgMembershipXref;

@Entity
@Table(name = "GROUP_ORGANIZATION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "MEMBERSHIP_ID"))
@DozerDTOCorrespondence(GroupToOrgMembershipXref.class)
@ElasticsearchIndex(indexName = ESIndexName.GRP_TO_ORG_XREF)
@ElasticsearchMapping(typeName = ESIndexType.GRP_TO_ORG_XREF)
public class GroupToOrgMembershipXrefEntity extends AbstractMembershipXrefEntity<OrganizationEntity, GroupEntity> implements GroupAwareMembershipXref, OrganizationAwareMembershipXref {

	@ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "COMPANY_ID", referencedColumnName = "COMPANY_ID", insertable = true, updatable = false, nullable=false)
	@ElasticsearchField(name = "entityId", bridge=@ElasticsearchFieldBridge(impl = OrganizationBridge.class), store = ElasticsearchStore.Yes, index = Index.Not_Analyzed)
    private OrganizationEntity entity;
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "GRP_ID", referencedColumnName = "GRP_ID", insertable = true, updatable = false, nullable=false)
    @ElasticsearchField(name = "memberEntityId", bridge=@ElasticsearchFieldBridge(impl = GroupBridge.class), store = ElasticsearchStore.Yes, index = Index.Not_Analyzed)
    private GroupEntity memberEntity;

    /* this is eager.  If you're loading the XREF - it's to get the rights */
    @ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.EAGER)
    @JoinTable(name = "GRP_ORG_MEMBERSHIP_RIGHTS",
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

	public GroupEntity getMemberEntity() {
		return memberEntity;
	}

	public void setMemberEntity(GroupEntity memberEntity) {
		this.memberEntity = memberEntity;
	}
	
	@Override
	@Transient
	public GroupEntity getGroup() {
		return memberEntity;
	}

	@Override
	@Transient
	public OrganizationEntity getOrganization() {
		return entity;
	}	

	public Set<AccessRightEntity> getRights() {
		return rights;
	}

	public void setRights(Set<AccessRightEntity> rights) {
		this.rights = rights;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
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
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupToOrgMembershipXrefEntity other = (GroupToOrgMembershipXrefEntity) obj;
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