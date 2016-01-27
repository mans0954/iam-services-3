package org.openiam.idm.srvc.lang.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.searchbeans.AbstractSearchBean;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LanguageMappingSearchBean", propOrder = {
        "languageId",
        "referenceId",
        "referenceType",
        "value"
})
public class LanguageMappingSearchBean extends AbstractSearchBean<LanguageMapping, String> {

	private String languageId;
	private String referenceId;
	private String referenceType;
	private String value;
	
	public LanguageMappingSearchBean() {}

	public String getLanguageId() {
		return languageId;
	}

	public void setLanguageId(String languageId) {
		this.languageId = languageId;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(String referenceType) {
		this.referenceType = referenceType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
