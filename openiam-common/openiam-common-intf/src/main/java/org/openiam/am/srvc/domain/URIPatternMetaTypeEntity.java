package org.openiam.am.srvc.domain;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.am.srvc.dto.URIPatternMetaType;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

@Entity
@Table(name = "URI_PATTERN_META_TYPE")
@DozerDTOCorrespondence(URIPatternMetaType.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "URI_PATTERN_META_TYPE_ID")),
	@AttributeOverride(name = "name", column = @Column(name = "METADATA_TYPE_NAME", length = 100, nullable = false))
})
public class URIPatternMetaTypeEntity extends AbstractKeyNameEntity {
	
	public URIPatternMetaTypeEntity() {}

	@Column(name = "SPRING_BEAN_NAME", length = 100, nullable = false)
	private String springBeanName;

	public String getSpringBeanName() {
		return springBeanName;
	}

	public void setSpringBeanName(String springBeanName) {
		this.springBeanName = springBeanName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((springBeanName == null) ? 0 : springBeanName.hashCode());
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
		URIPatternMetaTypeEntity other = (URIPatternMetaTypeEntity) obj;
		if (springBeanName == null) {
			if (other.springBeanName != null)
				return false;
		} else if (!springBeanName.equals(other.springBeanName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format(
				"URIPatternMetaTypeEntity [springBeanName=%s, toString()=%s]",
				springBeanName, super.toString());
	}

	
}
