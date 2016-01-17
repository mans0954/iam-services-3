package org.openiam.idm.srvc.meta.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.idm.srvc.meta.comparator.PageElementComparator;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplate;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PageTempate", 
	propOrder = { 
		"templateId",
        "pageElements",
        "uiFields",
		"jsonDataModel",
		"customJS"
})
public class PageTempate implements Serializable{

	private String templateId;
	
	private Map<String, TemplateUIField> uiFields;
	private String jsonDataModel;
	@XmlTransient
	private Map objectDataModel;
	private String customJS;
	private TreeSet<PageElement> pageElements = new TreeSet<PageElement>(PageElementComparator.INSTANCE);
	
	public PageTempate() {}
	
	public void addElement(final PageElement element) {
		if(element != null) {
			pageElements.add(element);
		}
	}
	
	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	
	public TemplateUIField getUIField(final String fieldID) {
		return (uiFields != null) ? uiFields.get(fieldID) : null;
	}
	
	public void addUIField(final TemplateUIField field) {
		if(field != null) {
			if(this.uiFields == null) {
				this.uiFields = new HashMap<String, TemplateUIField>();
			}
			this.uiFields.put(field.getId(), field);
		}
	}
	
	public boolean hasUIFIeld(final String id) {
		return (uiFields != null && uiFields.containsKey(id));
	}
	
	public boolean isFieldRequired(final String id) {
		return (uiFields != null && uiFields.containsKey(id)) ? uiFields.get(id).isRequired() : false;
	}

	/**
	 * Called only by JSTL
	 * @return always returns a map, to prevent any chance of a NPE
	 */
	public Map<String, TemplateUIField> getUiFields() {
		if(uiFields == null) {
			uiFields = new HashMap<String, TemplateUIField>();
		}
		return uiFields;
	}

	public TreeSet<PageElement> getPageElements() {
		return pageElements;
	}

	public void setPageElements(Collection<PageElement> pageElements) {
		final TreeSet<PageElement> treeSet = new TreeSet<PageElement>(PageElementComparator.INSTANCE);
		treeSet.addAll(pageElements);
		this.pageElements = treeSet;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((pageElements == null) ? 0 : pageElements.hashCode());
		result = prime * result
				+ ((templateId == null) ? 0 : templateId.hashCode());
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
		PageTempate other = (PageTempate) obj;
		if (pageElements == null) {
			if (other.pageElements != null)
				return false;
		} else if (!pageElements.equals(other.pageElements))
			return false;
		if (templateId == null) {
			if (other.templateId != null)
				return false;
		} else if (!templateId.equals(other.templateId))
			return false;
		return true;
	}

	public String getJsonDataModel() {
		return jsonDataModel;
	}

	public void setJsonDataModel(String jsonDataModel) {
		this.jsonDataModel = jsonDataModel;
	}

	public Map getObjectDataModel() {
		return objectDataModel;
	}

	public void setObjectDataModel(Map objectDataModel) {
		this.objectDataModel = objectDataModel;
	}

	public String getCustomJS() {
		return customJS;
	}

	public void setCustomJS(String customJS) {
		this.customJS = customJS;
	}

	@Override
	public String toString() {
		return "PageTempate [templateId=" + templateId + ", pageElements="
				+ pageElements + "]";
	}
	
	
}
