package org.openiam.authmanager;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;

@Entity
@Table(name = "DEFAULT_ORG_ENTITLEMENTS")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "OrganizationDefaultEntitlement")
public class OrganizationDefaultEntitlement extends AbstractDefaultEntitlementEntity<OrganizationEntity> {

	@ManyToOne(fetch = FetchType.EAGER,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="COMPANY_ID", referencedColumnName = "COMPANY_ID", insertable = true, updatable = false, nullable=false)
	private OrganizationEntity entity;

	@Override
	public OrganizationEntity getEntity() {
		return entity;
	}

	@Override
	public void setEntity(OrganizationEntity entity) {
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
		OrganizationDefaultEntitlement other = (OrganizationDefaultEntitlement) obj;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		return true;
	}
	
	
	
}
