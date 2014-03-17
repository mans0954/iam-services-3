package org.openiam.idm.srvc.lang.domain;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.dto.Language;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Map;

@Entity
@Table(name = "LANGUAGE")
@DozerDTOCorrespondence(Language.class)
@AttributeOverride(name = "id", column = @Column(name = "ID"))
public class LanguageEntity extends KeyEntity {
    private static final long serialVersionUID = 6695606794883491243L;
    
    @Column(name = "LANGUAGE", length = 20)
    private String name;

    @Column(name = "IS_USED")
    @Type(type = "yes_no")
    private boolean isUsed=false;
    
    @Column(name = "IS_DEFAULT")
    @Type(type = "yes_no")
    private boolean isDefault=false;

    @Column(name="LANGUAGE_CODE", length = 2)
    private String languageCode;

    @OneToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy="language")
    @MapKey(name = "locale")
    @Fetch(FetchMode.SUBSELECT)
    private Map<String, LanguageLocaleEntity> locales;

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

	public Map<String, LanguageLocaleEntity> getLocales() {
		return locales;
	}

	public void setLocales(Map<String, LanguageLocaleEntity> locales) {
		this.locales = locales;
	}

	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}

    public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
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
		LanguageEntity other = (LanguageEntity) obj;
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
		return "LanguageEntity [id=" + id + ", name=" + name
				+ ", isUsed=" + isUsed + "]";
	}

	
}