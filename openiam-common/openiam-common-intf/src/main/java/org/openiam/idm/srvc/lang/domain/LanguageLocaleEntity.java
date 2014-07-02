package org.openiam.idm.srvc.lang.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.dto.LanguageLocale;

@Entity
@Table(name = "LANGUAGE_LOCALE")
@DozerDTOCorrespondence(LanguageLocale.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LanguageLocaleEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "ID", length = 32)
    private String id;

    @ManyToOne
    @JoinColumn(name = "LANGUAGE_ID", referencedColumnName = "ID", insertable = true, updatable = false, nullable = false)
    private LanguageEntity language;

    @Column(name = "LOCALE", length = 20, nullable = false)
    private String locale;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LanguageEntity getLanguage() {
        return language;
    }

    public void setLanguage(LanguageEntity language) {
        this.language = language;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((language == null) ? 0 : language.hashCode());
        result = prime * result + ((locale == null) ? 0 : locale.hashCode());
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
        LanguageLocaleEntity other = (LanguageLocaleEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (language == null) {
            if (other.language != null)
                return false;
        } else if (!language.equals(other.language))
            return false;
        if (locale == null) {
            if (other.locale != null)
                return false;
        } else if (!locale.equals(other.locale))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "LanguageLocaleEntity [id=" + id + ", language=" + language + ", locale=" + locale + "]";
    }

}
