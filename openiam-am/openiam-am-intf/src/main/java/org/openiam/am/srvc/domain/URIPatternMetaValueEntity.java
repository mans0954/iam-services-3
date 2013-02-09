package org.openiam.am.srvc.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.openiam.am.srvc.dto.URIPatternMetaValue;
import org.openiam.dozer.DozerDTOCorrespondence;

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
	
	@Column(name = "STATIC_VALUE", length = 100, nullable = false)
	private String staticValue;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="AM_ATTRIBUTE_ID", referencedColumnName = "AM_ATTRIBUTE_ID", insertable = false, updatable = false)
	private AuthResourceAMAttributeEntity amAttribute;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="URI_PATTERN_META_ID", referencedColumnName = "URI_PATTERN_META_ID", insertable = false, updatable = false)
	private URIPatternMetaEntity metaEntity;
	
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((amAttribute == null) ? 0 : amAttribute.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((metaEntity == null) ? 0 : metaEntity.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (staticValue == null) {
			if (other.staticValue != null)
				return false;
		} else if (!staticValue.equals(other.staticValue))
			return false;
		return true;
	}

	
}
