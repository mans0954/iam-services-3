package org.openiam.base.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Type;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.internationalization.Internationalized;

import java.util.List;

@MappedSuperclass
@AttributeOverrides(value={
	@AttributeOverride(name = "name", column = @Column(name="NAME", length=100)),
	@AttributeOverride(name = "value", column = @Column(name="ATTR_VALUE", length=4000))
})
public abstract class AbstractAttributeEntity extends AbstractKeyNameValueEntity {


//    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch= FetchType.LAZY)
//    @JoinColumn(name = "METADATA_ID", insertable = true, updatable = true, nullable=true)
//    @Internationalized
//    protected MetadataElementEntity element;
	@Column(name = "METADATA_ID", nullable = true)
	private String metadataElementId;

//	public MetadataElementEntity getElement() {
//		return element;
//	}
//
//	public void setElement(MetadataElementEntity element) {
//		this.element = element;
//	}

	public String getMetadataElementId() {
		return metadataElementId;
	}

	public void setMetadataElementId(String metadataElementId) {
		this.metadataElementId = metadataElementId;
	}

	public abstract boolean getIsMultivalued();

	public abstract void setIsMultivalued(boolean isMultivalued);

	public abstract List<String> getValues();
    public abstract void setValues(List<String> values);

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((metadataElementId == null) ? 0 : metadataElementId.hashCode());
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
		AbstractAttributeEntity other = (AbstractAttributeEntity) obj;
		if (metadataElementId == null) {
			if (other.metadataElementId != null)
				return false;
		} else if (!metadataElementId.equals(other.metadataElementId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format(
				"AbstractAttributeEntity [metadataElementId=%s, toString()=%s]", (metadataElementId == null) ? "null" : metadataElementId,
				super.toString());
	}

	
}
