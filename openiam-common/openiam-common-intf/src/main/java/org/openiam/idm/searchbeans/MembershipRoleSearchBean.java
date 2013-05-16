package org.openiam.idm.searchbeans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MembershipRoleSearchBean", propOrder = {
        "membershipRoleId",
        "userId",
        "groupId",
        "resourceId"
})
public class MembershipRoleSearchBean extends RoleSearchBean {
    private String membershipRoleId;
    private String userId;
    private String groupId;
    private String resourceId;

    public String getMembershipRoleId() {
        return membershipRoleId;
    }

    public void setMembershipRoleId(String membershipRoleId) {
        this.membershipRoleId = membershipRoleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
