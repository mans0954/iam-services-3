package org.openiam.authmanager.common.xref;

public class ResourceResourceXref {

	private String resourceId;
	private String memberResourceId;
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public String getMemberResourceId() {
		return memberResourceId;
	}
	public void setMemberResourceId(String memberResourceId) {
		this.memberResourceId = memberResourceId;
	}
	@Override
	public String toString() {
		return String.format(
				"ResourceResourceXref [resourceId=%s, memberResourceId=%s]",
				resourceId, memberResourceId);
	}
	
	
}
