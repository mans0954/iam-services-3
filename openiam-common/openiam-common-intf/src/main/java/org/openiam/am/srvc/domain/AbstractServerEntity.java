package org.openiam.am.srvc.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.openiam.base.domain.KeyEntity;

@MappedSuperclass
public abstract class AbstractServerEntity extends KeyEntity {

	@Column(name = "SERVER_URL", length = 100, nullable = false)
	private String serverURL;

	public String getServerURL() {
		return serverURL;
	}

	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((serverURL == null) ? 0 : serverURL.hashCode());
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
		AbstractServerEntity other = (AbstractServerEntity) obj;
		if (serverURL == null) {
			if (other.serverURL != null)
				return false;
		} else if (!serverURL.equals(other.serverURL))
			return false;
		return true;
	}
	
	
}
