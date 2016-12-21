package org.openiam.base.request;

import org.openiam.idm.srvc.user.dto.UserStatusEnum;

/**
 * Created by Alexander Dukkardt on 2016-12-20.
 */
public class UnlockRequest extends BaseServiceRequest {
    private String principal;
    private String managedSysId;
    private UserStatusEnum status;

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getManagedSysId() {
        return managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
    }

    public UserStatusEnum getStatus() {
        return status;
    }

    public void setStatus(UserStatusEnum status) {
        this.status = status;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UnlockRequest{");
        sb.append(super.toString());
        sb.append(",                 principal='").append(principal).append('\'');
        sb.append(",                 managedSysId='").append(managedSysId).append('\'');
        sb.append(",                 status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}
