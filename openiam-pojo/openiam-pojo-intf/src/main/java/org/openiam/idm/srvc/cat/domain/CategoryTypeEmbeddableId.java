package org.openiam.idm.srvc.cat.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
@Embeddable
public class CategoryTypeEmbeddableId implements Serializable {
    /**
     * @author zaporozhec
     */
    private static final long serialVersionUID = 1L;
    @Column(name = "CATEGORY_ID", length = 32, nullable = false)
    private String categoryId;
    @Column(name = "TYPE_ID", length = 32, nullable = false)
    private String typeId;


    public CategoryTypeEmbeddableId() {
        super();
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getTypeId() {
        return typeId;
    }


    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryTypeEmbeddableId that = (CategoryTypeEmbeddableId) o;

        if (categoryId != null ? !categoryId.equals(that.categoryId) : that.categoryId != null) return false;
        if (typeId != null ? !typeId.equals(that.typeId) : that.typeId != null) return false;

        return true;
    }

    public int hashCode() {
        int result = categoryId != null ? categoryId.hashCode() : 0;
        result = 31 * result + (typeId != null ? typeId.hashCode() : 0);
        return result;
    }
}
