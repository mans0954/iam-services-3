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
public class MetadataTemplateTypeFieldSearchBean extends AbstractSearchBean<MetadataTemplateTypeField, String> {

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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((templateId == null) ? 0 : templateId.hashCode());
		result = prime * result
				+ ((templateTypeId == null) ? 0 : templateTypeId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetadataTemplateTypeFieldSearchBean other = (MetadataTemplateTypeFieldSearchBean) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (templateId == null) {
			if (other.templateId != null)
				return false;
		} else if (!templateId.equals(other.templateId))
			return false;
		if (templateTypeId == null) {
			if (other.templateTypeId != null)
				return false;
		} else if (!templateTypeId.equals(other.templateTypeId))
			return false;
		return true;
	}


	
}
