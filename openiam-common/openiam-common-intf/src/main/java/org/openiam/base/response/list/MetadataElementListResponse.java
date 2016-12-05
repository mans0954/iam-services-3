package org.openiam.base.response.list;

import org.openiam.idm.srvc.meta.dto.MetadataElement;

/**
 * Created by alexander on 05/12/16.
 */
public class MetadataElementListResponse extends BaseListResponse<MetadataElement> {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MetadataElementListResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
