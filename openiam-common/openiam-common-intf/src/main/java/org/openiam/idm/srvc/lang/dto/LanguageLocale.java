package org.openiam.idm.srvc.lang.dto;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.domain.LanguageLocaleEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LanguageLocale", propOrder = {
        "id",
        "languageId",
        "locale"
})
@DozerDTOCorrespondence(LanguageLocaleEntity.class)
public class LanguageLocale implements Serializable, Cloneable {

	private String id;
	private String languageId;
	private String locale;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public String getLanguageId() {
		return languageId;
	}
	public void setLanguageId(String languageId) {
		this.languageId = languageId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((languageId == null) ? 0 : languageId.hashCode());
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
		LanguageLocale other = (LanguageLocale) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
		return true;
	}
	@Override
	public String toString() {
		return "LanguageLocale [id=" + id + ", languageId=" + languageId
				+ ", locale=" + locale + "]";
	}
    @Override
    public LanguageLocale clone() throws CloneNotSupportedException {
        return (LanguageLocale)super.clone();
    }

	
}
