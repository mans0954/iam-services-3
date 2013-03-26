package org.openiam.idm.searchbeans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.meta.dto.MetadataElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataElementSearchBean", propOrder = {
	"metadataTypeId",
	"auditable",
	"required",
	"attributeName",
	"selfEditable",
	"templateId"
})
public class MetadataElementSearchBean extends AbstractSearchBean<MetadataElement, String> implements SearchBean<MetadataElement, String> {

	private String metadataTypeId;
	private boolean auditable;
	private boolean required;
	private String attributeName;
	private boolean selfEditable;
	private String templateId;
	public String getMetadataTypeId() {
		return metadataTypeId;
	}
	public void setMetadataTypeId(String metadataTypeId) {
		this.metadataTypeId = metadataTypeId;
	}
	public boolean isAuditable() {
		return auditable;
	}
	public void setAuditable(boolean auditable) {
		this.auditable = auditable;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	public boolean isSelfEditable() {
		return selfEditable;
	}
	public void setSelfEditable(boolean selfEditable) {
		this.selfEditable = selfEditable;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	
	
}
