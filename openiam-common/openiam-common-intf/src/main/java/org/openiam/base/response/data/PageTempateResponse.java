package org.openiam.base.response.data;

import org.openiam.idm.srvc.meta.dto.PageTempate;

/**
 * Created by alexander on 01/12/16.
 */
public class PageTempateResponse extends BaseDataResponse<PageTempate> {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PageTempateResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
