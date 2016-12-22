package org.openiam.base.response.list;

import org.openiam.idm.srvc.pswd.dto.IdentityQuestGroup;

/**
 * Created by Alexander Dukkardt on 2016-12-22.
 */
public class IdentityQuestGroupListResponse extends BaseListResponse<IdentityQuestGroup> {
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IdentityQuestGroupListResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
