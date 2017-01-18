package org.openiam.authmanager;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Type;
import org.openiam.base.domain.KeyEntity;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;

@MappedSuperclass
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "DEFAULT_XREF_ID"))
})
public abstract class AbstractDefaultEntitlementEntity<T extends KeyEntity> extends KeyEntity {

	@ManyToOne(fetch = FetchType.EAGER,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="ACCESS_RIGHT_ID", referencedColumnName = "ACCESS_RIGHT_ID", insertable = true, updatable = false, nullable=false)
	private AccessRightEntity right;
	
	@Column(name = "IS_PUBLIC")
    @Type(type = "yes_no")
	private boolean isPublic;
	
	public abstract T getEntity();
	public abstract void setEntity(final T t);
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isPublic ? 1231 : 1237);
		result = prime * result + ((right == null) ? 0 : right.hashCode());
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
		AbstractDefaultEntitlementEntity other = (AbstractDefaultEntitlementEntity) obj;
		if (isPublic != other.isPublic)
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}
	
	
}
