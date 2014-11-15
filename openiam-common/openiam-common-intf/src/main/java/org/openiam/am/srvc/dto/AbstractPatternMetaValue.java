package org.openiam.am.srvc.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.KeyNameDTO;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractPatternMetaValue", propOrder = {
        "staticValue",
        "amAttribute",
        "groovyScript",
        "propagateThroughProxy",
        "propagateOnError",
        "emptyValue"
})
public abstract class AbstractPatternMetaValue extends KeyNameDTO {

	protected String staticValue;
	protected AuthResourceAMAttribute amAttribute;
	protected String groovyScript;
	protected boolean propagateThroughProxy = true;
	protected boolean emptyValue = false;
	protected boolean propagateOnError = true;
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
	public String getGroovyScript() {
		return groovyScript;
	}
	public void setGroovyScript(String groovyScript) {
		this.groovyScript = groovyScript;
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
				+ ((groovyScript == null) ? 0 : groovyScript.hashCode());
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
		AbstractPatternMetaValue other = (AbstractPatternMetaValue) obj;
		if (amAttribute == null) {
			if (other.amAttribute != null)
				return false;
		} else if (!amAttribute.equals(other.amAttribute))
			return false;
		if (emptyValue != other.emptyValue)
			return false;
		if (groovyScript == null) {
			if (other.groovyScript != null)
				return false;
		} else if (!groovyScript.equals(other.groovyScript))
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
		return "AbstractPatternMetaValue [staticValue=" + staticValue
				+ ", amAttribute=" + amAttribute + ", groovyScript="
				+ groovyScript + ", propagateThroughProxy="
				+ propagateThroughProxy + ", emptyValue=" + emptyValue
				+ ", propagateOnError=" + propagateOnError + ", name=" + name
				+ ", id=" + id + ", objectState=" + objectState
				+ ", requestorSessionID=" + requestorSessionID
				+ ", requestorUserId=" + requestorUserId + ", requestorLogin="
				+ requestorLogin + ", requestClientIP=" + requestClientIP + "]";
	}
	
	
}
