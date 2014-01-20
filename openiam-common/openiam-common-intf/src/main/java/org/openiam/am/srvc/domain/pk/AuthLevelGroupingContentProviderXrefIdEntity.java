package org.openiam.am.srvc.domain.pk;

import java.io.Serializable;

import javax.persistence.Column;

import org.openiam.am.srvc.dto.AuthLevelGroupingContentProviderXrefId;
import org.openiam.dozer.DozerDTOCorrespondence;

@DozerDTOCorrespondence(AuthLevelGroupingContentProviderXrefId.class)
public class AuthLevelGroupingContentProviderXrefIdEntity implements Serializable {

	@Column(name="AUTH_LEVEL_GROUPING_ID", length = 32, nullable = false)
    private String groupingId;
	
    @Column(name="CONTENT_PROVIDER_ID", length = 32, nullable = false)
    private String contentProviderId;

	public String getGroupingId() {
		return groupingId;
	}
	
	public AuthLevelGroupingContentProviderXrefIdEntity() {
		
	}
	
	public AuthLevelGroupingContentProviderXrefIdEntity(final String groupingId, final String contentProviderId) {
		this.groupingId = groupingId;
		this.contentProviderId = contentProviderId;
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
		AuthLevelGroupingContentProviderXrefIdEntity other = (AuthLevelGroupingContentProviderXrefIdEntity) obj;
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
				.format("AuthLevelGroupingContentProviderXrefIdEntity [groupingId=%s, contentProviderId=%s]",
						groupingId, contentProviderId);
	}
    
    
}
