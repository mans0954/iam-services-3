package org.openiam.idm.srvc.cat.dto;

import javax.persistence.Column;
import javax.persistence.Embeddable;

// Generated Nov 22, 2008 1:32:51 PM by Hibernate Tools 3.2.2.GA

/**
 * CategoryLanguageId is the PrimaryKey for the CategoryLanguage object
 */
@Embeddable
public class CategoryLanguageId implements java.io.Serializable {

    private String categoryId;
    private String languageCd;

    public CategoryLanguageId() {
    }

    @Column(name="CATEGORY_ID",length=20,nullable=false)
    public String getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @Column(name="LANGUAGE_CD",length=5,nullable=false)
    public String getLanguageCd() {
        return this.languageCd;
    }

    public void setLanguageCd(String languageCd) {
        this.languageCd = languageCd;
    }

    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof CategoryLanguageId))
            return false;
        CategoryLanguageId castOther = (CategoryLanguageId) other;

        return ((this.getCategoryId() == castOther.getCategoryId()) || (this
                .getCategoryId() != null
                && castOther.getCategoryId() != null && this.getCategoryId()
                .equals(castOther.getCategoryId())))
                && ((this.getLanguageCd() == castOther.getLanguageCd()) || (this
                .getLanguageCd() != null
                && castOther.getLanguageCd() != null && this
                .getLanguageCd().equals(castOther.getLanguageCd())));
    }

    public int hashCode() {
        int result = 17;

        result = 37
                * result
                + (getCategoryId() == null ? 0 : this.getCategoryId()
                .hashCode());
        result = 37
                * result
                + (getLanguageCd() == null ? 0 : this.getLanguageCd()
                .hashCode());
        return result;
    }

}
