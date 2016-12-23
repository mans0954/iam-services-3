package org.openiam.base.response.list;

import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;

/**
 * Created by Alexander Dukkardt on 2016-12-22.
 */
public class UserIdentityAnswerListResponse extends BaseListResponse<UserIdentityAnswer> {
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserIdentityAnswerListResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
