package org.openiam.idm.searchbeans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.org.dto.OrganizationType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganizationTypeSearchBean", propOrder = {
	"name"
})
public class OrganizationTypeSearchBean extends AbstractSearchBean<OrganizationType, String> implements SearchBean<OrganizationType, String> {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
