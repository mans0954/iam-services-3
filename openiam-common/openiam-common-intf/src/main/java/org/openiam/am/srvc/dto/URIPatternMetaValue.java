package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.domain.URIPatternMetaValueEntity;
import org.openiam.am.srvc.groovy.URIFederationGroovyProcessor;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import java.io.Serializable;
import java.lang.ref.WeakReference;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URIPatternMetaValue", propOrder = {
        "metaEntityId"
})
@DozerDTOCorrespondence(URIPatternMetaValueEntity.class)
public class URIPatternMetaValue extends AbstractPatternMetaValue {
	private String metaEntityId;
	
	
	public String getMetaEntityId() {
		return metaEntityId;
	}
	public void setMetaEntityId(String metaEntityId) {
		this.metaEntityId = metaEntityId;
	}
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((metaEntityId == null) ? 0 : metaEntityId.hashCode());
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
		URIPatternMetaValue other = (URIPatternMetaValue) obj;
		if (metaEntityId == null) {
			if (other.metaEntityId != null)
				return false;
		} else if (!metaEntityId.equals(other.metaEntityId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "URIPatternMetaValue [metaEntityId=" + metaEntityId +
				", staticValue=" + staticValue + ", amAttribute=" + amAttribute
				+ ", groovyScript=" + groovyScript + ", propagateThroughProxy="
				+ propagateThroughProxy + ", emptyValue=" + emptyValue
				+ ", propagateOnError=" + propagateOnError + ", name=" + this.getName()
				+ ", id=" + id + ", objectState=" + objectState
				+ ", requestorSessionID=" + requestorSessionID
				+ ", requestorUserId=" + requestorUserId + ", requestorLogin="
				+ requestorLogin + ", requestClientIP=" + requestClientIP + "]";
	}
	
	
}
