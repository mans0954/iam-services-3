package org.openiam.base.response;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.am.srvc.dto.jdbc.AuthorizationGroup;
import org.openiam.base.ws.ResponseStatus;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GroupsForUserResponse", propOrder = {
    "groups"
})
public class GroupsForUserResponse extends AbstractResponse implements Serializable {

	private static final long serialVersionUID = -1L;
	
	private Set<AuthorizationGroup> groups = new HashSet<AuthorizationGroup>();
	
	public GroupsForUserResponse() {
		
	}
	
	public GroupsForUserResponse(final ResponseStatus responseStatus) {
		setResponseStatus(responseStatus);
	}

	public Set<AuthorizationGroup> getGroups() {
		return groups;
	}

	public void setGroups(Set<AuthorizationGroup> groups) {
		this.groups = groups;
	}
	
	
}
