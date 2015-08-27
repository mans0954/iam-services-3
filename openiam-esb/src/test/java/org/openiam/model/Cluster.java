package org.openiam.model;

import java.util.List;

public class Cluster {

	private List<Member> members;
	
	Cluster() {}

	public List<Member> getMembers() {
		return members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}
	
	
}
