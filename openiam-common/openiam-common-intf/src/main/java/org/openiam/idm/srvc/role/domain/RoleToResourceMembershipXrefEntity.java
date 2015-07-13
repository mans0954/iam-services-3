package org.openiam.idm.srvc.role.domain;

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
import org.openiam.elasticsearch.annotation.ElasticsearchField;
import org.openiam.elasticsearch.annotation.ElasticsearchFieldBridge;
import org.openiam.elasticsearch.annotation.ElasticsearchIndex;
import org.openiam.elasticsearch.annotation.ElasticsearchMapping;
import org.openiam.elasticsearch.bridge.ResourceBridge;
import org.openiam.elasticsearch.bridge.RoleBridge;
import org.openiam.elasticsearch.constants.ESIndexName;
import org.openiam.elasticsearch.constants.ESIndexType;
import org.openiam.elasticsearch.constants.ElasticsearchStore;
import org.openiam.elasticsearch.constants.Index;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.membership.domain.AbstractMembershipXrefEntity;
import org.openiam.idm.srvc.membership.domain.ResourceAwareMembershipXref;
import org.openiam.idm.srvc.membership.domain.RoleAwareMembershipXref;
import org.openiam.idm.srvc.res.domain.ResourceEntity;

@Entity
@Table(name = "RESOURCE_ROLE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "MEMBERSHIP_ID"))
@ElasticsearchIndex(indexName = ESIndexName.ROLE_TO_RES_XREF)
@ElasticsearchMapping(typeName = ESIndexType.ROLE_TO_RES_XREF)
public class RoleToResourceMembershipXrefEntity extends AbstractMembershipXrefEntity<RoleEntity, ResourceEntity> implements RoleAwareMembershipXref, ResourceAwareMembershipXref {

	@ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	 @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID", insertable = true, updatable = false, nullable=false)
	@ElasticsearchField(name = "entityId", bridge=@ElasticsearchFieldBridge(impl = RoleBridge.class), store = ElasticsearchStore.Yes, index = Index.Not_Analyzed)
	 private RoleEntity entity;
	    
	 @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	 @JoinColumn(name = "RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = true, updatable = false, nullable=false)
	 @ElasticsearchField(name = "memberEntityId", bridge=@ElasticsearchFieldBridge(impl = ResourceBridge.class), store = ElasticsearchStore.Yes, index = Index.Not_Analyzed)
	 private ResourceEntity memberEntity;
	    
	 /* this is eager.  If you're loading the XREF - it's to get the rights */
	 @ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.EAGER)
	 @JoinTable(name = "RES_ROLE_MEMBERSHIP_RIGHTS",
	 			joinColumns = {@JoinColumn(name = "MEMBERSHIP_ID")},
	            inverseJoinColumns = {@JoinColumn(name = "ACCESS_RIGHT_ID")})
	 @Fetch(FetchMode.SUBSELECT)
	 private Set<AccessRightEntity> rights;

	public RoleEntity getEntity() {
		return entity;
	}

	public void setEntity(RoleEntity entity) {
		this.entity = entity;
	}

	public ResourceEntity getMemberEntity() {
		return memberEntity;
	}

	public void setMemberEntity(ResourceEntity memberEntity) {
		this.memberEntity = memberEntity;
	}
	
	@Override
	@Transient
	public ResourceEntity getResource() {
		return memberEntity;
	}

	@Override
	@Transient
	public RoleEntity getRole() {
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
		RoleToResourceMembershipXrefEntity other = (RoleToResourceMembershipXrefEntity) obj;
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
