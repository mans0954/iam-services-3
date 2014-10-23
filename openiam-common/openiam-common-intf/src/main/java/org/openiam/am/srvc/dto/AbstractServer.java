package org.openiam.am.srvc.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.KeyDTO;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractServer", propOrder = {
        "serverURL"
})
public abstract class AbstractServer extends KeyDTO {

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
		AbstractServer other = (AbstractServer) obj;
		if (serverURL == null) {
			if (other.serverURL != null)
				return false;
		} else if (!serverURL.equals(other.serverURL))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AbstractServer [serverURL=" + serverURL + ", getId()="
				+ getId() + "]";
	}
	
	
}
