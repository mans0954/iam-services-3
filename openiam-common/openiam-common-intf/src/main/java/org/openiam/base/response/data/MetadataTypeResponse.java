package org.openiam.base.response.data;

import org.openiam.idm.srvc.meta.dto.MetadataType;

/**
 * Created by alexander on 05/12/16.
 */
public class MetadataTypeResponse extends BaseDataResponse<MetadataType> {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MetadataTypeResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
