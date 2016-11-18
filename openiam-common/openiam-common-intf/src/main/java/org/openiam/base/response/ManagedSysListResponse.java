package org.openiam.base.response;

import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;


/**
 * Created by alexander on 06/09/16.
 */
public class ManagedSysListResponse extends BaseListResponse<ManagedSysDto> {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ManagedSysListResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
