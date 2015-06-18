package org.openiam.am.srvc.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Type;
import org.openiam.base.domain.AbstractKeyNameEntity;

@AttributeOverrides({
	@AttributeOverride(name = "name", column = @Column(name = "META_ATTRIBUTE_NAME", length = 100, nullable = false))
})
@MappedSuperclass
public abstract class AbstractMetaValueEntity extends AbstractKeyNameEntity {
	
	@Column(name = "PROPAGETE_THROUGH_PROXY", nullable = false)
	@Type(type = "yes_no")
	protected boolean propagateThroughProxy = true;
	
	@Column(name = "PROPAGETE_ON_ERROR", nullable = false)
	@Type(type = "yes_no")
	protected boolean propagateOnError = true;
	
	@Column(name = "STATIC_VALUE", length = 4000, nullable = true)
	protected String staticValue;
	
	@Column(name = "GROOVY_SCRIPT", length = 200, nullable = true)
	protected String groovyScript;
	
	@Column(name = "IS_EMPTY_VALUE")
    @Type(type = "yes_no")
	protected boolean emptyValue;
	
	@Column(name="FETCHED_VALUE",length=400)
	protected String fetchedValue;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="AM_RES_ATTRIBUTE_ID", referencedColumnName = "AM_RES_ATTRIBUTE_ID", nullable=true, insertable=true, updatable=true)
	protected AuthResourceAMAttributeEntity amAttribute;

	public boolean isPropagateThroughProxy() {
		return propagateThroughProxy;
	}

	public void setPropagateThroughProxy(boolean propagateThroughProxy) {
		this.propagateThroughProxy = propagateThroughProxy;
	}

	public boolean isPropagateOnError() {
		return propagateOnError;
	}

	public void setPropagateOnError(boolean propagateOnError) {
		this.propagateOnError = propagateOnError;
	}

	public String getStaticValue() {
		return staticValue;
	}

	public void setStaticValue(String staticValue) {
		this.staticValue = staticValue;
	}

	public String getGroovyScript() {
		return groovyScript;
	}

	public void setGroovyScript(String groovyScript) {
		this.groovyScript = groovyScript;
	}

	public boolean isEmptyValue() {
		return emptyValue;
	}

	public void setEmptyValue(boolean emptyValue) {
		this.emptyValue = emptyValue;
	}

	public AuthResourceAMAttributeEntity getAmAttribute() {
		return amAttribute;
	}

	public void setAmAttribute(AuthResourceAMAttributeEntity amAttribute) {
		this.amAttribute = amAttribute;
	}

	public String getFetchedValue() {
		return fetchedValue;
	}
	public void setFetchedValue(String fetchedValue) {
		this.fetchedValue = fetchedValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((amAttribute == null) ? 0 : amAttribute.hashCode());
		result = prime * result + (emptyValue ? 1231 : 1237);
		result = prime * result
				+ ((fetchedValue == null) ? 0 : fetchedValue.hashCode());
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
		AbstractMetaValueEntity other = (AbstractMetaValueEntity) obj;
		if (amAttribute == null) {
			if (other.amAttribute != null)
				return false;
		} else if (!amAttribute.equals(other.amAttribute))
			return false;
		if (emptyValue != other.emptyValue)
			return false;
		if (fetchedValue == null) {
			if (other.fetchedValue != null)
				return false;
		} else if (!fetchedValue.equals(other.fetchedValue))
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
		return "AbstractMetaValueEntity [propagateThroughProxy="
				+ propagateThroughProxy + ", propagateOnError="
				+ propagateOnError + ", staticValue=" + staticValue
				+ ", groovyScript=" + groovyScript + ", emptyValue="
				+ emptyValue + ", fetchedValue=" + fetchedValue
				+ ", amAttribute=" + amAttribute + ", name=" + name + ", id="
				+ id + "]";
	}

	
}
