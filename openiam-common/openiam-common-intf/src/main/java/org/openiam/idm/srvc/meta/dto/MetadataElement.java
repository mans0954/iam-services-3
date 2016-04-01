package org.openiam.idm.srvc.meta.dto;

import org.openiam.base.KeyDTO;
import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.internationalization.Internationalized;
import org.openiam.internationalization.InternationalizedCollection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Generated Nov 4, 2008 12:11:29 AM by Hibernate Tools 3.2.2.GA

/**
 * <code>MetadataElement</code> represents an attribute of MetadataType.
 * MetadataElement also contains parameters that define validation constraints.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataElement", 
	propOrder = {
		"metadataTypeName",
        "metadataTypeId",
        "auditable",
        "required", 
        "selfEditable",
        "attributeName",
        "templateSet",
        "staticDefaultValue",
        "languageMap",
        "validValues",
        "dataType",
        "defaultValueLanguageMap",
        "resourceId",
        "userAttributes",
        "organizationAttributes",
        "isPublic",
        "displayName",
		"dataModelUrl"
})
@DozerDTOCorrespondence(MetadataElementEntity.class)
@Internationalized
public class MetadataElement extends KeyNameDTO {

    private static final long serialVersionUID = 1L;
    private String metadataTypeName;
    private String metadataTypeId;
    private String dataType;
    private boolean auditable = true;
    private boolean required;
    private String attributeName;
    private boolean selfEditable;
    private Set<MetadataElementPageTemplateXref> templateSet;
    
    @InternationalizedCollection(targetField="displayName")
    private Map<String, LanguageMapping> languageMap;
    private Set<MetadataValidValue> validValues;
    private String staticDefaultValue;
    private Map<String, LanguageMapping> defaultValueLanguageMap;
    private Set<UserAttribute> userAttributes;
    private String resourceId;
    private boolean isPublic = true;
    private Set<OrganizationAttribute> organizationAttributes;
    private String displayName;
	private String dataModelUrl;

    public MetadataElement() {
    }

	public String getMetadataTypeId() {
		return metadataTypeId;
	}

	public void setMetadataTypeId(String metadataTypeId) {
		this.metadataTypeId = metadataTypeId;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public boolean getAuditable() {
		return auditable;
	}

	public void setAuditable(boolean auditable) {
		this.auditable = auditable;
	}

	public boolean getRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean getSelfEditable() {
		return selfEditable;
	}

	public void setSelfEditable(boolean selfEditable) {
		this.selfEditable = selfEditable;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

//	public MetadataElementPageTemplate getTemplate() {
//		return template;
//	}
//
//	public void setTemplate(MetadataElementPageTemplate template) {
//		this.template = template;
//	}


    public Set<MetadataElementPageTemplateXref> getTemplateSet() {
        return templateSet;
    }

    public void setTemplateSet(Set<MetadataElementPageTemplateXref> templateSet) {
        this.templateSet = templateSet;
    }

	public Map<String, LanguageMapping> getLanguageMap() {
		return languageMap;
	}

	public void setLanguageMap(Map<String, LanguageMapping> languageMap) {
		this.languageMap = languageMap;
	}
	
	public void addLanguage(final LanguageMapping mapping) {
		if(mapping != null) {
			if(this.languageMap == null) {
				this.languageMap = new HashMap<String, LanguageMapping>();
			}
			this.languageMap.put(mapping.getId(), mapping);
		}
	}

	public Set<MetadataValidValue> getValidValues() {
		return validValues;
	}

	public void setValidValues(Set<MetadataValidValue> validValues) {
		this.validValues = validValues;
	}
	
	public void addValidValue(final MetadataValidValue value) {
		if(value != null) {
			if(this.validValues == null) {
				this.validValues = new HashSet<MetadataValidValue>();
			}
			this.validValues.add(value);
		}
	}

	public String getStaticDefaultValue() {
		return staticDefaultValue;
	}

	public void setStaticDefaultValue(String staticDefaultValue) {
		this.staticDefaultValue = staticDefaultValue;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public Map<String, LanguageMapping> getDefaultValueLanguageMap() {
		return defaultValueLanguageMap;
	}
	
	public void addDefaultValue(final LanguageMapping mapping) {
		if(mapping != null) {
			if(this.defaultValueLanguageMap == null) {
				this.defaultValueLanguageMap = new HashMap<String, LanguageMapping>();
			}
			this.defaultValueLanguageMap.put(mapping.getId(), mapping);
		}
	}

	public void setDefaultValueLanguageMap(
			Map<String, LanguageMapping> defaultValueLanguageMap) {
		this.defaultValueLanguageMap = defaultValueLanguageMap;
	}
	
	public Set<UserAttribute> getUserAttributes() {
		return userAttributes;
	}

	public void setUserAttributes(Set<UserAttribute> userAttributes) {
		this.userAttributes = userAttributes;
	}
	
	public Set<OrganizationAttribute> getOrganizationAttributes() {
		return organizationAttributes;
	}

	public void setOrganizationAttributes(
			Set<OrganizationAttribute> organizationAttributes) {
		this.organizationAttributes = organizationAttributes;
	}

	public boolean getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getMetadataTypeName() {
		return metadataTypeName;
	}

	public void setMetadataTypeName(String metadataTypeName) {
		this.metadataTypeName = metadataTypeName;
	}

	public String getDataModelUrl() {
		return dataModelUrl;
	}

	public void setDataModelUrl(String dataModelUrl) {
		this.dataModelUrl = dataModelUrl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((attributeName == null) ? 0 : attributeName.hashCode());
		result = prime * result + (auditable ? 1231 : 1237);
		result = prime * result
				+ ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result + (isPublic ? 1231 : 1237);
		result = prime * result
				+ ((metadataTypeId == null) ? 0 : metadataTypeId.hashCode());
		result = prime
				* result
				+ ((metadataTypeName == null) ? 0 : metadataTypeName.hashCode());
		result = prime * result + (required ? 1231 : 1237);
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
		result = prime * result + (selfEditable ? 1231 : 1237);
		result = prime
				* result
				+ ((staticDefaultValue == null) ? 0 : staticDefaultValue
						.hashCode());
		result = prime * result + ((dataModelUrl == null) ?0 : dataModelUrl.hashCode());
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
		MetadataElement other = (MetadataElement) obj;
		if (attributeName == null) {
			if (other.attributeName != null)
				return false;
		} else if (!attributeName.equals(other.attributeName))
			return false;
		if (auditable != other.auditable)
			return false;
		if (dataType == null) {
			if (other.dataType != null)
				return false;
		} else if (!dataType.equals(other.dataType))
			return false;
		if (isPublic != other.isPublic)
			return false;
		if (metadataTypeId == null) {
			if (other.metadataTypeId != null)
				return false;
		} else if (!metadataTypeId.equals(other.metadataTypeId))
			return false;
		if (metadataTypeName == null) {
			if (other.metadataTypeName != null)
				return false;
		} else if (!metadataTypeName.equals(other.metadataTypeName))
			return false;
		if (required != other.required)
			return false;
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		if (selfEditable != other.selfEditable)
			return false;
		if (staticDefaultValue == null) {
			if (other.staticDefaultValue != null)
				return false;
		} else if (!staticDefaultValue.equals(other.staticDefaultValue))
			return false;
		if (dataModelUrl == null) {
			if (other.dataModelUrl != null)
				return false;
		} else if (!dataModelUrl.equals(other.dataModelUrl))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MetadataElement [metadataTypeName=" + metadataTypeName
				+ ", metadataTypeId=" + metadataTypeId + ", dataType="
				+ dataType + ", auditable=" + auditable + ", required="
				+ required + ", attributeName=" + attributeName
				+ ", selfEditable=" + selfEditable + ", staticDefaultValue="
				+ staticDefaultValue + ", resourceId=" + resourceId
				+ ", isPublic=" + isPublic + ", displayName=" + displayName
				+ "]";
	}

	
}
