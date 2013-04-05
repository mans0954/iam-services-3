package org.openiam.idm.srvc.meta.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.idm.srvc.meta.comparator.PageElementComparator;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplate;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PageTempate", 
	propOrder = { 
		"templateId",
        "pageElements"
})
public class PageTempate implements Serializable{

	private String templateId;
	
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

	/**
	 * Called only by JSTL
	 * @return
	 */
	public List<PageElement> getElements() {
		return (pageElements != null) ? new ArrayList<PageElement>(pageElements) : null;
	}

	public TreeSet<PageElement> getPageElements() {
		return pageElements;
	}

	public void setPageElements(TreeSet<PageElement> pageElements) {
		this.pageElements = pageElements;
	}
}
