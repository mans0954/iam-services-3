package org.openiam.base;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.internationalization.InternationalizedCollection;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractDisplayNameDTO", propOrder = {
	"displayNameMap",
	"displayName"
})
public class AbstractDisplayNameDTO extends KeyDTO {

	 @InternationalizedCollection(referenceType="MetadataTypeEntity", targetField="displayName")
	 private Map<String, LanguageMapping> displayNameMap;
	    
	 private String displayName;
	 

	 public Map<String, LanguageMapping> getDisplayNameMap() {
		 return displayNameMap;
	 }

	 public void setDisplayNameMap(Map<String, LanguageMapping> displayNameMap) {
		 this.displayNameMap = displayNameMap;
	 }

	 public String getDisplayName() {
		 return displayName;
	 }

	 public void setDisplayName(String displayName) {
		 this.displayName = displayName;
	 }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result
				+ ((displayNameMap == null) ? 0 : displayNameMap.hashCode());
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
		AbstractDisplayNameDTO other = (AbstractDisplayNameDTO) obj;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (displayNameMap == null) {
			if (other.displayNameMap != null)
				return false;
		} else if (!displayNameMap.equals(other.displayNameMap))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("AbstractDisplayNameDTO [displayNameMap=%s, displayName=%s, toString()=%s]",
						displayNameMap, displayName, super.toString());
	}
	 
	 
}
