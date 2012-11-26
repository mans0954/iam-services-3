package org.openiam.idm.srvc.cat.domain;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.cat.dto.CategoryType;

@Entity
@Table(name="CATEGORY_TYPE")
@DozerDTOCorrespondence(CategoryType.class)
public class CategoryTypeEntity implements Serializable {
    private CategoryTypeEmbeddableId id;

    @EmbeddedId
    public CategoryTypeEmbeddableId getId() {
        return id;
    }

    public void setId(CategoryTypeEmbeddableId id) {
        this.id = id;
    }
}
