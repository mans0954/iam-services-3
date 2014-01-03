package org.openiam.idm.srvc.meta.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.cat.dto.Category;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;

// Generated Nov 4, 2008 12:11:29 AM by Hibernate Tools 3.2.2.GA

/**
 * <code>MetadataType</code> represents a metdata type instance.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataType", propOrder = { "metadataTypeId", "description", "active", "syncManagedSys",
	"elementAttributes", "categories", "grouping" })
@DozerDTOCorrespondence(MetadataTypeEntity.class)
public class MetadataType implements Serializable {

    private String metadataTypeId;
    private String description;

    private boolean active;
    private boolean syncManagedSys;

    private String grouping;

    protected Map<String, MetadataElement> elementAttributes = new HashMap<String, MetadataElement>(0);
    protected Set<Category> categories = new HashSet<Category>(0);

    public MetadataType() {
    }

    public MetadataType(String metadataTypeId) {
	this.metadataTypeId = metadataTypeId;
    }

    public MetadataType(String metadataTypeId, String description) {
	this.metadataTypeId = metadataTypeId;
	this.description = description;
    }

    public String getMetadataTypeId() {
	return this.metadataTypeId;
    }

    public void setMetadataTypeId(String metadataTypeId) {
	this.metadataTypeId = metadataTypeId;
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

    public void setActive(boolean active) {
	this.active = active;
    }

    public void setSyncManagedSys(boolean syncManagedSys) {
	this.syncManagedSys = syncManagedSys;
    }

    public String getGrouping() {
	return grouping;
    }

    public void setGrouping(String grouping) {
	this.grouping = grouping;
    }

    public boolean getActive() {
	return active;
    }

    public boolean getSyncManagedSys() {
	return syncManagedSys;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (active ? 1231 : 1237);
	result = prime * result + ((categories == null) ? 0 : categories.hashCode());
	result = prime * result + ((description == null) ? 0 : description.hashCode());
	result = prime * result + ((elementAttributes == null) ? 0 : elementAttributes.hashCode());
	result = prime * result + ((grouping == null) ? 0 : grouping.hashCode());
	result = prime * result + ((metadataTypeId == null) ? 0 : metadataTypeId.hashCode());
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
	if (categories == null) {
	    if (other.categories != null)
		return false;
	} else if (!categories.equals(other.categories))
	    return false;
	if (description == null) {
	    if (other.description != null)
		return false;
	} else if (!description.equals(other.description))
	    return false;
	if (elementAttributes == null) {
	    if (other.elementAttributes != null)
		return false;
	} else if (!elementAttributes.equals(other.elementAttributes))
	    return false;
	if (grouping == null) {
	    if (other.grouping != null)
		return false;
	} else if (!grouping.equals(other.grouping))
	    return false;
	if (metadataTypeId == null) {
	    if (other.metadataTypeId != null)
		return false;
	} else if (!metadataTypeId.equals(other.metadataTypeId))
	    return false;
	if (syncManagedSys != other.syncManagedSys)
	    return false;
	return true;
    }

}
