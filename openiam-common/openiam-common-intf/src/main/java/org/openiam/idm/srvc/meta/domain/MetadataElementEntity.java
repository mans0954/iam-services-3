package org.openiam.idm.srvc.meta.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.res.domain.ResourceEntity;

@Entity
@Table(name = "METADATA_ELEMENT")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(MetadataElement.class)
public class MetadataElementEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "METADATA_ID", length = 32)
    private String id;

    @Column(name = "DESCRIPTION", length = 40)
    private String description;
    
    @Column(name = "ATTRIBUTE_NAME", length = 50)
    private String attributeName;

    @Column(name = "DATA_TYPE", length = 20)
    private String dataType;

    @Column(name = "AUDITABLE")
    @Type(type = "yes_no")
    private boolean auditable = true;

    @Column(name = "REQUIRED")
    @Type(type = "yes_no")
    private boolean required;

    @Column(name = "SELF_EDITABLE")
    @Type(type = "yes_no")
    private boolean selfEditable;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID")
    private MetadataTypeEntity metadataType;
    
//    @ManyToOne
//    @JoinColumn(name = "TEMPLATE_ID")
//    private MetadataElementPageTemplateEntity template;
    
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = false, updatable = false)
	private ResourceEntity resource;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "metadataElement", fetch = FetchType.LAZY)
    private Set<MetadataElementPageTemplateXrefEntity> templateSet;
    
    @OneToMany(cascade={CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinColumn(name = "REFERENCE_ID", insertable = true, updatable = true)
	@Where(clause="REFERENCE_TYPE='MetadataElementEntity'")
    @MapKey(name = "languageId")
    private Map<String, LanguageMappingEntity> languageMap;
    
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "entity", fetch = FetchType.LAZY)
    private Set<MetadataValidValueEntity> validValues;
    
    @Column(name="STATIC_DEFAULT_VALUE", length=400)
    private String staticDefaultValue;
    
    @OneToMany(cascade={CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinColumn(name = "REFERENCE_ID", insertable = true, updatable = true)
	@Where(clause="REFERENCE_TYPE='MetadataElementDefaultValues'")
    @MapKey(name = "languageId")
	private Map<String, LanguageMappingEntity> defaultValueLanguageMap;
    

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
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

	public MetadataTypeEntity getMetadataType() {
		return metadataType;
	}

	public void setMetadataType(MetadataTypeEntity metadataType) {
		this.metadataType = metadataType;
	}

//	public MetadataElementPageTemplateEntity getTemplate() {
//		return template;
//	}
//
//	public void setTemplate(MetadataElementPageTemplateEntity template) {
//		this.template = template;
//	}


    public Set<MetadataElementPageTemplateXrefEntity> getTemplateSet() {
        return templateSet;
    }

    public void setTemplateSet(Set<MetadataElementPageTemplateXrefEntity> templateSet) {
        this.templateSet = templateSet;
    }
    
    public void addTemplate(final MetadataElementPageTemplateXrefEntity xref) {
    	if(this.templateSet == null) {
    		this.templateSet = new HashSet<MetadataElementPageTemplateXrefEntity>();
    	}
    	
    	//TODO:  is this right?  displayOrder is taken into account in equals()
    	if(!templateSet.contains(xref)) {
    		templateSet.add(xref);
    	}
    }

	public Map<String, LanguageMappingEntity> getLanguageMap() {
		return languageMap;
	}
	
	public void addLanguageMap(final Map<String, LanguageMappingEntity> languageMap) {
		if(languageMap != null) {
			if(this.languageMap == null) {
				this.languageMap = new HashMap<String, LanguageMappingEntity>();
			}
			this.languageMap.putAll(languageMap);
		}
	}

	public void setLanguageMap(Map<String, LanguageMappingEntity> languageMap) {
		this.languageMap = languageMap;
	}

	public Set<MetadataValidValueEntity> getValidValues() {
		return validValues;
	}

	public void setValidValues(Set<MetadataValidValueEntity> validValues) {
		this.validValues = validValues;
	}

	public String getStaticDefaultValue() {
		return staticDefaultValue;
	}

	public void setStaticDefaultValue(String staticDefaultValue) {
		this.staticDefaultValue = staticDefaultValue;
	}

	
	
	public Map<String, LanguageMappingEntity> getDefaultValueLanguageMap() {
		return defaultValueLanguageMap;
	}
	
	public void addDefaultValueLanguageMap(final Map<String, LanguageMappingEntity> defaultValueLanguageMap) {
		if(defaultValueLanguageMap != null) {
			if(this.defaultValueLanguageMap == null) {
				this.defaultValueLanguageMap = new HashMap<String, LanguageMappingEntity>();
			}
			this.defaultValueLanguageMap.putAll(defaultValueLanguageMap);
		}
	}

	public void setDefaultValueLanguageMap(
			Map<String, LanguageMappingEntity> defaultValueLanguageMap) {
		this.defaultValueLanguageMap = defaultValueLanguageMap;
	}

	public ResourceEntity getResource() {
		return resource;
	}

	public void setResource(ResourceEntity resource) {
		this.resource = resource;
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
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((metadataType == null) ? 0 : metadataType.hashCode());
		result = prime * result + (required ? 1231 : 1237);
		result = prime * result + (selfEditable ? 1231 : 1237);
		result = prime
				* result
				+ ((staticDefaultValue == null) ? 0 : staticDefaultValue
						.hashCode());
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
		MetadataElementEntity other = (MetadataElementEntity) obj;
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
		if (metadataType == null) {
			if (other.metadataType != null)
				return false;
		} else if (!metadataType.equals(other.metadataType))
			return false;
		if (required != other.required)
			return false;
		if (selfEditable != other.selfEditable)
			return false;
		if (staticDefaultValue == null) {
			if (other.staticDefaultValue != null)
				return false;
		} else if (!staticDefaultValue.equals(other.staticDefaultValue))
			return false;
		return true;
	}

	
}
