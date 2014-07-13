package org.openiam.am.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.am.srvc.dto.URIPatternMetaValue;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "URI_PATTERN_META_VALUE")
@DozerDTOCorrespondence(URIPatternMetaValue.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "URI_PATTERN_META_VALUE_ID")),
	@AttributeOverride(name = "name", column = @Column(name = "META_ATTRIBUTE_NAME", length = 100, nullable = false))
})
public class URIPatternMetaValueEntity extends AbstractKeyNameEntity {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "STATIC_VALUE", length = 4096, nullable = true)
	private String staticValue;
	
	@Column(name = "GROOVY_SCRIPT", length = 200, nullable = true)
	private String groovyScript;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="AM_RES_ATTRIBUTE_ID", referencedColumnName = "AM_RES_ATTRIBUTE_ID", nullable=true, insertable=true, updatable=true)
	private AuthResourceAMAttributeEntity amAttribute;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="URI_PATTERN_META_ID", referencedColumnName = "URI_PATTERN_META_ID", insertable=true, updatable=true, nullable=false)
	private URIPatternMetaEntity metaEntity;
    //@Transient
	//private AttributeOperationEnum operation = AttributeOperationEnum.NO_CHANGE;
	
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((amAttribute == null) ? 0 : amAttribute.hashCode());
		result = prime * result
				+ ((groovyScript == null) ? 0 : groovyScript.hashCode());
		result = prime * result
				+ ((metaEntity == null) ? 0 : metaEntity.hashCode());
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
		URIPatternMetaValueEntity other = (URIPatternMetaValueEntity) obj;
		if (amAttribute == null) {
			if (other.amAttribute != null)
				return false;
		} else if (!amAttribute.equals(other.amAttribute))
			return false;
		if (groovyScript == null) {
			if (other.groovyScript != null)
				return false;
		} else if (!groovyScript.equals(other.groovyScript))
			return false;
		if (metaEntity == null) {
			if (other.metaEntity != null)
				return false;
		} else if (!metaEntity.equals(other.metaEntity))
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
				.format("URIPatternMetaValueEntity [staticValue=%s, groovyScript=%s, amAttribute=%s, metaEntity=%s]",
						staticValue, groovyScript, amAttribute, metaEntity);
	}

	
}
