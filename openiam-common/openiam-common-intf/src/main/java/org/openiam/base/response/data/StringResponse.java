package org.openiam.base.response.data;


import org.openiam.base.response.data.BaseDataResponse;

/**
 * Created by alexander on 12/08/16.
 */
//@XmlAccessorType(XmlAccessType.FIELD)
//@XmlType(name = "StringResponse", propOrder = {
//})
public class StringResponse extends BaseDataResponse<String> {

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("StringResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
