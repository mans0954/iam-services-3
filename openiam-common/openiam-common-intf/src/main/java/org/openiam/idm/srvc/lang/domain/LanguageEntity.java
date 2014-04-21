package org.openiam.idm.srvc.lang.domain;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.internationalization.Internationalized;
import org.openiam.internationalization.InternationalizedCollection;

import javax.persistence.*;
import java.util.Map;

@Entity
@Table(name = "LANGUAGE")
@DozerDTOCorrespondence(Language.class)
@AttributeOverride(name = "id", column = @Column(name = "ID"))
@Internationalized
public class LanguageEntity extends KeyEntity {
    private static final long serialVersionUID = 6695606794883491243L;
    
//    @Column(name = "LANGUAGE", length = 20)
    @Transient
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

    @Transient
    @InternationalizedCollection(targetField="name")
    private Map<String, LanguageMappingEntity> displayNameMap;

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

    public boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public Map<String, LanguageMappingEntity> getDisplayNameMap() {
        return displayNameMap;
    }

    public void setDisplayNameMap(Map<String, LanguageMappingEntity> displayNameMap) {
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