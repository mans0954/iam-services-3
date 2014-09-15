package org.openiam.idm.srvc.meta.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;
import org.openiam.base.domain.KeyEntity;
import org.openiam.base.domain.AbstractKeyNameEntity;
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
@Internationalized
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "TYPE_ID")),
	@AttributeOverride(name = "name", column = @Column(name = "NAME", length = 100, nullable = true))
})
public class MetadataTypeEntity extends AbstractKeyNameEntity {

    private static final long serialVersionUID = 1L;

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
    
    @Enumerated(EnumType.STRING)
    @Column(name = "GROUPING", length = 100)
    private MetadataTypeGrouping grouping;

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
    @InternationalizedCollection(targetField="displayName")
    private Map<String, LanguageMappingEntity> displayNameMap;

    @OneToMany(mappedBy = "employeeType")
    @ContainedIn
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
		int result = super.hashCode();
		result = prime * result + (active ? 1231 : 1237);
		result = prime * result + (binary ? 1231 : 1237);
		result = prime * result
				+ ((grouping == null) ? 0 : grouping.hashCode());
		result = prime * result + (sensitive ? 1231 : 1237);
		result = prime * result + (syncManagedSys ? 1231 : 1237);
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
		MetadataTypeEntity other = (MetadataTypeEntity) obj;
		if (active != other.active)
			return false;
		if (binary != other.binary)
			return false;
		if (grouping != other.grouping)
			return false;
		if (sensitive != other.sensitive)
			return false;
		if (syncManagedSys != other.syncManagedSys)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MetadataTypeEntity [active=" + active + ", syncManagedSys="
				+ syncManagedSys + ", binary=" + binary + ", sensitive="
				+ sensitive + ", grouping=" + grouping + "]";
	}

    
}
