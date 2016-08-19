package org.openiam.am.srvc.dto.jdbc;

import java.util.Date;


public class InternalAuthorizationToken {
	
	private InternalAuthorizationToken() {}
	
	public InternalAuthorizationToken(final String rightId, final Date startDate, final Date endDate) {
		this.rightId = rightId;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	private String rightId;
	private Date startDate;
	private Date endDate;
	public String getRightId() {
		return rightId;
	}
	public void setRightId(String rightId) {
		this.rightId = rightId;
	}
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
		int result = 1;
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((rightId == null) ? 0 : rightId.hashCode());
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InternalAuthorizationToken other = (InternalAuthorizationToken) obj;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (rightId == null) {
			if (other.rightId != null)
				return false;
		} else if (!rightId.equals(other.rightId))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}
	
	
}
