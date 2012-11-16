package org.openiam.idm.srvc.res.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.res.dto.ResourceUser;
import org.openiam.idm.srvc.res.dto.ResourceUserId;

@Embeddable
@DozerDTOCorrespondence(ResourceUserId.class)
public class ResourceUserEmbeddableId implements Serializable {
    @Column(name = "RESOURCE_ID", length = 32, nullable = false)
    private String resourceId;
    @Column(name = "USER_ID", length = 32, nullable = false)
    private String userId;

    public ResourceUserEmbeddableId() {
    }

    public ResourceUserEmbeddableId(String resourceId, String userId, String privilegeId) {
        this.resourceId = resourceId;
        this.userId = userId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		ResourceUserEmbeddableId other = (ResourceUserEmbeddableId) obj;
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

    
}
