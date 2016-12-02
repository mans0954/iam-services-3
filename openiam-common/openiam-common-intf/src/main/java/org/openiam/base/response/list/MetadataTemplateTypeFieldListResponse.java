package org.openiam.base.response.list;

import org.openiam.idm.srvc.meta.dto.MetadataTemplateTypeField;

/**
 * Created by alexander on 01/12/16.
 */
public class MetadataTemplateTypeFieldListResponse extends BaseListResponse<MetadataTemplateTypeField> {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MetadataTemplateTypeFieldListResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
