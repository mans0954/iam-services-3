package org.openiam.authmanager.ws.request;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.authmanager.common.model.AuthorizationResource;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserToResourceAccessRequest", propOrder = {
	"resource"
})
public class UserToResourceAccessRequest extends UserRequest implements Serializable {

	private static final long serialVersionUID = -1L;
	
	private AuthorizationResource resource;

	public AuthorizationResource getResource() {
		return resource;
	}

	public void setResource(AuthorizationResource resource) {
		this.resource = resource;
	}
	
	public UserToResourceAccessRequest() {
		
	}

	@Override
	public String toString() {
		return String.format("UserToResourceAccessRequest [resource=%s]",
				resource);
	}
	
	
}
