package org.openiam.base.response.list;

import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplate;

/**
 * Created by alexander on 01/12/16.
 */
public class MetadataElementPageTemplateListResponse extends BaseListResponse<MetadataElementPageTemplate> {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MetadataElementPageTemplateListResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
