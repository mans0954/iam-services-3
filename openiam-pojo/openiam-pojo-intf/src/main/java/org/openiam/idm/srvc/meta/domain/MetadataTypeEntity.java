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
import org.openiam.idm.srvc.cat.dto.Category;
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
    private Set<Category> categories = new HashSet<Category>(0);


    public Map<String, MetadataElementEntity> getElementAttributes() {
        return elementAttributes;
    }

    public void setElementAttributes(
            Map<String, MetadataElementEntity> elementAttributes) {
        this.elementAttributes = elementAttributes;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
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
}
