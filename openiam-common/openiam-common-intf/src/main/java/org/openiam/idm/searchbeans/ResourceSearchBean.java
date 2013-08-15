package org.openiam.idm.searchbeans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.res.dto.Resource;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceSearchBean", propOrder = {
        "name",
        "resourceTypeId",
        "rootsOnly"
})
public class ResourceSearchBean extends AbstractSearchBean<Resource, String> implements SearchBean<Resource, String>, Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String resourceTypeId;
	private Boolean rootsOnly;
	
	public String getName() {
		return name;
	}
	
	public void setName(final String name) {
		this.name = name;
	}

	public String getResourceTypeId() {
		return resourceTypeId;
	}

	public void setResourceTypeId(String resourceTypeId) {
		this.resourceTypeId = resourceTypeId;
	}

	public Boolean getRootsOnly() {
		return rootsOnly;
	}

	public void setRootsOnly(Boolean rootsOnly) {
		this.rootsOnly = rootsOnly;
	}
}
