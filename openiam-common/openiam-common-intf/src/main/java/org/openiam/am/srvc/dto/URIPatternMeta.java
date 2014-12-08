package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.domain.URIPatternMetaEntity;
import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URIPatternMeta", propOrder = {
        "patternId",
        "metaValueSet"
})
@DozerDTOCorrespondence(URIPatternMetaEntity.class)
public class URIPatternMeta extends AbstractMeta<URIPatternMetaValue> {

	private String patternId;
	private Set<URIPatternMetaValue> metaValueSet;
	
	public String getPatternId() {
		return patternId;
	}
	public void setPatternId(String patternId) {
		this.patternId = patternId;
	}
	public Set<URIPatternMetaValue> getMetaValueSet() {
		return metaValueSet;
	}
	public void setMetaValueSet(Set<URIPatternMetaValue> metaValueSet) {
		this.metaValueSet = metaValueSet;
	}
	
	@Override
	public void addMetaValue(AbstractPatternMetaValue value) {
		if(value != null) {
			if(this.metaValueSet == null) {
				this.metaValueSet = new HashSet<URIPatternMetaValue>();
			}
			this.metaValueSet.add((URIPatternMetaValue)value);
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((metaValueSet == null) ? 0 : metaValueSet.hashCode());
		result = prime * result
				+ ((patternId == null) ? 0 : patternId.hashCode());
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
		URIPatternMeta other = (URIPatternMeta) obj;
		if (metaValueSet == null) {
			if (other.metaValueSet != null)
				return false;
		} else if (!metaValueSet.equals(other.metaValueSet))
			return false;
		if (patternId == null) {
			if (other.patternId != null)
				return false;
		} else if (!patternId.equals(other.patternId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "URIPatternMeta [patternId=" + patternId
				+ ", metaValueSet=" + metaValueSet + ", metaType=" + metaType
				+ ", name=" + this.getName() + ", id=" + id + ", objectState="
				+ objectState + ", requestorSessionID=" + requestorSessionID
				+ ", requestorUserId=" + requestorUserId + ", requestorLogin="
				+ requestorLogin + ", requestClientIP=" + requestClientIP + "]";
	}

	
	
}
