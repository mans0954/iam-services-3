package org.openiam.am.srvc.dto;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.am.srvc.domain.URIPatternMethodMetaEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URIPatternMethodMeta", propOrder = {
        "patternMethod",
        "metaValueSet"
})
@DozerDTOCorrespondence(URIPatternMethodMetaEntity.class)
public class URIPatternMethodMeta extends AbstractMeta {

	private Set<URIPatternMethodMetaValue> metaValueSet;
	private URIPatternMethod patternMethod;
	
	public Set<URIPatternMethodMetaValue> getMetaValueSet() {
		return metaValueSet;
	}

	public void setMetaValueSet(Set<URIPatternMethodMetaValue> metaValueSet) {
		this.metaValueSet = metaValueSet;
	}

	public URIPatternMethod getPatternMethod() {
		return patternMethod;
	}

	public void setPatternMethod(URIPatternMethod patternMethod) {
		this.patternMethod = patternMethod;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((patternMethod == null) ? 0 : patternMethod.hashCode());
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
		URIPatternMethodMeta other = (URIPatternMethodMeta) obj;
		if (patternMethod == null) {
			if (other.patternMethod != null)
				return false;
		} else if (!patternMethod.equals(other.patternMethod))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "URIPatternMethodMeta [patternMethod=" + patternMethod
				+ ", metaType=" + metaType + ", name=" + name + ", id=" + id
				+ ", objectState=" + objectState + ", requestorSessionID="
				+ requestorSessionID + ", requestorUserId=" + requestorUserId
				+ ", requestorLogin=" + requestorLogin + ", requestClientIP="
				+ requestClientIP + "]";
	}
	
	
}
