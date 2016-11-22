package org.openiam.idm.srvc.user.domain;

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
import org.openiam.elasticsearch.annotation.ElasticsearchFieldBridge;
import org.openiam.elasticsearch.bridge.ResourceBridge;
import org.openiam.elasticsearch.bridge.UserBrigde;
import org.openiam.elasticsearch.constants.ESIndexName;
import org.openiam.elasticsearch.constants.ESIndexType;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.membership.domain.AbstractMembershipXrefEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.user.dto.UserToResourceMembershipXref;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Entity
@Table(name = "RESOURCE_USER")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "MEMBERSHIP_ID"))
@DozerDTOCorrespondence(UserToResourceMembershipXref.class)
@Document(indexName = ESIndexName.USER_TO_RES_XREF, type= ESIndexType.USER_TO_RES_XREF)
public class UserToResourceMembershipXrefEntity extends AbstractMembershipXrefEntity<ResourceEntity, UserEntity> {

	@ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = true, updatable = false, nullable=false)
	//@ElasticsearchField(name = "entityId", bridge=@ElasticsearchFieldBridge(impl = ResourceBridge.class), store = ElasticsearchStore.Yes, index = Index.Not_Analyzed)
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
    @ElasticsearchFieldBridge(impl = ResourceBridge.class)
	private ResourceEntity entity;
	
	@ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", insertable = true, updatable = false, nullable=false)
	//@ElasticsearchField(name = "memberEntityId", bridge=@ElasticsearchFieldBridge(impl = UserBrigde.class), store = ElasticsearchStore.Yes, index = Index.Not_Analyzed)
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
    @ElasticsearchFieldBridge(impl = UserBrigde.class)
	private UserEntity memberEntity;
	    
	/* this is eager.  If you're loading the XREF - it's to get the rights */
	@ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.EAGER)
	@JoinTable(name = "USER_RES_MEMBERSHIP_RIGHTS",
	 			joinColumns = {@JoinColumn(name = "MEMBERSHIP_ID")},
	            inverseJoinColumns = {@JoinColumn(name = "ACCESS_RIGHT_ID")})
	@Fetch(FetchMode.SUBSELECT)
	private Set<AccessRightEntity> rights;

	public ResourceEntity getEntity() {
		return entity;
	}

	public void setEntity(ResourceEntity entity) {
		this.entity = entity;
	}

	public UserEntity getMemberEntity() {
		return memberEntity;
	}

	public void setMemberEntity(UserEntity memberEntity) {
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
	public Class<ResourceEntity> getEntityClass() {
		return ResourceEntity.class;
	}

	@Override
	@Transient
	public Class<UserEntity> getMemberClass() {
		return UserEntity.class;
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
		UserToResourceMembershipXrefEntity other = (UserToResourceMembershipXrefEntity) obj;
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
