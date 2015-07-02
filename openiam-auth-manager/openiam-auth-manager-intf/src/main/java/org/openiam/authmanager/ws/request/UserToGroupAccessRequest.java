package org.openiam.authmanager.ws.request;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.authmanager.common.model.AuthorizationGroup;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserToGroupAccessRequest", propOrder = {
	"groupId"
})
public class UserToGroupAccessRequest extends UserRequest implements Serializable {

	private static final long serialVersionUID = -1L;
	
	private String groupId;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	
}
