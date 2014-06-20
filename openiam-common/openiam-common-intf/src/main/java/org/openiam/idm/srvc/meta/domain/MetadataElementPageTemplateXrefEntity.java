package org.openiam.idm.srvc.meta.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.pk.MetadataElementPageTemplateXrefIdEntity;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplateXref;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "PAGE_TEMPLATE_XREF")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(MetadataElementPageTemplateXref.class)
public class MetadataElementPageTemplateXrefEntity implements Serializable {

    @EmbeddedId
    private MetadataElementPageTemplateXrefIdEntity id;

    @Column(name="DISPLAY_ORDER", nullable = false)
    private Integer displayOrder;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.LAZY)
    @JoinColumn(name = "TEMPLATE_ID", insertable = false, updatable = false)
    private MetadataElementPageTemplateEntity template;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.LAZY)
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		MetadataElementPageTemplateXrefEntity other = (MetadataElementPageTemplateXrefEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
}
