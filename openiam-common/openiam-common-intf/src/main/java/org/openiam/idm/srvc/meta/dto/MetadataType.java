package org.openiam.idm.srvc.meta.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.KeyDTO;
import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.cat.dto.Category;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.internationalization.Internationalized;
import org.openiam.internationalization.InternationalizedCollection;

/**
 * <code>MetadataType</code> represents a metdata type instance.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataType", propOrder = {
	"active", 
	"syncManagedSys",
	"elementAttributes", 
	"categories", 
	"grouping",
	"binary",
	"displayNameMap",
	"displayName",
	"sensitive",
	"usedForSMSOTP"
})
@DozerDTOCorrespondence(MetadataTypeEntity.class)
@Internationalized
public class MetadataType extends KeyNameDTO {
    private boolean active;
    private boolean syncManagedSys;
    private MetadataTypeGrouping grouping;
    private boolean binary;
    private boolean sensitive;
    private boolean usedForSMSOTP;

    protected Map<String, MetadataElement> elementAttributes = new HashMap<String, MetadataElement>(0);
    protected Set<Category> categories = new HashSet<Category>(0);
    
    @InternationalizedCollection(targetField="displayName")
    private Map<String, LanguageMapping> displayNameMap;
	    
    private String displayName;
    
    public MetadataType() {
    	super();
    }

    public Map<String, MetadataElement> getElementAttributes() {
    	return this.elementAttributes;
    }

    public void setElementAttributes(Map<String, MetadataElement> elementAttributes) {
    	this.elementAttributes = elementAttributes;
    }

    public Set<Category> getCategories() {
    	return categories;
    }

    public void setCategories(Set<Category> categories) {
    	this.categories = categories;
    }

    public void setActive(boolean active) {
    	this.active = active;
    }

    public void setSyncManagedSys(boolean syncManagedSys) {
    	this.syncManagedSys = syncManagedSys;
    }
    
    

    public MetadataTypeGrouping getGrouping() {
		return grouping;
	}

	public void setGrouping(MetadataTypeGrouping grouping) {
		this.grouping = grouping;
	}

	public boolean getActive() {
    	return active;
    }

    public boolean getSyncManagedSys() {
    	return syncManagedSys;
    }

	public boolean isBinary() {
		return binary;
	}

	public void setBinary(boolean binary) {
		this.binary = binary;
	}
	
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
	 
	public boolean isSensitive() {
		return sensitive;
	}

	public void setSensitive(boolean sensitive) {
		this.sensitive = sensitive;
	}
	
	public boolean isUsedForSMSOTP() {
		return usedForSMSOTP;
	}

	public void setUsedForSMSOTP(boolean usedForSMSOTP) {
		this.usedForSMSOTP = usedForSMSOTP;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (active ? 1231 : 1237);
		result = prime * result + (binary ? 1231 : 1237);
		result = prime * result
				+ ((grouping == null) ? 0 : grouping.hashCode());
		result = prime * result + (sensitive ? 1231 : 1237);
		result = prime * result + (syncManagedSys ? 1231 : 1237);
		result = prime * result + (usedForSMSOTP ? 1231 : 1237);
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
		MetadataType other = (MetadataType) obj;
		if (active != other.active)
			return false;
		if (binary != other.binary)
			return false;
		if (grouping != other.grouping)
			return false;
		if (sensitive != other.sensitive)
			return false;
		if (syncManagedSys != other.syncManagedSys)
			return false;
		if (usedForSMSOTP != other.usedForSMSOTP)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MetadataType [active=" + active + ", syncManagedSys="
				+ syncManagedSys + ", grouping=" + grouping + ", binary="
				+ binary + ", sensitive=" + sensitive + "]";
	}

	
}
