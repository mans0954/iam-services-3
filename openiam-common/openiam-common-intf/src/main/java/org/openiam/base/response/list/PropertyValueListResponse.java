package org.openiam.base.response.list;

import org.openiam.property.dto.PropertyValue;

/**
 * Created by alexander on 06/12/16.
 */
public class PropertyValueListResponse extends BaseListResponse<PropertyValue> {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PropertyValueListResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
