package org.openiam.base.request;

import java.util.Date;

/**
 * Created by alexander on 12/09/16.
 */
public class UserEntitlementsMatrixRequest extends BaseServiceRequest {
    private String userId;
    private Date date;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UserEntitlementsMatrixRequest{");
        sb.append(super.toString());
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", date=").append(date);
        sb.append('}');
        return sb.toString();
    }
}
