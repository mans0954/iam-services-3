package org.openiam.am.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.am.srvc.dto.ContentProviderServer;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.persistence.*;

@Entity
@Table(name = "CONTENT_PROVIDER_SERVER")
@DozerDTOCorrespondence(ContentProviderServer.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "CONTENT_PROVIDER_SERVER_ID"))
})
public class ContentProviderServerEntity extends AbstractServerEntity {
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="CONTENT_PROVIDER_ID", referencedColumnName = "CONTENT_PROVIDER_ID")
	private ContentProviderEntity contentProvider;

	public ContentProviderEntity getContentProvider() {
		return contentProvider;
	}

	public void setContentProvider(ContentProviderEntity contentProvider) {
		this.contentProvider = contentProvider;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((contentProvider == null) ? 0 : contentProvider.hashCode());
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
		ContentProviderServerEntity other = (ContentProviderServerEntity) obj;
		if (contentProvider == null) {
			if (other.contentProvider != null)
				return false;
		} else if (!contentProvider.equals(other.contentProvider))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ContentProviderServerEntity [contentProvider="
				+ contentProvider + ", getServerURL()=" + getServerURL()
				+ ", getId()=" + getId() + "]";
	}

	
}
