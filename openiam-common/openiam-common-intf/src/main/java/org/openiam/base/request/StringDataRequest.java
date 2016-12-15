package org.openiam.base.request;

/**
 * Created by aduckardt on 2016-12-12.
 */
public class StringDataRequest extends BaseServiceRequest {
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("StringDataRequest{");
        sb.append(super.toString());
        sb.append(",                 data='").append(data).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
