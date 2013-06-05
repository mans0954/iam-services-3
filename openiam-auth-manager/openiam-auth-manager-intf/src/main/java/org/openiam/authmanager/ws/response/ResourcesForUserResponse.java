package org.openiam.authmanager.ws.response;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.base.ws.ResponseStatus;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourcesForUserResponse", propOrder = {
    "resources"
})
public class ResourcesForUserResponse extends AbstractResponse implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	private Set<AuthorizationResource> resources = new HashSet<AuthorizationResource>();
	
	public ResourcesForUserResponse() {
		
	}

	public Set<AuthorizationResource> getResources() {
		return resources;
	}

	public void setResources(final Set<AuthorizationResource> authorizationResources) {
		this.resources = authorizationResources;
	}
	
	public ResourcesForUserResponse(final ResponseStatus responseStatus) {
		setResponseStatus(responseStatus);
	}
}
