package org.openiam.idm.srvc.service.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class RequestFormEntityId implements Serializable {

	@Column(name = "REQUEST_TYPE", length = 20, nullable = false)
    private String requestType;
	
	@Column(name = "SERVICE_ID", length = 20, nullable = false)
    private String serviceId;

    public RequestFormEntityId() {
    }

    public RequestFormEntityId(String requestType, String serviceId) {
        this.requestType = requestType;
        this.serviceId = serviceId;
    }

    public String getRequestType() {
        return this.requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getServiceId() {
        return this.serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((requestType == null) ? 0 : requestType.hashCode());
		result = prime * result
				+ ((serviceId == null) ? 0 : serviceId.hashCode());
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
		RequestFormEntityId other = (RequestFormEntityId) obj;
		if (requestType == null) {
			if (other.requestType != null)
				return false;
		} else if (!requestType.equals(other.requestType))
			return false;
		if (serviceId == null) {
			if (other.serviceId != null)
				return false;
		} else if (!serviceId.equals(other.serviceId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RequestFormId [requestType=" + requestType + ", serviceId="
				+ serviceId + "]";
	}

    
}
