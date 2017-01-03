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
import org.openiam.elasticsearch.annotation.ElasticsearchFieldBridge;
import org.openiam.elasticsearch.bridge.GroupBridge;
import org.openiam.elasticsearch.bridge.RoleBridge;
import org.openiam.elasticsearch.constants.ESIndexName;
import org.openiam.elasticsearch.constants.ESIndexType;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.membership.domain.AbstractMembershipXrefEntity;
import org.openiam.idm.srvc.membership.domain.GroupAwareMembershipXref;
import org.openiam.idm.srvc.membership.domain.RoleAwareMembershipXref;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Entity
@Table(name = "GRP_ROLE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "MEMBERSHIP_ID"))
@Document(indexName = ESIndexName.ROLE_TO_GRP_XREF, type= ESIndexType.ROLE_TO_GRP_XREF)
public class RoleToGroupMembershipXrefEntity extends AbstractMembershipXrefEntity<RoleEntity, GroupEntity> implements GroupAwareMembershipXref, RoleAwareMembershipXref {

	@ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID", insertable = true, updatable = false, nullable=false)
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= false)
    @ElasticsearchFieldBridge(impl = RoleBridge.class)
	private RoleEntity entity;
	    
	@ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "GRP_ID", referencedColumnName = "GRP_ID", insertable = true, updatable = false, nullable=false)
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= false)
    @ElasticsearchFieldBridge(impl = GroupBridge.class)
	private GroupEntity memberEntity;
	    
	 /* this is eager.  If you're loading the XREF - it's to get the rights */
	@ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.EAGER)
	@JoinTable(name = "GRP_ROLE_MEMBERSHIP_RIGHTS",
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
	@Transient
	public Class<RoleEntity> getEntityClass() {
		return RoleEntity.class;
	}

	@Override
	@Transient
	public Class<GroupEntity> getMemberClass() {
		return GroupEntity.class;
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
		RoleToGroupMembershipXrefEntity other = (RoleToGroupMembershipXrefEntity) obj;
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
