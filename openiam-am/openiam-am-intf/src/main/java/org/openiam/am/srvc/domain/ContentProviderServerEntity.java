package org.openiam.am.srvc.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "CONTENT_PROVIDER_SERVER")
public class ContentProviderServerEntity {

	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "CONTENT_PROVIDER_SERVER_ID", length = 32, nullable = false)
	private String id;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="CONTENT_PROVIDER_ID", referencedColumnName = "CONTENT_PROVIDER_ID", insertable = false, updatable = false)
	private ContentProviderEntity contentProvider;
	
	@Column(name = "SERVER_URL", length = 100, nullable = false)
	private String serverURL;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ContentProviderEntity getContentProvider() {
		return contentProvider;
	}

	public void setContentProvider(ContentProviderEntity contentProvider) {
		this.contentProvider = contentProvider;
	}

	public String getServerURL() {
		return serverURL;
	}

	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((contentProvider == null) ? 0 : contentProvider.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((serverURL == null) ? 0 : serverURL.hashCode());
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
		ContentProviderServerEntity other = (ContentProviderServerEntity) obj;
		if (contentProvider == null) {
			if (other.contentProvider != null)
				return false;
		} else if (!contentProvider.equals(other.contentProvider))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (serverURL == null) {
			if (other.serverURL != null)
				return false;
		} else if (!serverURL.equals(other.serverURL))
			return false;
		return true;
	}
	
	
}
