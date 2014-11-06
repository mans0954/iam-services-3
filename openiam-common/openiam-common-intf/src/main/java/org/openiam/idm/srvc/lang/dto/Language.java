package org.openiam.idm.srvc.lang.dto;

import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.internationalization.Internationalized;
import org.openiam.internationalization.InternationalizedCollection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.HashMap;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Language", propOrder = { "name", "locales", "isUsed", "languageCode", "isDefault", "displayNameMap" })
@DozerDTOCorrespondence(LanguageEntity.class)
@Internationalized
public class Language extends KeyDTO implements Cloneable {
    private static final long serialVersionUID = 6695606794883491243L;
    private String name;
    private boolean isUsed = false;
    private String languageCode;
    private boolean isDefault = false;

    @InternationalizedCollection(targetField = "name")
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

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
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
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        return "Language [id=" + id + ", name=" + name + ", isUsed=" + isUsed + "]";
    }

    @Override
    public Language clone() throws CloneNotSupportedException {
        Language cloned = new Language();
        cloned.setId(this.id);
        cloned.setRequestorLogin(this.requestorLogin);
        cloned.setRequestorUserId(this.requestorUserId);
        cloned.setRequestClientIP(this.requestClientIP);
        cloned.setRequestorSessionID(this.requestorSessionID);
        cloned.setIsDefault(this.isDefault);
        cloned.setIsUsed(this.isUsed);
        cloned.setLanguageCode(this.languageCode);
        cloned.setName(this.name);
        cloned.setUsed(this.isUsed);
        cloned.setObjectState(this.objectState);

        if (locales != null) {
            Map<String, LanguageLocale> clonedLocales = new HashMap<>();
            for (String key : this.locales.keySet()) {
                LanguageLocale locale = this.locales.get(key);
                if (locale != null)
                    clonedLocales.put(key, locale.clone());
            }
            cloned.setLocales(clonedLocales);
        }

        if (displayNameMap != null) {
            Map<String, LanguageMapping> clonedDisplayNameMap = new HashMap<>();
            for (String key : this.displayNameMap.keySet()) {
                LanguageMapping nameMap = this.displayNameMap.get(key);
                if (nameMap != null)
                    clonedDisplayNameMap.put(key, nameMap.clone());
            }
            cloned.setDisplayNameMap(clonedDisplayNameMap);
        }

        return cloned;
    }
}