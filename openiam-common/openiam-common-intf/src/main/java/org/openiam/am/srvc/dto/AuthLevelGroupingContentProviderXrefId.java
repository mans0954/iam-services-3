package org.openiam.am.srvc.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.am.srvc.domain.pk.AuthLevelGroupingContentProviderXrefIdEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthLevelGroupingContentProviderXrefId", propOrder = {
        "groupingId",
        "contentProviderId"
})
@DozerDTOCorrespondence(AuthLevelGroupingContentProviderXrefIdEntity.class)
public class AuthLevelGroupingContentProviderXrefId implements Serializable {

	private String groupingId;
	private String contentProviderId;
	
	public AuthLevelGroupingContentProviderXrefId() {
		
	}
	
	public String getGroupingId() {
		return groupingId;
	}
	public void setGroupingId(String groupingId) {
		this.groupingId = groupingId;
	}
	public String getContentProviderId() {
		return contentProviderId;
	}
	public void setContentProviderId(String contentProviderId) {
		this.contentProviderId = contentProviderId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((contentProviderId == null) ? 0 : contentProviderId
						.hashCode());
		result = prime * result
				+ ((groupingId == null) ? 0 : groupingId.hashCode());
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
		AuthLevelGroupingContentProviderXrefId other = (AuthLevelGroupingContentProviderXrefId) obj;
		if (contentProviderId == null) {
			if (other.contentProviderId != null)
				return false;
		} else if (!contentProviderId.equals(other.contentProviderId))
			return false;
		if (groupingId == null) {
			if (other.groupingId != null)
				return false;
		} else if (!groupingId.equals(other.groupingId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return String
				.format("AuthLevelGroupingContentProviderXrefId [groupingId=%s, contentProviderId=%s]",
						groupingId, contentProviderId);
	}
	
	
}
