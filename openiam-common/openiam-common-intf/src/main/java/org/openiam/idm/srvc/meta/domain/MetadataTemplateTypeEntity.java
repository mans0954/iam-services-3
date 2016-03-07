package org.openiam.idm.srvc.meta.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.dto.MetadataTemplateType;

@Entity
@Table(name = "UI_TEMPLATE_TYPE")
@DozerDTOCorrespondence(MetadataTemplateType.class)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MetadataTemplateTypeEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "TEMPLATE_TYPE_ID", length = 32)
    private String id;

    @Column(name = "NAME", length = 100, nullable = false)
    private String name;

    @Column(name = "DESCRIPTION", length = 200, nullable = true)
    private String description;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "templateType", fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<MetadataElementPageTemplateEntity> templates;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "templateType", fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<MetadataTemplateTypeFieldEntity> fields;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Set<MetadataElementPageTemplateEntity> getTemplates() {
        return templates;
    }

    public void setTemplates(Set<MetadataElementPageTemplateEntity> templates) {
        this.templates = templates;
    }

    public MetadataTemplateTypeFieldEntity getField(final String id) {
        MetadataTemplateTypeFieldEntity retVal = null;
        if (this.fields != null) {
            for (final MetadataTemplateTypeFieldEntity entity : fields) {
                if (StringUtils.equals(id, entity.getId())) {
                    retVal = entity;
                    break;
                }
            }
        }
        return retVal;
    }

    public Set<MetadataTemplateTypeFieldEntity> getFields() {
        return fields;
    }

    public void setFields(Set<MetadataTemplateTypeFieldEntity> fields) {
        this.fields = fields;
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
        MetadataTemplateTypeEntity other = (MetadataTemplateTypeEntity) obj;
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
        return "MetadataTemplateTypeEntity [id=" + id + ", name=" + name
                + ", description=" + description + "]";
    }


}
