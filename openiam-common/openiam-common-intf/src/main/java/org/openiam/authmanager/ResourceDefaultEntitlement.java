package org.openiam.authmanager;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.idm.srvc.res.domain.ResourceEntity;

@Entity
@Table(name = "DEFAULT_RES_ENTITLEMENTS")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ResourceDefaultEntitlement")
public class ResourceDefaultEntitlement extends AbstractDefaultEntitlementEntity<ResourceEntity> {

	@ManyToOne(fetch = FetchType.EAGER,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="RES_ID", referencedColumnName = "RES_ID", insertable = true, updatable = false, nullable=false)
	private ResourceEntity entity;

	@Override
	public ResourceEntity getEntity() {
		return entity;
	}

	@Override
	public void setEntity(final ResourceEntity entity) {
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
		ResourceDefaultEntitlement other = (ResourceDefaultEntitlement) obj;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		return true;
	}
	
	
}
