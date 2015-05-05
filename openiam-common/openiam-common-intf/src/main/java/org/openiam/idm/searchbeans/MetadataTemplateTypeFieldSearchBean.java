package org.openiam.idm.searchbeans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeFieldEntity;
import org.openiam.idm.srvc.meta.dto.MetadataTemplateTypeField;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;
import org.springframework.stereotype.Component;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataTemplateTypeFieldSearchBean", propOrder = {
	"templateId",
	"templateTypeId",
	"name"
})
public class MetadataTemplateTypeFieldSearchBean extends AbstractSearchBean<MetadataTemplateTypeField, String> implements SearchBean<MetadataTemplateTypeField, String> {

	private String templateId;
	private String templateTypeId;
	private String name;
	
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTemplateTypeId() {
		return templateTypeId;
	}
	public void setTemplateTypeId(String templateTypeId) {
		this.templateTypeId = templateTypeId;
	}


}
