package org.openiam.idm.srvc.lang.domain;

import org.hibernate.annotations.Type;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.dto.Language;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "LANGUAGE")
@DozerDTOCorrespondence(Language.class)
public class LanguageEntity implements Serializable {
    private static final long serialVersionUID = 6695606794883491243L;

    @Id
    @Column(name = "ID", length = 32)
    private String languageId;
    @Column(name = "LANGUAGE", length = 20)
    private String name;
    @Column(name = "LOCALE", length = 10)
    private String locale;
    @Column(name = "IS_USED")
    @Type(type = "yes_no")
    private boolean isUsed=false;

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public boolean getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(boolean used) {
        isUsed = used;
    }
}