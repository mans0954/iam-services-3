package org.openiam.idm.srvc.cat.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

// Generated Nov 22, 2008 1:32:51 PM by Hibernate Tools 3.2.2.GA

/**
 * CategoryLanguageId is the PrimaryKey for the CategoryLanguage object
 */
@Embeddable
public class CategoryLanguageEmbeddableId implements java.io.Serializable {
    /**
     * @author zaporozhec
     */
    private static final long serialVersionUID = 1L;
    @Column(name = "CATEGORY_ID", length = 32, nullable = false)
    private String categoryId;
    @Column(name = "LANGUAGE_ID", length = 5, nullable = false)
    private String languageId;

    public CategoryLanguageEmbeddableId() {
    }

    public String getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getLanguageId() {
        return this.languageId;
    }

    public void setLanguageId(String languageCd) {
        this.languageId = languageCd;
    }

    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof CategoryLanguageEmbeddableId))
            return false;
        CategoryLanguageEmbeddableId castOther = (CategoryLanguageEmbeddableId) other;

        return ((this.getCategoryId() == castOther.getCategoryId()) || (this
                .getCategoryId() != null
                && castOther.getCategoryId() != null && this.getCategoryId()
                .equals(castOther.getCategoryId())))
                && ((this.getLanguageId() == castOther.getLanguageId()) || (this
                .getLanguageId() != null
                && castOther.getLanguageId() != null && this
                .getLanguageId().equals(castOther.getLanguageId())));
    }

    public int hashCode() {
        int result = 17;

        result = 37
                * result
                + (getCategoryId() == null ? 0 : this.getCategoryId()
                .hashCode());
        result = 37
                * result
                + (getLanguageId() == null ? 0 : this.getLanguageId()
                .hashCode());
        return result;
    }
}
