package org.openiam.idm.searchbeans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MembershipGroupSearchBean", propOrder = {
        "membershipGroupId",
        "userId",
        "roleId",
        "resourceId"
})
public class MembershipGroupSearchBean extends GroupSearchBean {
    private String membershipGroupId;
    private String userId;
    private String roleId;
    private String resourceId;

    public String getMembershipGroupId() {
        return membershipGroupId;
    }

    public void setMembershipGroupId(String membershipGroupId) {
        this.membershipGroupId = membershipGroupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
