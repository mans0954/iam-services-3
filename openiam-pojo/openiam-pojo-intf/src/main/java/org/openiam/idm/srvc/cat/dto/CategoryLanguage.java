package org.openiam.idm.srvc.cat.dto;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.cat.domain.CategoryLanguageEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "categoryLanguage", propOrder = { "id", "categoryName" })
@DozerDTOCorrespondence(CategoryLanguageEntity.class)
public class CategoryLanguage implements Serializable {


    private CategoryLanguageId id;
    private String categoryName;

    static final long serialVersionUID = -6948749895519617508L;

    public CategoryLanguage() {
        super();
    }

    public CategoryLanguageId getId() {
        return id;
    }


    public void setId(CategoryLanguageId id) {
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
		CategoryLanguage other = (CategoryLanguage) obj;
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
