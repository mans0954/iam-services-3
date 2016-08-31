package org.openiam.base.request;

/**
 * Created by alexander on 08/08/16.
 */
public class PolicyGetAppPolicyAttrubutesRequest extends IdServiceRequest {
    private String pswdGroup;

    public String getPswdGroup() {
        return pswdGroup;
    }

    public void setPswdGroup(String pswdGroup) {
        this.pswdGroup = pswdGroup;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PolicyGetAppPolicyAttrubutesRequest{");
        sb.append("pswdGroup='").append(pswdGroup).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
