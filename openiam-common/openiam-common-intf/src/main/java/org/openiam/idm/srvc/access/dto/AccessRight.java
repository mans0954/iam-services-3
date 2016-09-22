package org.openiam.idm.srvc.access.dto;

import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.internationalization.Internationalized;
import org.openiam.internationalization.InternationalizedCollection;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AccessRight", propOrder = {})
@DozerDTOCorrespondence(AccessRightEntity.class)
@Internationalized
public class AccessRight extends KeyNameDTO {

    @Transient
    private String displayName;

    private String metadataType1;
    private String metadataType2;

    private String metadataTypeDisplayName1;
    private String metadataTypeDisplayName2;

    @Transient
    @InternationalizedCollection(targetField = "displayName")
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

    public String getMetadataType1() {
        return metadataType1;
    }

    public void setMetadataType1(String metadataType1) {
        this.metadataType1 = metadataType1;
    }

    public String getMetadataType2() {
        return metadataType2;
    }

    public void setMetadataType2(String metadataType2) {
        this.metadataType2 = metadataType2;
    }

    public String getMetadataTypeDisplayName1() {
        return metadataTypeDisplayName1;
    }

    public void setMetadataTypeDisplayName1(String metadataTypeDisplayName1) {
        this.metadataTypeDisplayName1 = metadataTypeDisplayName1;
    }

    public String getMetadataTypeDisplayName2() {
        return metadataTypeDisplayName2;
    }

    public void setMetadataTypeDisplayName2(String metadataTypeDisplayName2) {
        this.metadataTypeDisplayName2 = metadataTypeDisplayName2;
    }
}
