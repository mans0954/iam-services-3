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
        "description",
        "active",
        "syncManagedSys",
        "elementAttributes",
        "categories",
        "grouping",
        "binary",
        "displayNameMap",
        "displayName",
        "sensitive",
        "languageMappings"
})
@DozerDTOCorrespondence(MetadataTypeEntity.class)
@Internationalized
public class MetadataType extends KeyDTO {

    private String description;

    private Boolean active = Boolean.FALSE;
    private boolean syncManagedSys;

    private MetadataTypeGrouping grouping;

    private boolean binary;

    private boolean sensitive;

    protected Map<String, MetadataElement> elementAttributes = new HashMap<String, MetadataElement>(0);
    protected Set<Category> categories = new HashSet<Category>(0);

    @InternationalizedCollection(targetField = "displayName")
    private Map<String, LanguageMapping> displayNameMap;

    private String displayName;

    private Set<LanguageMapping> languageMappings=new HashSet<LanguageMapping>(0);;

    public MetadataType() {
        super();
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public void setActive(Boolean active) {
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

    public Boolean getActive() {
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

    public Set<LanguageMapping> getLanguageMappings() {
        return languageMappings;
    }

    public void setLanguageMappings(Set<LanguageMapping> languageMappings) {
        this.languageMappings = languageMappings;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (active ? 1231 : 1237);
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result
                + ((grouping == null) ? 0 : grouping.hashCode());
        result = prime * result + (binary ? 1231 : 1237);
        result = prime * result
                + ((id == null) ? 0 : id.hashCode());
        result = prime * result + (syncManagedSys ? 1231 : 1237);
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
        MetadataType other = (MetadataType) obj;
        if (active != other.active)
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (grouping == null) {
            if (other.grouping != null)
                return false;
        } else if (!grouping.equals(other.grouping))
            return false;
        if (binary != other.binary)
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (syncManagedSys != other.syncManagedSys)
            return false;
        return true;
    }

}
