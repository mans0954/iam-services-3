package org.openiam.base.request;

import org.openiam.idm.srvc.pswd.dto.Password;

/**
 * Created by aduckardt on 2016-12-16.
 */
public class PasswordRequest extends BaseServiceRequest {
    private Password password;

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PasswordRequest{");
        sb.append(super.toString());
        sb.append(",                 password=").append(password);
        sb.append('}');
        return sb.toString();
    }
}
