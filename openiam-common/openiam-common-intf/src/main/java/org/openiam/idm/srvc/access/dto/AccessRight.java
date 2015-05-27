package org.openiam.idm.srvc.access.dto;

import java.util.Map;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.internationalization.Internationalized;
import org.openiam.internationalization.InternationalizedCollection;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AccessRight", propOrder = {

})
@DozerDTOCorrespondence(AccessRightEntity.class)
@Internationalized
public class AccessRight extends KeyNameDTO {

	@Transient
    private String displayName;
    
    @Transient
    @InternationalizedCollection(targetField="displayName")
    private Map<String, LanguageMapping> languageMap;
    
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Map<String, LanguageMapping> getLanguageMap() {
		return languageMap;
	}

	public void setLanguageMap(Map<String, LanguageMapping> languageMap) {
		this.languageMap = languageMap;
	}
    
    
}
