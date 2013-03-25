package org.openiam.idm.srvc.lang.dto;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Language", propOrder = {
        "languageId",
        "name",
        "locale",
        "isUsed"
})
@DozerDTOCorrespondence(LanguageEntity.class)
public class Language implements Serializable {
        private static final long serialVersionUID = 6695606794883491243L;
        private String languageId;
        private String name;
        private String locale;
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
