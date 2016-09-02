package org.openiam.base.response;

import org.openiam.base.ws.Response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by alexander on 12/08/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IntResponse", propOrder = {
        "value"
})
public class IntResponse extends Response {
    private Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("IntResponse{");
        sb.append(super.toString());
        sb.append(", value='").append(value).append('\'');
        sb.append('}');
        return sb.toString();
    }

    protected Object getValueInternal(){
        return this.value;
    }
}
