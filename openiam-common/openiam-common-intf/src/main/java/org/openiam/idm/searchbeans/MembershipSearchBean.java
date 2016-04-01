package org.openiam.idm.searchbeans;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.membership.dto.AbstractMembershipXref;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MembershipSearchBean", propOrder = {
        "entityId",
        "memberEntityId",
        "rightIds"
})
public class MembershipSearchBean extends AbstractSearchBean<AbstractMembershipXref, String> implements SearchBean {

	private String entityId;
	private String memberEntityId;
	private Set<String> rightIds;
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
	public Set<String> getRightIds() {
		return rightIds;
	}
	public void setRightIds(Set<String> rightIds) {
		this.rightIds = rightIds;
	}

	@Override
	public String getCacheUniqueBeanKey() {
		return new StringBuilder()
				.append(getKey() != null ? getKey() : "")
				.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((entityId == null) ? 0 : entityId.hashCode());
		result = prime * result
				+ ((memberEntityId == null) ? 0 : memberEntityId.hashCode());
		result = prime * result
				+ ((rightIds == null) ? 0 : rightIds.hashCode());
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
		MembershipSearchBean other = (MembershipSearchBean) obj;
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
		if (rightIds == null) {
			if (other.rightIds != null)
				return false;
		} else if (!rightIds.equals(other.rightIds))
			return false;
		return true;
	}
	
	
}
