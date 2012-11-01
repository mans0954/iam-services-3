package org.openiam.idm.srvc.cat.dto;

import javax.persistence.Cacheable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CategoryType", propOrder = {
        "id"
})
@Entity
@Table(name="CATEGORY_TYPE")
public class CategoryType implements Serializable {
    private CategoryTypeId id;

    @EmbeddedId
    public CategoryTypeId getId() {
        return id;
    }

    public void setId(CategoryTypeId id) {
        this.id = id;
    }
}
