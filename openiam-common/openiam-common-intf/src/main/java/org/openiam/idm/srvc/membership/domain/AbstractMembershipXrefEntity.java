package org.openiam.idm.srvc.membership.domain;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.openiam.base.domain.KeyEntity;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;

@MappedSuperclass
public abstract class AbstractMembershipXrefEntity<Parent extends KeyEntity, Child extends KeyEntity> extends KeyEntity {
	
	public abstract Set<AccessRightEntity> getRights();
	public abstract Parent getEntity();
	public abstract Child getMemberEntity();
	public abstract Class<Parent> getEntityClass();
	public abstract Class<Child> getMemberClass();
	
	@Column(name = "START_DATE", length = 19)
    private Date startDate;
	
	@Column(name = "END_DATE", length = 19)
    private Date endDate;

	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
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
		AbstractMembershipXrefEntity other = (AbstractMembershipXrefEntity) obj;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}
	
	
}
