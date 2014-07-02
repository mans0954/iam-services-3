package org.openiam.am.srvc.domain;

import java.io.Serializable;

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
import org.openiam.dozer.DozerDTOCorrespondence;

@Entity
@Table(name = "URI_PATTERN_META_TYPE")
@DozerDTOCorrespondence(URIPatternMetaType.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class URIPatternMetaTypeEntity  implements Serializable {

	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "URI_PATTERN_META_TYPE_ID", length = 32, nullable = false)
	private String id;
	
	@Column(name = "METADATA_TYPE_NAME", length = 100, nullable = false)
	private String name;
	
	@Column(name = "SPRING_BEAN_NAME", length = 100, nullable = false)
	private String springBeanName;

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

	public String getSpringBeanName() {
		return springBeanName;
	}

	public void setSpringBeanName(String springBeanName) {
		this.springBeanName = springBeanName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((springBeanName == null) ? 0 : springBeanName.hashCode());
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
		URIPatternMetaTypeEntity other = (URIPatternMetaTypeEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
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
				"URIPatternMetaTypeEntity [id=%s, name=%s, springBeanName=%s]",
				id, name, springBeanName);
	}
	
	
}
