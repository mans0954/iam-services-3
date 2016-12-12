package org.openiam.base.response.data;

import org.openiam.idm.srvc.meta.dto.MetadataElement;

/**
 * Created by alexander on 02/12/16.
 */
public class MetadataElementResponse extends BaseDataResponse<MetadataElement> {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MetadataElementResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
