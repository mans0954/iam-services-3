package org.openiam.base.domain;

import java.util.Map;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.internationalization.InternationalizedCollection;

@MappedSuperclass
public class AbstractDisplayNameEntity extends KeyEntity {

	@Transient
    @InternationalizedCollection(referenceType="MetadataTypeEntity", targetField="displayName")
    private Map<String, LanguageMappingEntity> displayNameMap;
    
    @Transient
    private String displayName;


    public Map<String, LanguageMappingEntity> getDisplayNameMap() {
		return displayNameMap;
	}

	public void setDisplayNameMap(Map<String, LanguageMappingEntity> displayNameMap) {
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
		AbstractDisplayNameEntity other = (AbstractDisplayNameEntity) obj;
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
				.format("AbstractDisplayNameEntity [displayNameMap=%s, displayName=%s, toString()=%s]",
						displayNameMap, displayName, super.toString());
	}

	
}
