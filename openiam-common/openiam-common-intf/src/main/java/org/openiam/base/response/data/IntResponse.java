package org.openiam.base.response.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by alexander on 12/08/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IntResponse", propOrder = {
})
public class IntResponse extends BaseDataResponse<Integer> {

    protected Object getValueInternal(){
        return super.getValue();
    }
}
