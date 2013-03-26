package org.openiam.idm.srvc.meta.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.pk.MetadataElementPageTemplateXrefIdEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplateXref;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "METADATA_ELEMENT_PAGE_TEMPLATE_XREF")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(MetadataElementPageTemplateXref.class)
public class MetadataElementPageTemplateXrefEntity implements Serializable {

    @EmbeddedId
    private MetadataElementPageTemplateXrefIdEntity id;

    @Column(name="DISPLAY_ORDER", nullable = false)
    private Integer displayOrder;

    @ManyToOne
    @JoinColumn(name = "TEMPLATE_ID", insertable = false, updatable = false)
    private MetadataElementPageTemplateEntity template;

    @ManyToOne
    @JoinColumn(name = "METADATA_ELEMENT_ID", insertable = false, updatable = false)
    private MetadataElementEntity metadataElement;

    public MetadataElementPageTemplateXrefIdEntity getId() {
        return id;
    }

    public void setId(MetadataElementPageTemplateXrefIdEntity id) {
        this.id = id;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public MetadataElementPageTemplateEntity getTemplate() {
        return template;
    }

    public void setTemplate(MetadataElementPageTemplateEntity template) {
        this.template = template;
    }

    public MetadataElementEntity getMetadataElement() {
        return metadataElement;
    }

    public void setMetadataElement(MetadataElementEntity metadataElement) {
        this.metadataElement = metadataElement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MetadataElementPageTemplateXrefEntity that = (MetadataElementPageTemplateXrefEntity) o;

        if (displayOrder != null ? !displayOrder.equals(that.displayOrder) : that.displayOrder != null) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (displayOrder != null ? displayOrder.hashCode() : 0);
        return result;
    }
}
