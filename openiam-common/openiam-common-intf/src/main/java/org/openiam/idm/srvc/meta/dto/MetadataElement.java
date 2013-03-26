package org.openiam.idm.srvc.meta.dto;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Set;

// Generated Nov 4, 2008 12:11:29 AM by Hibernate Tools 3.2.2.GA

/**
 * <code>MetadataElement</code> represents an attribute of MetadataType.
 * MetadataElement also contains parameters that define validation constraints.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataElement", 
	propOrder = { 
		"id",
        "metadataTypeId",
        "description",
        "auditable",
        "required", 
        "selfEditable",
        "attributeName",
        "templateSet",
        "staticDefaultValue",
        "languageSet",
        "validValues",
        "dataType",
        "defaultValueLanguageSet"
})
@DozerDTOCorrespondence(MetadataElementEntity.class)
public class MetadataElement implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String metadataTypeId;
    private String description;
    private String dataType;
    private boolean auditable = true;
    private boolean required;
    private String attributeName;
    private boolean selfEditable;
    private Set<MetadataElementPageTemplateXref> templateSet;
    private Set<LanguageMapping> languageSet;
    private Set<MetadataValidValue> validValues;
    private String staticDefaultValue;
    private Set<LanguageMapping> defaultValueLanguageSet;

    public MetadataElement() {
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMetadataTypeId() {
		return metadataTypeId;
	}

	public void setMetadataTypeId(String metadataTypeId) {
		this.metadataTypeId = metadataTypeId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public boolean isAuditable() {
		return auditable;
	}

	public void setAuditable(boolean auditable) {
		this.auditable = auditable;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isSelfEditable() {
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

    public Set<LanguageMapping> getLanguageSet() {
		return languageSet;
	}

	public void setLanguageSet(Set<LanguageMapping> languageSet) {
		this.languageSet = languageSet;
	}

	public Set<MetadataValidValue> getValidValues() {
		return validValues;
	}

	public void setValidValues(Set<MetadataValidValue> validValues) {
		this.validValues = validValues;
	}

	public String getStaticDefaultValue() {
		return staticDefaultValue;
	}

	public void setStaticDefaultValue(String staticDefaultValue) {
		this.staticDefaultValue = staticDefaultValue;
	}

	public Set<LanguageMapping> getDefaultValueLanguageSet() {
		return defaultValueLanguageSet;
	}

	public void setDefaultValueLanguageSet(
			Set<LanguageMapping> defaultValueLanguageSet) {
		this.defaultValueLanguageSet = defaultValueLanguageSet;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attributeName == null) ? 0 : attributeName.hashCode());
		result = prime * result + (auditable ? 1231 : 1237);
		result = prime * result
				+ ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result
				+ ((staticDefaultValue == null) ? 0 : staticDefaultValue.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((metadataTypeId == null) ? 0 : metadataTypeId.hashCode());
		result = prime * result + (required ? 1231 : 1237);
		result = prime * result + (selfEditable ? 1231 : 1237);
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
		if (staticDefaultValue == null) {
			if (other.staticDefaultValue != null)
				return false;
		} else if (!staticDefaultValue.equals(other.staticDefaultValue))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (metadataTypeId == null) {
			if (other.metadataTypeId != null)
				return false;
		} else if (!metadataTypeId.equals(other.metadataTypeId))
			return false;
		if (required != other.required)
			return false;
		if (selfEditable != other.selfEditable)
			return false;
		return true;
	}
    
    
}
