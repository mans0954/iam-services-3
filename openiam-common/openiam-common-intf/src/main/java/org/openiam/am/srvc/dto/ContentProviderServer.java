package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.domain.ContentProviderServerEntity;
import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContentProviderServer", propOrder = {
        "contentProviderId"
})
@DozerDTOCorrespondence(ContentProviderServerEntity.class)
public class ContentProviderServer extends AbstractServer {

	private String contentProviderId;
	
	public String getContentProviderId() {
		return contentProviderId;
	}
	public void setContentProviderId(String contentProviderId) {
		this.contentProviderId = contentProviderId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((contentProviderId == null) ? 0 : contentProviderId
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
		ContentProviderServer other = (ContentProviderServer) obj;
		if (contentProviderId == null) {
			if (other.contentProviderId != null)
				return false;
		} else if (!contentProviderId.equals(other.contentProviderId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "ContentProviderServer [contentProviderId=" + contentProviderId
				+ ", getServerURL()=" + getServerURL() + ", getId()=" + getId()
				+ "]";
	}
	
	
}
