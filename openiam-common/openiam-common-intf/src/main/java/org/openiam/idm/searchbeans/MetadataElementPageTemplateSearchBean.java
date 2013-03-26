package org.openiam.idm.searchbeans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplate;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataElementPageTemplateSearchBean", propOrder = {
		"name"
})
public class MetadataElementPageTemplateSearchBean extends AbstractSearchBean<MetadataElementPageTemplate, String> implements SearchBean<MetadataElementPageTemplate, String> {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
