package org.openiam.idm.srvc.meta.domain;

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
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.cat.domain.CategoryEntity;
import org.openiam.idm.srvc.meta.dto.MetadataType;

@Entity
@Table(name = "METADATA_TYPE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(MetadataType.class)
public class MetadataTypeEntity implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "TYPE_ID", length = 20)
    private String metadataTypeId;

    @Column(name = "DESCRIPTION", length = 40)
    private String description;

    @Column(name = "ACTIVE")
    private int active;

    @Column(name = "SYNC_MANAGED_SYS")
    private int syncManagedSys = 0;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "metadataElementId", fetch = FetchType.LAZY)
    @MapKey(name = "attributeName")
    private Map<String, MetadataElementEntity> elementAttributes = new HashMap<String, MetadataElementEntity>(
            0);

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID", insertable = false, updatable = false)
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

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getSyncManagedSys() {
        return syncManagedSys;
    }

    public void setSyncManagedSys(int syncManagedSys) {
        this.syncManagedSys = syncManagedSys;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + active;
        result = prime * result
                + ((categories == null) ? 0 : categories.hashCode());
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime
                * result
                + ((elementAttributes == null) ? 0 : elementAttributes
                        .hashCode());
        result = prime * result
                + ((metadataTypeId == null) ? 0 : metadataTypeId.hashCode());
        result = prime * result + syncManagedSys;
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
