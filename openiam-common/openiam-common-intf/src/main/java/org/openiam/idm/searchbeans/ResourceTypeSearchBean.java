package org.openiam.idm.searchbeans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.res.dto.ResourceType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceTypeSearchBean", propOrder = {
	"searchable"
})
public class ResourceTypeSearchBean extends AbstractSearchBean<ResourceType, String> implements SearchBean<ResourceType, String>, Serializable {

	private boolean searchable = true;
	
	public ResourceTypeSearchBean() {}

	public boolean isSearchable() {
		return searchable;
	}

	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}
	
	
}
