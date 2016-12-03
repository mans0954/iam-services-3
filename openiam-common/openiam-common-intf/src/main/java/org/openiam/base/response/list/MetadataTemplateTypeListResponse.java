package org.openiam.base.response.list;

import org.openiam.idm.srvc.meta.dto.MetadataTemplateType;

/**
 * Created by alexander on 01/12/16.
 */
public class MetadataTemplateTypeListResponse extends BaseListResponse<MetadataTemplateType> {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MetadataTemplateTypeListResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
