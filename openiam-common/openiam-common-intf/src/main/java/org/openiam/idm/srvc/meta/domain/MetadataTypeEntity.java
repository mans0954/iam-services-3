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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.cat.domain.CategoryEntity;
import org.openiam.idm.srvc.meta.dto.MetadataType;

@Entity
@Table(name = "METADATA_TYPE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(MetadataType.class)
public class MetadataTypeEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "TYPE_ID", length = 32)
    private String metadataTypeId;

    @Column(name = "DESCRIPTION", length = 40)
    private String description;

    @Column(name = "ACTIVE")
    @Type(type = "yes_no")
    private Boolean active;

    @Column(name = "SYNC_MANAGED_SYS")
    @Type(type = "yes_no")
    private Boolean syncManagedSys;

    @Column(name = "GROUPING", length = 100)
    private String grouping;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = { CascadeType.ALL })
    @JoinColumn(name = "TYPE_ID", referencedColumnName = "TYPE_ID")
    @MapKeyColumn(name = "ATTRIBUTE_NAME")
    @Fetch(FetchMode.SUBSELECT)
    private Map<String, MetadataElementEntity> elementAttributes = new HashMap<String, MetadataElementEntity>(
            0);
    /*
     * @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch =
     * FetchType.LAZY)
     * 
     * @JoinColumn(name = "CATEGORY_ID", insertable = false, updatable = false)
     * 
     * @Fetch(FetchMode.SUBSELECT)
     */
    @ManyToMany(cascade = { CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH }, fetch = FetchType.LAZY)
    @JoinTable(name = "CATEGORY_TYPE", joinColumns = { @JoinColumn(name = "TYPE_ID") }, inverseJoinColumns = { @JoinColumn(name = "CATEGORY_ID") })
    @Fetch(FetchMode.SUBSELECT)
    private Set<CategoryEntity> categories = new HashSet<CategoryEntity>(0);

    public Map<String, MetadataElementEntity> getElementAttributes() {
        return elementAttributes;
    }

    public void setElementAttributes(
            Map<String, MetadataElementEntity> elementAttributes) {
        this.elementAttributes = elementAttributes;
    }

    public Set<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryEntity> categories) {
        this.categories = categories;
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

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean isSyncManagedSys() {
        return syncManagedSys;
    }

    public void setSyncManagedSys(Boolean syncManagedSys) {
        this.syncManagedSys = syncManagedSys;
    }

    public String getGrouping() {
        return grouping;
    }

    public void setGrouping(String grouping) {
        this.grouping = grouping;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((active == null) ? 0 : active.hashCode());
        result = prime * result
                + ((categories == null) ? 0 : categories.hashCode());
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime
                * result
                + ((elementAttributes == null) ? 0 : elementAttributes
                        .hashCode());
        result = prime * result
                + ((grouping == null) ? 0 : grouping.hashCode());
        result = prime * result
                + ((metadataTypeId == null) ? 0 : metadataTypeId.hashCode());
        result = prime * result
                + ((syncManagedSys == null) ? 0 : syncManagedSys.hashCode());
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
        if (active == null) {
            if (other.active != null)
                return false;
        } else if (!active.equals(other.active))
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
        if (syncManagedSys == null) {
            if (other.syncManagedSys != null)
                return false;
        } else if (!syncManagedSys.equals(other.syncManagedSys))
            return false;
        return true;
    }

}
