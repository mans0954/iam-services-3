package org.openiam.idm.searchbeans;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.meta.dto.MetadataElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataElementSearchBean", propOrder = {
	"typeIdSet",
	"auditable",
	"required",
	"attributeName",
	"selfEditable",
	"templateId",
	"keySet",
	"excludedGroupings",
	"categoryTypes"
})
public class MetadataElementSearchBean extends AbstractSearchBean<MetadataElement, String> implements SearchBean<MetadataElement, String> {

	private Set<String> keySet;
	private Set<String> typeIdSet;
	private boolean auditable;
	private boolean required;
	private String attributeName;
	private boolean selfEditable;
	private String templateId;
	private Set<MetadataTypeGrouping> excludedGroupings;
	private Set<String> categoryTypes;
	
	public Set<String> getTypeIdSet() {
		return typeIdSet;
	}
	public void addTypeId(final String typeId) {
		if(StringUtils.isNotBlank(typeId)) {
			if(this.typeIdSet == null) {
				this.typeIdSet = new HashSet<>();
			}
			this.typeIdSet.add(typeId);
		}
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
	
	public void addExcludedGrouping(final MetadataTypeGrouping grouping) {
		if(grouping != null) {
			if(this.excludedGroupings == null) {
				this.excludedGroupings = new HashSet<>();
			}
			this.excludedGroupings.add(grouping);
		}
	}
	
	public Set<MetadataTypeGrouping> getExcludedGroupings() {
		return excludedGroupings;
	}
	public void setExcludedGroupings(Set<MetadataTypeGrouping> excludedGroupings) {
		this.excludedGroupings = excludedGroupings;
	}
	
	public void addCategoryType(final String category) {
		if(StringUtils.isNotBlank(category)) {
			if(this.categoryTypes == null) {
				this.categoryTypes = new HashSet<>();
			}
			this.categoryTypes.add(category);
		}
	}
	
	public Set<String> getCategoryTypes() {
		return categoryTypes;
	}
	public void setCategoryTypes(Set<String> categoryTypes) {
		this.categoryTypes = categoryTypes;
	}
	@Override
	public String getKey() {
		return (CollectionUtils.isNotEmpty(keySet)) ? keySet.iterator().next() : null;
	}
}
