package org.openiam.idm.searchbeans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataTemplateType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataTemplateTypeSearchBean", propOrder = {
	"name"
})
public class MetadataTemplateTypeSearchBean extends AbstractSearchBean<MetadataTemplateType, String> implements SearchBean<MetadataTemplateType, String> {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
