package org.openiam.idm.srvc.lang.dto;

import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.internationalization.Internationalized;
import org.openiam.internationalization.InternationalizedCollection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Language", propOrder = {
        "name",
        "locales",
        "isUsed",
        "languageCode",
        "isDefault"
})
@DozerDTOCorrespondence(LanguageEntity.class)
@Internationalized
public class Language extends KeyDTO {
        private static final long serialVersionUID = 6695606794883491243L;
        private String name;
        private boolean isUsed=false;
        private String languageCode;
        private boolean isDefault=false;

        @InternationalizedCollection(referenceType="LanguageEntity", targetField="name")
        private Map<String, LanguageMapping> displayNameMap;

        private Map<String, LanguageLocale> locales;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean getIsUsed() {
            return isUsed;
        }

        public void setIsUsed(boolean used) {
            isUsed = used;
        }

		public Map<String, LanguageLocale> getLocales() {
			return locales;
		}
		
		public boolean hasLocale(final String locale) {
			return (locale != null && locales != null) ? locales.containsKey(locale) : null;
		}

		public void setLocales(Map<String, LanguageLocale> locales) {
			this.locales = locales;
		}

		public void setUsed(boolean isUsed) {
			this.isUsed = isUsed;
		}

        public String getLanguageCode() {
            return languageCode;
        }

        public void setLanguageCode(String languageCode) {
            this.languageCode = languageCode;
        }
        
        public boolean isDefault() {
    		return isDefault;
    	}

    	public void setDefault(boolean isDefault) {
    		this.isDefault = isDefault;
    	}

        public Map<String, LanguageMapping> getDisplayNameMap() {
            return displayNameMap;
        }

        public void setDisplayNameMap(Map<String, LanguageMapping> displayNameMap) {
            this.displayNameMap = displayNameMap;
        }

        @Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (isUsed ? 1231 : 1237);
			result = prime * result
					+ ((id == null) ? 0 : id.hashCode());
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
			if (isDefault != other.isDefault)
				return false;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
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
			return "Language [id=" + id + ", name=" + name
					+ ", isUsed=" + isUsed + "]";
		}

		
		
}
