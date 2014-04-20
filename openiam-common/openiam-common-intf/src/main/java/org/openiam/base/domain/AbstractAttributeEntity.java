package org.openiam.base.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.internationalization.Internationalized;

@MappedSuperclass
@AttributeOverrides(value={
	@AttributeOverride(name = "name", column = @Column(name="NAME", length=100)),
	@AttributeOverride(name = "value", column = @Column(name="_VALUE", length=4096))
})
public abstract class AbstractAttributeEntity extends AbstractKeyNameValueEntity {


    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch= FetchType.LAZY)
    @JoinColumn(name = "METADATA_ID", insertable = true, updatable = true, nullable=true)
    @Internationalized
    protected MetadataElementEntity element;

	public MetadataElementEntity getElement() {
		return element;
	}

	public void setElement(MetadataElementEntity element) {
		this.element = element;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((element == null) ? 0 : element.hashCode());
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
		if (element == null) {
			if (other.element != null)
				return false;
		} else if (!element.equals(other.element))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format(
				"AbstractAttributeEntity [element=%s, toString()=%s]", element,
				super.toString());
	}

	
}
