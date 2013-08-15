package org.openiam.idm.searchbeans;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.idm.srvc.meta.dto.MetadataElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataElementSearchBean", propOrder = {
	"typeIdSet",
	"auditable",
	"required",
	"attributeName",
	"selfEditable",
	"templateId",
	"keySet"
})
public class MetadataElementSearchBean extends AbstractSearchBean<MetadataElement, String> implements SearchBean<MetadataElement, String> {

	private Set<String> keySet;
	private Set<String> typeIdSet;
	private boolean auditable;
	private boolean required;
	private String attributeName;
	private boolean selfEditable;
	private String templateId;
	
	public Set<String> getTypeIdSet() {
		return typeIdSet;
	}
	public void setTypeIdSet(Set<String> typeIdSet) {
		this.typeIdSet = typeIdSet;
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
	
	@Override
	public void setKey(final String key) {
		if(keySet == null) {
			keySet = new HashSet<String>();
		}
		keySet.add(key);
	}
	
	public Set<String> getKeys() {
		return keySet;
	}
	
	public void addKey(final String key) {
		if(this.keySet == null) {
			this.keySet = new HashSet<String>();
		}
		this.keySet.add(key);
	}
	
	public boolean hasMultipleKeys() {
		return (keySet != null && keySet.size() > 1);
	}
	
	public void setKeys(final Set<String> keySet) {
		this.keySet = keySet;
	}
	
	@Override
	public String getKey() {
		return (CollectionUtils.isNotEmpty(keySet)) ? keySet.iterator().next() : null;
	}
}
