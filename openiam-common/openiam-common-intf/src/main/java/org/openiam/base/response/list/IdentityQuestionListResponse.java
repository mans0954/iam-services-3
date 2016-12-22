package org.openiam.base.response.list;

import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;

/**
 * Created by Alexander Dukkardt on 2016-12-22.
 */
public class IdentityQuestionListResponse extends BaseListResponse<IdentityQuestion> {
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IdentityQuestionListResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
