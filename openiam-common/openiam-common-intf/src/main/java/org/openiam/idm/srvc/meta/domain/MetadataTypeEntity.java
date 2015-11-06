package org.openiam.idm.srvc.meta.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.cat.domain.CategoryEntity;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.internationalization.Internationalized;
import org.openiam.internationalization.InternationalizedCollection;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "METADATA_TYPE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(MetadataType.class)
@AttributeOverride(name = "id", column = @Column(name = "TYPE_ID"))
@Internationalized
public class MetadataTypeEntity extends KeyEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "DESCRIPTION", length = 40)
    private String description;

    @Column(name = "ACTIVE")
    @Type(type = "yes_no")
    private Boolean active = Boolean.FALSE;

    @Column(name = "SYNC_MANAGED_SYS")
    @Type(type = "yes_no")
    private boolean syncManagedSys;

    @Column(name = "IS_BINARY")
    @Type(type = "yes_no")
    private boolean binary;
    
    @Column(name = "IS_SENSITIVE")
    @Type(type = "yes_no")
    private boolean sensitive;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "GROUPING", length = 100)
    private MetadataTypeGrouping grouping;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = { CascadeType.ALL })
    @JoinColumn(name = "TYPE_ID", referencedColumnName = "TYPE_ID")
    @MapKeyColumn(name = "ATTRIBUTE_NAME")
    @Fetch(FetchMode.SUBSELECT)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Map<String, MetadataElementEntity> elementAttributes = new HashMap<String, MetadataElementEntity>(0);
    /*
     * @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch =
     * FetchType.LAZY)
     * 
     * @JoinColumn(name = "CATEGORY_ID", insertable = false, updatable = false)
     * 
     * @Fetch(FetchMode.SUBSELECT)
     */
    @ManyToMany(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH }, fetch = FetchType.LAZY)
    @JoinTable(name = "CATEGORY_TYPE", joinColumns = { @JoinColumn(name = "TYPE_ID") }, inverseJoinColumns = { @JoinColumn(name = "CATEGORY_ID") })
    @Fetch(FetchMode.SUBSELECT)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<CategoryEntity> categories = new HashSet<CategoryEntity>(0);
    
    @Transient
    @InternationalizedCollection(targetField="displayName")
    private Map<String, LanguageMappingEntity> displayNameMap;

    @OneToMany(mappedBy = "employeeType")
    @ContainedIn
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<UserEntity> userEntitySet;
    
    @Transient
    private String displayName;

    public MetadataTypeEntity() {
    	super();
    }
    
	public Map<String, MetadataElementEntity> getElementAttributes() {
    	return elementAttributes;
    }

    public void setElementAttributes(Map<String, MetadataElementEntity> elementAttributes) {
    	this.elementAttributes = elementAttributes;
    }

    public Set<CategoryEntity> getCategories() {
    	return categories;
    }

    public void setCategories(Set<CategoryEntity> categories) {
    	this.categories = categories;
    }
    
    public void addCategory(final CategoryEntity entity) {
    	if(this.categories == null) {
    		this.categories = new HashSet<>();
    	}
    	this.categories.add(entity);
    }

    public String getDescription() {
    	return description;
    }

    public void setDescription(String description) {
    	this.description = description;
    }

    public Boolean getActive() {
    	return active;
    }

    public void setActive(Boolean active) {
    	this.active = active;
    }

    public boolean isSyncManagedSys() {
    	return syncManagedSys;
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
    
	public boolean isBinary() {
		return binary;
	}

	public void setBinary(boolean binary) {
		this.binary = binary;
	}
	
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

	public boolean isSensitive() {
		return sensitive;
	}

	public void setSensitive(boolean sensitive) {
		this.sensitive = sensitive;
	}

    public Set<UserEntity> getUserEntitySet() {
        return userEntitySet;
    }

    public void setUserEntitySet(Set<UserEntity> userEntitySet) {
        this.userEntitySet = userEntitySet;
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
		MetadataTypeEntity other = (MetadataTypeEntity) obj;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("MetadataTypeEntity");
        sb.append("{description='").append(description).append('\'');
        sb.append(", active=").append(active);
        sb.append(", syncManagedSys=").append(syncManagedSys);
        sb.append(", binary=").append(binary);
        sb.append(", sensitive=").append(sensitive);
        sb.append(", displayName='").append(displayName).append('\'');
        sb.append('}');
		sb.append("displayNameMap { ");
		if (displayNameMap != null) {
			for (String key : displayNameMap.keySet()) {
				LanguageMappingEntity lm = displayNameMap.get(key);
				if (lm != null) {
					sb.append(lm.toString());
				}
			}
		}
		sb.append(" }");
        return sb.toString();
    }
}
