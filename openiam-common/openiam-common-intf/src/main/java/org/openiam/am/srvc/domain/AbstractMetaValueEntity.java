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
	
	@Column(name = "STATIC_VALUE", length = 4096, nullable = true)
	protected String staticValue;
	
	@Column(name = "GROOVY_SCRIPT", length = 200, nullable = true)
	protected String groovyScript;
	
	@Column(name = "IS_EMPTY_VALUE")
    @Type(type = "yes_no")
	protected boolean emptyValue;
	
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

	
}
