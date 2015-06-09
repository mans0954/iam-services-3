package org.openiam.idm.srvc.user.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.membership.dto.AbstractMembershipXref;
import org.openiam.idm.srvc.user.domain.UserToOrganizationMembershipXrefEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserToOrganizationMembershipXref", propOrder = {
	"organizationTypeId",
	"name"
})
@DozerDTOCorrespondence(UserToOrganizationMembershipXrefEntity.class)
public class UserToOrganizationMembershipXref extends AbstractMembershipXref {

	private String organizationTypeId;
	private String name;

	public String getOrganizationTypeId() {
		return organizationTypeId;
	}

	public void setOrganizationTypeId(String organizationTypeId) {
		this.organizationTypeId = organizationTypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime
				* result
				+ ((organizationTypeId == null) ? 0 : organizationTypeId
						.hashCode());
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
		UserToOrganizationMembershipXref other = (UserToOrganizationMembershipXref) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (organizationTypeId == null) {
			if (other.organizationTypeId != null)
				return false;
		} else if (!organizationTypeId.equals(other.organizationTypeId))
			return false;
		return true;
	}

	
}
