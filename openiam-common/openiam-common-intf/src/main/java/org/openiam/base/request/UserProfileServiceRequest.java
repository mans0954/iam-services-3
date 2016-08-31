package org.openiam.base.request;

import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;

/**
 * Created by alexander on 31/08/16.
 */
public class UserProfileServiceRequest extends BaseServiceRequest {
    private UserProfileRequestModel model;

    public UserProfileRequestModel getModel() {
        return model;
    }

    public void setModel(UserProfileRequestModel model) {
        this.model = model;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UserProfileServiceRequest{");
        sb.append(super.toString());
        sb.append(", model=").append(model);
        sb.append('}');
        return sb.toString();
    }
}
