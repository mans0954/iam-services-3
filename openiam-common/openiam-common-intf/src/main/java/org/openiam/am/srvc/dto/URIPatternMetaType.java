package org.openiam.am.srvc.dto;

import java.io.Serializable;

import org.openiam.am.srvc.domain.URIPatternMetaTypeEntity;
import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URIPatternMetaType", propOrder = {
        "springBeanName"
})
@DozerDTOCorrespondence(URIPatternMetaTypeEntity.class)
public class URIPatternMetaType extends KeyNameDTO {

	private static final long serialVersionUID = 1L;
	
	public URIPatternMetaType() {}
	
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
		URIPatternMetaType other = (URIPatternMetaType) obj;
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
				"URIPatternMetaType [springBeanName=%s, toString()=%s]",
				springBeanName, super.toString());
	}
	
	
}
