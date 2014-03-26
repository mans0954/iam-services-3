package org.openiam.idm.srvc.org.dto;

import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;
import org.openiam.internationalization.Internationalized;
import org.openiam.internationalization.InternationalizedCollection;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganizationType", propOrder = {
	"name",
	"description",
	"parentTypes",
	"childTypes",
	"organizations",
	"displayNameMap",
	"displayName"
})
@DozerDTOCorrespondence(OrganizationTypeEntity.class)
@Internationalized
public class OrganizationType extends KeyDTO {

	private String name;
	private String description;
	private Set<OrganizationType> parentTypes;
	private Set<OrganizationType> childTypes;
	private Set<Organization> organizations;
	
	@InternationalizedCollection(referenceType="MetadataTypeEntity", targetField="displayName")
    private Map<String, LanguageMapping> displayNameMap;
	    
    private String displayName;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Set<OrganizationType> getParentTypes() {
		return parentTypes;
	}
	public void setParentTypes(Set<OrganizationType> parentTypes) {
		this.parentTypes = parentTypes;
	}
	public Set<OrganizationType> getChildTypes() {
		return childTypes;
	}
	public void setChildTypes(Set<OrganizationType> childTypes) {
		this.childTypes = childTypes;
	}
	
	public Set<Organization> getOrganizations() {
		return organizations;
	}
	public void setOrganizations(Set<Organization> organizations) {
		this.organizations = organizations;
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		OrganizationType other = (OrganizationType) obj;
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "OrganizationType [id=" + id + ", name=" + name
				+ ", description=" + description + "]";
	}
	
	
}
