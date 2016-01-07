package org.openiam.idm.srvc.res.domain;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.openiam.base.domain.AbstractAttributeEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.internationalization.Internationalized;

import java.util.List;

@Entity
@Table(name="RESOURCE_PROP")
@AttributeOverride(name = "id", column = @Column(name = "RESOURCE_PROP_ID"))
@DozerDTOCorrespondence(ResourceProp.class)
@Internationalized
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ResourcePropEntity extends AbstractAttributeEntity {
   
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = true, updatable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private ResourceEntity resource;
    @Column(name = "IS_MULTIVALUED", nullable = false)
    @Type(type = "yes_no")
    private boolean isMultivalued = false;

    public ResourcePropEntity() {
    }

    public ResourceEntity getResource() {
		return resource;
	}

	public void setResource(ResourceEntity resource) {
		this.resource = resource;
	}
	
	public boolean getIsMultivalued() {
		return isMultivalued;
	}

	public void setIsMultivalued(boolean isMultivalued) {
		this.isMultivalued = isMultivalued;
	}

	@Override
	public List<String> getValues() {
		return null;
	}
	public void setValues(List<String> values) {
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isMultivalued ? 1231 : 1237);
		result = prime * result
				+ ((resource == null) ? 0 : resource.hashCode());
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
		ResourcePropEntity other = (ResourcePropEntity) obj;
		if (isMultivalued != other.isMultivalued)
			return false;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("ResourcePropEntity [resource=%s, isMultivalued=%s, toString()=%s]",
						resource, isMultivalued, super.toString());
	}

	
}
