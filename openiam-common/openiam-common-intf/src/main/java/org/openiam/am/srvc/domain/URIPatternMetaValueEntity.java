package org.openiam.am.srvc.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.am.srvc.dto.URIPatternMetaValue;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "URI_PATTERN_META_VALUE")
@DozerDTOCorrespondence(URIPatternMetaValue.class)
public class URIPatternMetaValueEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "URI_PATTERN_META_VALUE_ID", length = 32, nullable = false)
	private String id;
	
	@Column(name = "META_ATTRIBUTE_NAME", length = 100, nullable = false)
	private String name;
	
	@Column(name = "STATIC_VALUE", length = 4000, nullable = true)
	private String staticValue;
	
	@Column(name = "GROOVY_SCRIPT", length = 200, nullable = true)
	private String groovyScript;
	
	@Column(name = "IS_EMPTY_VALUE")
    @Type(type = "yes_no")
	private boolean emptyValue;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="AM_RES_ATTRIBUTE_ID", referencedColumnName = "AM_RES_ATTRIBUTE_ID", nullable=true, insertable=true, updatable=true)
	private AuthResourceAMAttributeEntity amAttribute;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="URI_PATTERN_META_ID", referencedColumnName = "URI_PATTERN_META_ID", insertable=true, updatable=true, nullable=false)
	private URIPatternMetaEntity metaEntity;
    //@Transient
	//private AttributeOperationEnum operation = AttributeOperationEnum.NO_CHANGE;
	
	@Column(name = "PROPAGETE_THROUGH_PROXY", nullable = false)
	@Type(type = "yes_no")
	private boolean propagateThroughProxy = true;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public AuthResourceAMAttributeEntity getAmAttribute() {
		return amAttribute;
	}
	
	public void setAmAttribute(AuthResourceAMAttributeEntity amAttribute) {
		this.amAttribute = amAttribute;
	}
	
	public String getStaticValue() {
		return staticValue;
	}
	
	public void setStaticValue(String staticValue) {
		this.staticValue = staticValue;
	}

	public URIPatternMetaEntity getMetaEntity() {
		return metaEntity;
	}

	public void setMetaEntity(URIPatternMetaEntity metaEntity) {
		this.metaEntity = metaEntity;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((amAttribute == null) ? 0 : amAttribute.hashCode());
		result = prime * result + (emptyValue ? 1231 : 1237);
		result = prime * result
				+ ((groovyScript == null) ? 0 : groovyScript.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((metaEntity == null) ? 0 : metaEntity.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (propagateThroughProxy ? 1231 : 1237);
		result = prime * result
				+ ((staticValue == null) ? 0 : staticValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		URIPatternMetaValueEntity other = (URIPatternMetaValueEntity) obj;
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (metaEntity == null) {
			if (other.metaEntity != null)
				return false;
		} else if (!metaEntity.equals(other.metaEntity))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
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
		return String
				.format("URIPatternMetaValueEntity [id=%s, name=%s, staticValue=%s, groovyScript=%s, emptyValue=%s, amAttribute=%s, metaEntity=%s, propagateThroughProxy=%s]",
						id, name, staticValue, groovyScript, emptyValue,
						amAttribute, metaEntity, propagateThroughProxy);
	}

	
}
