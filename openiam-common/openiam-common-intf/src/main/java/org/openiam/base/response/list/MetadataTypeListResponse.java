package org.openiam.base.response.list;

import org.openiam.idm.srvc.meta.dto.MetadataType;

/**
 * Created by alexander on 05/12/16.
 */
public class MetadataTypeListResponse extends BaseListResponse<MetadataType> {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MetadataTypeListResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
