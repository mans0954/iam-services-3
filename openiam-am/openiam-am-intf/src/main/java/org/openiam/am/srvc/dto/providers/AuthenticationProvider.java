package org.openiam.am.srvc.dto.providers;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.mngsys.dto.ManagedSys;
import org.openiam.idm.srvc.res.dto.Resource;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthenticationProvider", propOrder = {
	"name",
	"id",
	"managedSysId",
	"description",
	"resource"
})
public abstract class AuthenticationProvider implements Serializable {
	
	private String name;
	private String id;
	private String managedSysId;
	private String description;
	private Resource resource;
	
	public String getId() {
		return id;
	}
	
	public void setId(final String id) {
		this.id = id;
	}

	public String getManagedSysId() {
		return managedSysId;
	}

	public void setManagedSysId(String managedSysId) {
		this.managedSysId = managedSysId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(final Resource resource) {
		this.resource = resource;
	}
}
