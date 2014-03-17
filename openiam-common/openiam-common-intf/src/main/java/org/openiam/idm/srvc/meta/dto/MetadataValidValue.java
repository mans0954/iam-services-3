package org.openiam.idm.srvc.meta.dto;

import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.meta.domain.MetadataValidValueEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataValidValue", 
	propOrder = {
        "uiValue",
        "metadataEntityId",
        "displayOrder",
        "languageMap"
})
@DozerDTOCorrespondence(MetadataValidValueEntity.class)
public class MetadataValidValue extends KeyDTO implements Serializable, Comparable<MetadataValidValue> {

	private String uiValue;
	private String metadataEntityId;
    private Integer displayOrder;
	private Map<String, LanguageMapping> languageMap;
	
	public String getUiValue() {
		return uiValue;
	}
	public void setUiValue(String uiValue) {
		this.uiValue = uiValue;
	}
	public String getMetadataEntityId() {
		return metadataEntityId;
	}
	public void setMetadataEntityId(String metadataEntityId) {
		this.metadataEntityId = metadataEntityId;
	}

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Map<String, LanguageMapping> getLanguageMap() {
		return languageMap;
	}
	public void setLanguageMap(Map<String, LanguageMapping> languageMap) {
		this.languageMap = languageMap;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime
				* result
				+ ((metadataEntityId == null) ? 0 : metadataEntityId.hashCode());
		result = prime * result + ((uiValue == null) ? 0 : uiValue.hashCode());
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
		MetadataValidValue other = (MetadataValidValue) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (metadataEntityId == null) {
			if (other.metadataEntityId != null)
				return false;
		} else if (!metadataEntityId.equals(other.metadataEntityId))
			return false;
		if (uiValue == null) {
			if (other.uiValue != null)
				return false;
		} else if (!uiValue.equals(other.uiValue))
			return false;
		return true;
	}


    @Override
    public int compareTo(MetadataValidValue o) {
        return this.displayOrder.compareTo(o.displayOrder);
    }
}
