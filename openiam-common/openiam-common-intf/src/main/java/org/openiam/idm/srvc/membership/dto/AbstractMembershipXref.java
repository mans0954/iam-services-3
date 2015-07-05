package org.openiam.idm.srvc.membership.dto;

import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.KeyDTO;
import org.openiam.idm.srvc.access.dto.AccessRight;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractMembershipXref", propOrder = {
	"entityId",
	"memberEntityId",
	"rights",
	"operation"
})
public class AbstractMembershipXref extends KeyDTO {

	private String entityId;
	private String memberEntityId;
	private Set<AccessRight> rights;
	private AttributeOperationEnum operation = AttributeOperationEnum.NO_CHANGE;
	
	
	
	public AttributeOperationEnum getOperation() {
		return operation;
	}
	public void setOperation(AttributeOperationEnum operation) {
		this.operation = operation;
	}
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public String getMemberEntityId() {
		return memberEntityId;
	}
	public void setMemberEntityId(String memberEntityId) {
		this.memberEntityId = memberEntityId;
	}
	public Set<AccessRight> getRights() {
		return rights;
	}
	public void setRights(final Set<AccessRight> rights) {
		this.rights = rights;
	}
	public Set<String> getAccessRightIds() {
		return (rights != null) ? rights.stream().map(e -> e.getId()).collect(Collectors.toSet()) : null;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((entityId == null) ? 0 : entityId.hashCode());
		result = prime * result
				+ ((memberEntityId == null) ? 0 : memberEntityId.hashCode());
		result = prime * result + ((rights == null) ? 0 : rights.hashCode());
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
		AbstractMembershipXref other = (AbstractMembershipXref) obj;
		if (entityId == null) {
			if (other.entityId != null)
				return false;
		} else if (!entityId.equals(other.entityId))
			return false;
		if (memberEntityId == null) {
			if (other.memberEntityId != null)
				return false;
		} else if (!memberEntityId.equals(other.memberEntityId))
			return false;
		if (rights == null) {
			if (other.rights != null)
				return false;
		} else if (!rights.equals(other.rights))
			return false;
		return true;
	}
	
	
}
