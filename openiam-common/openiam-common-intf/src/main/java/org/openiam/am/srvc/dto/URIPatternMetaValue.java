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
        "staticValue",
        "amAttribute",
        "metaEntityId",
        "groovyScript",
        "propagateThroughProxy",
        "propagateOnError",
        "emptyValue"
})
@DozerDTOCorrespondence(URIPatternMetaValueEntity.class)
public class URIPatternMetaValue extends KeyNameDTO {
	private String staticValue;
	private AuthResourceAMAttribute amAttribute;
	private String groovyScript;
	private String metaEntityId;
	private boolean propagateThroughProxy = true;
	private boolean emptyValue = false;
	private boolean propagateOnError = true;
	
	/* internal use only!  Is compiled at spring refresh time 
	 * to avoid run-time groovy class initialization.  
	 * It is a WeakReference to prevent accidental
	 * memory leaks of PermGen space.  The refresh thread, however,
	 * should clear this reference
	 */
	@Transient
	@XmlTransient
	private URIFederationGroovyProcessor groovyProcessor;
	
	public String getStaticValue() {
		return staticValue;
	}
	public void setStaticValue(String staticValue) {
		this.staticValue = staticValue;
	}
	public AuthResourceAMAttribute getAmAttribute() {
		return amAttribute;
	}
	public void setAmAttribute(AuthResourceAMAttribute amAttribute) {
		this.amAttribute = amAttribute;
	}
	public String getMetaEntityId() {
		return metaEntityId;
	}
	public void setMetaEntityId(String metaEntityId) {
		this.metaEntityId = metaEntityId;
	}
    
    public String getGroovyScript() {
		return groovyScript;
	}

	public void setGroovyScript(String groovyScript) {
		this.groovyScript = groovyScript;
	}
	
	public URIFederationGroovyProcessor getGroovyProcessor() {
		return this.groovyProcessor;
	}
	public void setGroovyProcessor(final  URIFederationGroovyProcessor groovyProcessor) {
		this.groovyProcessor = groovyProcessor;
	}
	public boolean isPropagateThroughProxy() {
		return propagateThroughProxy;
	}
	public void setPropagateThroughProxy(boolean propagateThroughProxy) {
		this.propagateThroughProxy = propagateThroughProxy;
	}
	
	public boolean isEmptyValue() {
		return emptyValue;
	}

	public void setEmptyValue(boolean emptyValue) {
		this.emptyValue = emptyValue;
	}
	public boolean isPropagateOnError() {
		return propagateOnError;
	}
	public void setPropagateOnError(boolean propagateOnError) {
		this.propagateOnError = propagateOnError;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((amAttribute == null) ? 0 : amAttribute.hashCode());
		result = prime * result + (emptyValue ? 1231 : 1237);
		result = prime * result
				+ ((groovyProcessor == null) ? 0 : groovyProcessor.hashCode());
		result = prime * result
				+ ((groovyScript == null) ? 0 : groovyScript.hashCode());
		result = prime * result
				+ ((metaEntityId == null) ? 0 : metaEntityId.hashCode());
		result = prime * result + (propagateOnError ? 1231 : 1237);
		result = prime * result + (propagateThroughProxy ? 1231 : 1237);
		result = prime * result
				+ ((staticValue == null) ? 0 : staticValue.hashCode());
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
		if (amAttribute == null) {
			if (other.amAttribute != null)
				return false;
		} else if (!amAttribute.equals(other.amAttribute))
			return false;
		if (emptyValue != other.emptyValue)
			return false;
		if (groovyProcessor == null) {
			if (other.groovyProcessor != null)
				return false;
		} else if (!groovyProcessor.equals(other.groovyProcessor))
			return false;
		if (groovyScript == null) {
			if (other.groovyScript != null)
				return false;
		} else if (!groovyScript.equals(other.groovyScript))
			return false;
		if (metaEntityId == null) {
			if (other.metaEntityId != null)
				return false;
		} else if (!metaEntityId.equals(other.metaEntityId))
			return false;
		if (propagateOnError != other.propagateOnError)
			return false;
		if (propagateThroughProxy != other.propagateThroughProxy)
			return false;
		if (staticValue == null) {
			if (other.staticValue != null)
				return false;
		} else if (!staticValue.equals(other.staticValue))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "URIPatternMetaValue [staticValue=" + staticValue
				+ ", amAttribute=" + amAttribute + ", groovyScript="
				+ groovyScript + ", metaEntityId=" + metaEntityId
				+ ", propagateThroughProxy=" + propagateThroughProxy
				+ ", propagateOnError=" + propagateOnError + "]";
	}
	
	
}
