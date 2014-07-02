package org.openiam.idm.srvc.cat.domain;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.cat.dto.CategoryType;

@Entity
@Table(name="CATEGORY_TYPE")
@DozerDTOCorrespondence(CategoryType.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CategoryTypeEntity implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private CategoryTypeEmbeddableId id;

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name="typeId", column=@Column(name="TYPE_ID")),
            @AttributeOverride(name="categoryId", column=@Column(name="CATEGORY_ID"))
    })
    public CategoryTypeEmbeddableId getId() {
        return id;
    }

    public void setId(CategoryTypeEmbeddableId id) {
        this.id = id;
    }
}
