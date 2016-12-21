package org.openiam.base.request;

/**
 * Created by aduckardt on 2016-12-19.
 */
public class DataEncryptionRequest extends BaseServiceRequest {
    private String userId;
    private String data;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DataEncryptionRequest{");
        sb.append(super.toString());
        sb.append(",                 userId='").append(userId).append('\'');
        sb.append(",                 data='").append(data).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
