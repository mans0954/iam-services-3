package org.openiam.idm.srvc.cat.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.cat.dto.CategoryLanguage;

/**
 * 
 * @author zaporozhec
 *
 */

@Entity
@Table(name="CATEGORY_LANGUAGE")
@DozerDTOCorrespondence(CategoryLanguage.class)
public class CategoryLanguageEntity implements Serializable {

    @EmbeddedId
    private CategoryLanguageEmbeddableId id;
    @Column(name = "CATEGORY_NAME", length = 40)
    private String categoryName;

    static final long serialVersionUID = -6948749895519617508L;

    public CategoryLanguageEntity() {
        super();
    }

    public CategoryLanguageEmbeddableId getId() {
        return id;
    }
    public void setId(CategoryLanguageEmbeddableId id) {
        this.id = id;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CategoryLanguageEntity other = (CategoryLanguageEntity) obj;
		if (categoryName == null) {
			if (other.categoryName != null)
				return false;
		} else if (!categoryName.equals(other.categoryName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
