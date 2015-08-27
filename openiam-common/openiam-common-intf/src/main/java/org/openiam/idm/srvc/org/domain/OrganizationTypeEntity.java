package org.openiam.idm.srvc.org.domain;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.org.dto.OrganizationType;
import org.openiam.internationalization.Internationalized;
import org.openiam.internationalization.InternationalizedCollection;

@Entity
@Table(name="ORGANIZATION_TYPE")
@DozerDTOCorrespondence(OrganizationType.class)
@AttributeOverride(name = "id", column = @Column(name = "ORG_TYPE_ID"))
@Internationalized
public class OrganizationTypeEntity extends KeyEntity {
	
	@Column(name="NAME", length=100, nullable = false)
	private String name;
	
	@Column(name="DESCRIPTION", length=100, nullable = true)
	private String description;
	
	@ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.LAZY)
    @JoinTable(name="ORG_TYPE_VALID_MEMBERSHIP",
        joinColumns={@JoinColumn(name="MEMBER_ORG_TYPE_ID")},
        inverseJoinColumns={@JoinColumn(name="ORG_TYPE_ID")})
//    @Fetch(FetchMode.SUBSELECT)
    private Set<OrganizationTypeEntity> parentTypes;
    
	@ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.LAZY)
    @JoinTable(name="ORG_TYPE_VALID_MEMBERSHIP",
        joinColumns={@JoinColumn(name="ORG_TYPE_ID")},
        inverseJoinColumns={@JoinColumn(name="MEMBER_ORG_TYPE_ID")})
//    @Fetch(FetchMode.SUBSELECT)
    private Set<OrganizationTypeEntity> childTypes;
	
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "organizationType", fetch = FetchType.LAZY)
	private Set<OrganizationEntity> organizations;
	
	@Transient
	@InternationalizedCollection(targetField="displayName")
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

	public Set<OrganizationTypeEntity> getParentTypes() {
		return parentTypes;
	}

	public void setParentTypes(Set<OrganizationTypeEntity> parentTypes) {
		this.parentTypes = parentTypes;
	}
	
	public boolean hasChildType(final String typeId) {
		boolean retVal = false;
		if(childTypes != null) {
			for(final OrganizationTypeEntity type : childTypes) {
				if(StringUtils.equals(type.getId(), typeId)) {
					retVal = true;
					break;
				}
			}
		}
		return retVal;
	}

	public Set<OrganizationTypeEntity> getChildTypes() {
		return childTypes;
	}

	public void setChildTypes(Set<OrganizationTypeEntity> childTypes) {
		this.childTypes = childTypes;
	}

	public Set<OrganizationEntity> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(Set<OrganizationEntity> organizations) {
		this.organizations = organizations;
	}
	
	public boolean containsChild(final String childId) {
		boolean retVal = false;
		if(StringUtils.isBlank(childId)) {
			if(childTypes != null) {
				for(final Iterator<OrganizationTypeEntity> it = childTypes.iterator(); it.hasNext();) {
					final OrganizationTypeEntity entity = it.next();
					if(entity != null) {
						if(StringUtils.equals(entity.getId(), childId)) {
							retVal = true;
							break;
						}
					}
				}
			}
		}
		return retVal;
	}
	
	public void removeChildType(final String childId) {
		if(StringUtils.isNotBlank(childId)) {
			if(childTypes != null) {
				for(final Iterator<OrganizationTypeEntity> it = childTypes.iterator(); it.hasNext();) {
					final OrganizationTypeEntity entity = it.next();
					if(entity != null) {
						if(StringUtils.equals(entity.getId(), childId)) {
							it.remove();
							break;
						}
					}
				}
			}
		}
	}
	
	public void addChildType(final OrganizationTypeEntity child) {
		if(child != null) {
			if(this.childTypes == null) {
				this.childTypes = new HashSet<OrganizationTypeEntity>();
			}
			boolean contains = false;
			for(final OrganizationTypeEntity entity : this.childTypes) {
				if(StringUtils.equals(entity.getId(), child.getId())) {
					contains = true;
				}
			}
			
			if(!contains) {
				this.childTypes.add(child);
			}
		}
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
		OrganizationTypeEntity other = (OrganizationTypeEntity) obj;
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
		return "OrganizationTypeEntity [id=" + id + ", name=" + name
				+ ", description=" + description + "]";
	}
	
	
}
