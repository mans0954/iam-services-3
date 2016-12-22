package org.openiam.base.response.data;

import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;

/**
 * Created by Alexander Dukkardt on 2016-12-22.
 */
public class IdentityQuestionResponse extends BaseDataResponse<IdentityQuestion> {
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IdentityQuestionResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
