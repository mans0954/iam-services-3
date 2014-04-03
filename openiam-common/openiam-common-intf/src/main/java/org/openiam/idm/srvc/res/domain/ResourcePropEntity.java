package org.openiam.idm.srvc.res.domain;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.user.domain.UserEntity;

@Entity
@Table(name="RESOURCE_PROP")
@DozerDTOCorrespondence(ResourceProp.class)
public class ResourcePropEntity {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="RESOURCE_PROP_ID", length=32)
    private String resourcePropId;
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = true, updatable = false)
    private ResourceEntity resource;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch= FetchType.LAZY)
    @JoinColumn(name = "METADATA_ID", insertable = true, updatable = true, nullable=true)
    private MetadataElementEntity element;

    @Column(name="PROP_VALUE",length=200)
    private String propValue;

    @Column(name="NAME",length=40)
    private String name;

    public ResourcePropEntity() {
    }

    public String getResourcePropId() {
        return resourcePropId;
    }

    public void setResourcePropId(String resourcePropId) {
        this.resourcePropId = resourcePropId;
    }

    public ResourceEntity getResource() {
		return resource;
	}

	public void setResource(ResourceEntity resource) {
		this.resource = resource;
	}

    public MetadataElementEntity getElement() {
        return element;
    }

    public void setElement(MetadataElementEntity element) {
        this.element = element;
    }

    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
        result = prime * result + ((element == null) ? 0 : element.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((propValue == null) ? 0 : propValue.hashCode());
		result = prime * result
				+ ((resource == null) ? 0 : resource.hashCode());
		result = prime * result
				+ ((resourcePropId == null) ? 0 : resourcePropId.hashCode());
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
		ResourcePropEntity other = (ResourcePropEntity) obj;
        if (element == null) {
            if (other.element != null)
                return false;
        } else if (!element.equals(other.element))
            return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (propValue == null) {
			if (other.propValue != null)
				return false;
		} else if (!propValue.equals(other.propValue))
			return false;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		if (resourcePropId == null) {
			if (other.resourcePropId != null)
				return false;
		} else if (!resourcePropId.equals(other.resourcePropId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ResourcePropEntity [resourcePropId=" + resourcePropId
				+ ", resource=" + resource + ", propValue=" + propValue + ", name=" + name + "]";
	}

    
}
