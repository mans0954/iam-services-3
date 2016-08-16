package org.openiam.base.request;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserToResourceAccessRequest", propOrder = {
	"resourceId"
})
public class UserToResourceAccessRequest extends UserRequest implements Serializable {

	private static final long serialVersionUID = -1L;
	
	public UserToResourceAccessRequest() {}
	
	private String resourceId;

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	
}
