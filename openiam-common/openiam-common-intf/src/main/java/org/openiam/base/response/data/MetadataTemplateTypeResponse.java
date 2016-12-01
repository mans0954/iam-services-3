package org.openiam.base.response.data;

import org.openiam.idm.srvc.meta.dto.MetadataTemplateType;

/**
 * Created by alexander on 01/12/16.
 */
public class MetadataTemplateTypeResponse extends BaseDataResponse<MetadataTemplateType> {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MetadataTemplateTypeResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
