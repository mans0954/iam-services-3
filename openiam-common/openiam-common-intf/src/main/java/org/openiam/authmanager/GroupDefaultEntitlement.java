package org.openiam.authmanager;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.idm.srvc.grp.domain.GroupEntity;

@Entity
@Table(name = "DEFAULT_GRP_ENTITLEMENTS")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "GroupDefaultEntitlement")
public class GroupDefaultEntitlement extends AbstractDefaultEntitlementEntity<GroupEntity> {
	
	@ManyToOne(fetch = FetchType.EAGER,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="GRP_ID", referencedColumnName = "GRP_ID", insertable = true, updatable = false, nullable=false)
	private GroupEntity entity;

	@Override
	public GroupEntity getEntity() {
		return entity;
	}

	@Override
	public void setEntity(final GroupEntity entity) {
		this.entity = entity;
	}

}
