package org.openiam.am.srvc.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.KeyNameDTO;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractMeta", propOrder = {
        "metaType",
        "contentType"
})
public abstract class AbstractMeta extends KeyNameDTO {

	protected String contentType;
	protected URIPatternMetaType metaType;

	public URIPatternMetaType getMetaType() {
		return metaType;
	}

	public void setMetaType(URIPatternMetaType metaType) {
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
		AbstractMeta other = (AbstractMeta) obj;
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
		return "AbstractMeta [contentType=" + contentType + ", metaType="
				+ metaType + ", name=" + this.getName() + ", id=" + id + ", objectState="
				+ objectState + ", requestorSessionID=" + requestorSessionID
				+ ", requestorUserId=" + requestorUserId + ", requestorLogin="
				+ requestorLogin + ", requestClientIP=" + requestClientIP + "]";
	}

	
}
