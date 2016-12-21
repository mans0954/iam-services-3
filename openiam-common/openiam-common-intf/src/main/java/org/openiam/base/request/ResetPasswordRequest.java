package org.openiam.base.request;

/**
 * Created by Alexander Dukkardt on 2016-12-20.
 */
public class ResetPasswordRequest extends BaseServiceRequest {
    private String principal;
    private String managedSysId;
    private String contentProviderId;
    private String password;
    private boolean notifyUserViaEmail;

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

    public String getContentProviderId() {
        return contentProviderId;
    }

    public void setContentProviderId(String contentProviderId) {
        this.contentProviderId = contentProviderId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isNotifyUserViaEmail() {
        return notifyUserViaEmail;
    }

    public void setNotifyUserViaEmail(boolean notifyUserViaEmail) {
        this.notifyUserViaEmail = notifyUserViaEmail;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ResetPasswordRequest{");
        sb.append(super.toString());
        sb.append(",                 principal='").append(principal).append('\'');
        sb.append(",                 managedSysId='").append(managedSysId).append('\'');
        sb.append(",                 contentProviderId='").append(contentProviderId).append('\'');
        sb.append(",                 password='").append(password).append('\'');
        sb.append(",                 notifyUserViaEmail=").append(notifyUserViaEmail);
        sb.append('}');
        return sb.toString();
    }
}
