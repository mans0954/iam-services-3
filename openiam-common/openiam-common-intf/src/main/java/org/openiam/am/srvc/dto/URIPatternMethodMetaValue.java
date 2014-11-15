package org.openiam.am.srvc.dto;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.openiam.am.srvc.domain.URIPatternMethodMetaValueEntity;
import org.openiam.am.srvc.groovy.URIFederationGroovyProcessor;
import org.openiam.dozer.DozerDTOCorrespondence;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URIPatternMethodMetaValue", propOrder = {
        "metaEntityId"
})
@DozerDTOCorrespondence(URIPatternMethodMetaValueEntity.class)
public class URIPatternMethodMetaValue extends AbstractPatternMetaValue {
	
	private String metaEntityId;
	
	/* internal use only!  Is compiled at spring refresh time 
	 * to avoid run-time groovy class initialization.  
	 * It is a WeakReference to prevent accidental
	 * memory leaks of PermGen space.  The refresh thread, however,
	 * should clear this reference
	 */
	@Transient
	@XmlTransient
	private URIFederationGroovyProcessor groovyProcessor;
	
	
	public String getMetaEntityId() {
		return metaEntityId;
	}
	public void setMetaEntityId(String metaEntityId) {
		this.metaEntityId = metaEntityId;
	}
    
	public URIFederationGroovyProcessor getGroovyProcessor() {
		return this.groovyProcessor;
	}
	public void setGroovyProcessor(final  URIFederationGroovyProcessor groovyProcessor) {
		this.groovyProcessor = groovyProcessor;
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
		URIPatternMethodMetaValue other = (URIPatternMethodMetaValue) obj;
		if (metaEntityId == null) {
			if (other.metaEntityId != null)
				return false;
		} else if (!metaEntityId.equals(other.metaEntityId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "URIPatternMetaValue [metaEntityId=" + metaEntityId + ", name="
				+ name + ", id=" + id + ", objectState=" + objectState
				+ ", requestorSessionID=" + requestorSessionID
				+ ", requestorUserId=" + requestorUserId + ", requestorLogin="
				+ requestorLogin + ", requestClientIP=" + requestClientIP + "]";
	}
	
	
}
