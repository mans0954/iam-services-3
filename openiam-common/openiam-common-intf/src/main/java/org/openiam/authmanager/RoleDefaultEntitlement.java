package org.openiam.authmanager;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.idm.srvc.role.domain.RoleEntity;

@Entity
@Table(name = "DEFAULT_ROLE_ENTITLEMENTS")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "RoleDefaultEntitlement")
public class RoleDefaultEntitlement extends AbstractDefaultEntitlementEntity<RoleEntity> {

	@ManyToOne(fetch = FetchType.EAGER,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="ROLE_ID", referencedColumnName = "ROLE_ID", insertable = true, updatable = false, nullable=false)
	private RoleEntity entity;

	@Override
	public RoleEntity getEntity() {
		return entity;
	}

	@Override
	public void setEntity(final RoleEntity entity) {
		this.entity = entity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
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
		RoleDefaultEntitlement other = (RoleDefaultEntitlement) obj;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		return true;
	}
	
	
}
