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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.cat.domain.CategoryEntity;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.internationalization.Internationalized;
import org.openiam.internationalization.InternationalizedCollection;

@Entity
@Table(name = "METADATA_TYPE")
@Cacheable
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
    private boolean active;

    @Column(name = "SYNC_MANAGED_SYS")
    @Type(type = "yes_no")
    private boolean syncManagedSys;

    @Column(name = "IS_BINARY")
    @Type(type = "yes_no")
    private boolean binary;
    
    @Column(name = "IS_SENSITIVE")
    @Type(type = "yes_no")
    private boolean sensitive;
    
    @Column(name = "GROUPING", length = 100)
    private String grouping;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = { CascadeType.ALL })
    @JoinColumn(name = "TYPE_ID", referencedColumnName = "TYPE_ID")
    @MapKeyColumn(name = "ATTRIBUTE_NAME")
    @Fetch(FetchMode.SUBSELECT)
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
    private Set<CategoryEntity> categories = new HashSet<CategoryEntity>(0);
    
    @Transient
    @InternationalizedCollection(referenceType="MetadataTypeEntity", targetField="displayName")
    private Map<String, LanguageMappingEntity> displayNameMap;
    
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

    public boolean isActive() {
    	return active;
    }

    public void setActive(boolean active) {
    	this.active = active;
    }

    public boolean isSyncManagedSys() {
    	return syncManagedSys;
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

}
