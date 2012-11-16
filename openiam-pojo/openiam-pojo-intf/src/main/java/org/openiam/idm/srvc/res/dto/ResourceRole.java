package org.openiam.idm.srvc.res.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.res.domain.ResourceRoleEntity;

// Generated Mar 8, 2009 12:54:32 PM by Hibernate Tools 3.2.2.GA

/**
 * ResourceRole associates a role to a resource. This association is used to determine if a role has access
 * to a resource.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceRole", propOrder = {
		"id",
        "startDate",
        "endDate"
})
@DozerDTOCorrespondence(ResourceRoleEntity.class)
public class ResourceRole implements java.io.Serializable {

	private ResourceRoleId id;
	
    @XmlSchemaType(name = "dateTime")
    protected Date startDate;
    @XmlSchemaType(name = "dateTime")
    protected Date endDate;

    public ResourceRole() {
    }

    /*
    public ResourceRole(ResourceRoleEntity resourceRoleEntity) {
        this.id = new ResourceRoleId(resourceRoleEntity.getId());
        this.startDate = resourceRoleEntity.getStartDate();
        this.endDate = resourceRoleEntity.getEndDate();
    }
    */

	public ResourceRoleId getId() {
		return id;
	}

	public void setId(ResourceRoleId id) {
		this.id = id;
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		ResourceRole other = (ResourceRole) obj;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("ResourceRole [id=%s, startDate=%s, endDate=%s]",
						id, startDate, endDate);
	}

	
}
