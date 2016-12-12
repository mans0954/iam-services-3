package org.openiam.base.request;

import org.openiam.property.dto.PropertyValue;

import java.util.List;

/**
 * Created by alexander on 06/12/16.
 */
public class PropertyValueCrudRequest extends BaseCrudServiceRequest<PropertyValue> {
    private List<PropertyValue> propertyValueList;

    public PropertyValueCrudRequest() {
        super(null);
    }

    public List<PropertyValue> getPropertyValueList() {
        return propertyValueList;
    }

    public void setPropertyValueList(List<PropertyValue> propertyValueList) {
        this.propertyValueList = propertyValueList;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PropertyValueCrudRequest{");
        sb.append(super.toString());
        sb.append(", propertyValueList=").append(propertyValueList);
        sb.append('}');
        return sb.toString();
    }
}
