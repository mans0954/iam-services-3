package org.openiam.idm.srvc.cat.dto;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.cat.domain.CategoryTypeEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CategoryType", propOrder = {
        "id"
})
@DozerDTOCorrespondence(CategoryTypeEntity.class)
public class CategoryType implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private CategoryTypeId id;

    public CategoryTypeId getId() {
        return id;
    }

    public void setId(CategoryTypeId id) {
        this.id = id;
    }
}
