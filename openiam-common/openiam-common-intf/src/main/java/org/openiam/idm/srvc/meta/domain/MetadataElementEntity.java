package org.openiam.idm.srvc.meta.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.AttributeOverride;
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
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.internationalization.Internationalized;
import org.openiam.internationalization.InternationalizedCollection;

@Entity
@Table(name = "METADATA_ELEMENT")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(MetadataElement.class)
@AttributeOverride(name = "id", column = @Column(name = "METADATA_ID"))
@Internationalized
public class MetadataElementEntity extends KeyEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "DESCRIPTION", length = 40)
    private String description;
    
    @Column(name = "ATTRIBUTE_NAME", length = 100)
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
    @JoinColumn(name = "TYPE_ID", insertable=true, updatable=true)
    @Internationalized
    private MetadataTypeEntity metadataType;
    
	@Column(name = "IS_PUBLIC", nullable = false)
	@Type(type = "yes_no")
	private boolean isPublic = true;
    
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="RESOURCE_ID", referencedColumnName = "RESOURCE_ID", insertable = true, updatable = false)
	private ResourceEntity resource;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "metadataElement", fetch = FetchType.LAZY)
    private Set<MetadataElementPageTemplateXrefEntity> templateSet;
    
    //@OneToMany(cascade={}, fetch = FetchType.LAZY)
    //@JoinColumn(name = "REFERENCE_ID", referencedColumnName="METADATA_ID")
    //@Where(clause="REFERENCE_TYPE='MetadataElementEntity'")
    //@MapKey(name = "languageId")
    //@Fetch(FetchMode.SUBSELECT)
    @Transient
    @InternationalizedCollection(targetField="displayName")
    private Map<String, LanguageMappingEntity> languageMap;
    
    @Transient
    private String displayName;
    
    @Internationalized
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.REMOVE, CascadeType.DETACH}, mappedBy = "entity", fetch = FetchType.LAZY)
    private Set<MetadataValidValueEntity> validValues;
    
    @Column(name="STATIC_DEFAULT_VALUE", length=4096)
    private String staticDefaultValue;
    
    //@OneToMany(cascade={}, fetch = FetchType.LAZY)
    //@JoinColumn(name = "REFERENCE_ID", referencedColumnName="METADATA_ID")
    //@Where(clause="REFERENCE_TYPE='MetadataElementDefaultValues'")
    //@MapKey(name = "languageId")
    //@Fetch(FetchMode.SUBSELECT)
    @Transient
    @InternationalizedCollection(targetField="defaultValue")
	private Map<String, LanguageMappingEntity> defaultValueLanguageMap;
    
    @Transient
    private String defaultValue;
    
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "element", fetch = FetchType.LAZY)
    private Set<UserAttributeEntity> userAttributes;
    
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "element", fetch = FetchType.LAZY)
    private Set<OrganizationAttributeEntity> organizationAttributes;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "element", fetch = FetchType.LAZY)
    private Set<GroupAttributeEntity> groupAttributes;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "element", fetch = FetchType.LAZY)
    private Set<ResourcePropEntity> resourceAttributes;
    
	public String getDisplayName() {
		return displayName;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
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

	public boolean getSelfEditable() {
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

	public Set<UserAttributeEntity> getUserAttributes() {
		return userAttributes;
	}

	public void setUserAttributes(Set<UserAttributeEntity> userAttributes) {
		this.userAttributes = userAttributes;
	}

	public Set<OrganizationAttributeEntity> getOrganizationAttributes() {
		return organizationAttributes;
	}

    public Set<GroupAttributeEntity> getGroupAttributes() {
        return groupAttributes;
    }

	public void setGroupAttributes(
			Set<GroupAttributeEntity> groupAttributes) {
		this.groupAttributes = groupAttributes;
	}
	
    public Set<ResourcePropEntity> getResourceAttributes() {
		return resourceAttributes;
	}

	public void setResourceAttributes(Set<ResourcePropEntity> resourceAttributes) {
		this.resourceAttributes = resourceAttributes;
	}

	public void setOrganizationAttributes(
            Set<OrganizationAttributeEntity> organizationAttributes) {
        this.organizationAttributes = organizationAttributes;
    }

	public boolean getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetadataElementEntity that = (MetadataElementEntity) o;

        if (attributeName != null ? !attributeName.equals(that.attributeName) : that.attributeName != null)
            return false;
        if (dataType != null ? !dataType.equals(that.dataType) : that.dataType != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (staticDefaultValue != null ? !staticDefaultValue.equals(that.staticDefaultValue) : that.staticDefaultValue != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (attributeName != null ? attributeName.hashCode() : 0);
        result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
        result = 31 * result + (staticDefaultValue != null ? staticDefaultValue.hashCode() : 0);
        return result;
    }
}
