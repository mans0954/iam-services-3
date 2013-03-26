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

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (isUsed ? 1231 : 1237);
			result = prime * result
					+ ((languageId == null) ? 0 : languageId.hashCode());
			result = prime * result
					+ ((locale == null) ? 0 : locale.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
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
			Language other = (Language) obj;
			if (isUsed != other.isUsed)
				return false;
			if (languageId == null) {
				if (other.languageId != null)
					return false;
			} else if (!languageId.equals(other.languageId))
				return false;
			if (locale == null) {
				if (other.locale != null)
					return false;
			} else if (!locale.equals(other.locale))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return String.format(
					"Language [languageId=%s, name=%s, locale=%s, isUsed=%s]",
					languageId, name, locale, isUsed);
		}
        
        
}
