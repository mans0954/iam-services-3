package org.openiam.am.srvc.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.openiam.base.domain.AbstractKeyNameEntity;

@MappedSuperclass
@AttributeOverrides({
	@AttributeOverride(name = "name", column = @Column(name = "URI_META_NAME", length = 100, nullable = false))
})
public abstract class AbstractMetaEntity extends AbstractKeyNameEntity {
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="URI_PATTERN_META_TYPE_ID", referencedColumnName = "URI_PATTERN_META_TYPE_ID")
	protected URIPatternMetaTypeEntity metaType;
	
	@Column(name="CONTENT_TYPE", length=100)
	protected String contentType;

	public URIPatternMetaTypeEntity getMetaType() {
		return metaType;
	}

	public void setMetaType(URIPatternMetaTypeEntity metaType) {
		this.metaType = metaType;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((contentType == null) ? 0 : contentType.hashCode());
		result = prime * result
				+ ((metaType == null) ? 0 : metaType.hashCode());
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
		AbstractMetaEntity other = (AbstractMetaEntity) obj;
		if (contentType == null) {
			if (other.contentType != null)
				return false;
		} else if (!contentType.equals(other.contentType))
			return false;
		if (metaType == null) {
			if (other.metaType != null)
				return false;
		} else if (!metaType.equals(other.metaType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AbstractMetaEntity [metaType=" + metaType + ", contentType="
				+ contentType + ", name=" + name + ", id=" + id + "]";
	}

	
}
