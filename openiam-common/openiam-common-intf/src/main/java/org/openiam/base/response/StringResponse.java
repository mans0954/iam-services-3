package org.openiam.base.response;

import org.openiam.base.ws.Response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by alexander on 12/08/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StringResponse", propOrder = {
        "value"
})
public class StringResponse extends Response {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("StringResponse{");
        sb.append(super.toString());
        sb.append(", value='").append(value).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
