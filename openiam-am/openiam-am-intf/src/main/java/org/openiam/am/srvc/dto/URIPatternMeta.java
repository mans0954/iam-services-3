package org.openiam.am.srvc.dto;

import org.openiam.am.srvc.domain.URIPatternMetaEntity;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URIPatternMeta", propOrder = {
        "id",
        "uriPatternId",
        "name",
        "metaType",
        "metaValueSet"
})
@DozerDTOCorrespondence(URIPatternMetaEntity.class)
public class URIPatternMeta implements Serializable {

	private String id;
	private String uriPatternId;
    private String name;
	private URIPatternMetaType metaType;
	private Set<URIPatternMetaValue> metaValueSet;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUriPatternId() {
		return uriPatternId;
	}
	public void setUriPatternId(String uriPatternId) {
		this.uriPatternId = uriPatternId;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URIPatternMetaType getMetaType() {
		return metaType;
	}
	public void setMetaType(URIPatternMetaType metaType) {
		this.metaType = metaType;
	}
	public Set<URIPatternMetaValue> getMetaValueSet() {
		return metaValueSet;
	}
	public void setMetaValueSet(Set<URIPatternMetaValue> metaValueSet) {
		this.metaValueSet = metaValueSet;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((metaType == null) ? 0 : metaType.hashCode());
		result = prime * result
				+ ((uriPatternId == null) ? 0 : uriPatternId.hashCode());
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
		URIPatternMeta other = (URIPatternMeta) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (metaType == null) {
			if (other.metaType != null)
				return false;
		} else if (!metaType.equals(other.metaType))
			return false;
		if (uriPatternId == null) {
			if (other.uriPatternId != null)
				return false;
		} else if (!uriPatternId.equals(other.uriPatternId))
			return false;
		return true;
	}
}
