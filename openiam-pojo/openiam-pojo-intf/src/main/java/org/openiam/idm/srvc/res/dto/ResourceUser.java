package org.openiam.idm.srvc.res.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.res.domain.ResourceUserEntity;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceUser", propOrder = {
        "resourceUserId",
        "resourceId",
        "userId",
        "startDate",
        "endDate"
})
@DozerDTOCorrespondence(ResourceUserEntity.class)
public class ResourceUser implements java.io.Serializable {

	private String resourceUserId;
	private String resourceId;
	private String userId;
	private Date startDate;
	private Date endDate;
	
	public ResourceUser() {
		
	}

	public String getResourceUserId() {
		return resourceUserId;
	}

	public void setResourceUserId(String resourceUserId) {
		this.resourceUserId = resourceUserId;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
		result = prime * result
				+ ((resourceUserId == null) ? 0 : resourceUserId.hashCode());
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		ResourceUser other = (ResourceUser) obj;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		if (resourceUserId == null) {
			if (other.resourceUserId != null)
				return false;
		} else if (!resourceUserId.equals(other.resourceUserId))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("ResourceUser [resourceUserId=%s, resourceId=%s, userId=%s, startDate=%s, endDate=%s]",
						resourceUserId, resourceId, userId, startDate, endDate);
	}
	
	
}
