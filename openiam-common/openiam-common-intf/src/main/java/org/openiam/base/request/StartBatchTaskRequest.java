package org.openiam.base.request;

import java.util.Date;

/**
 * Created by alexander on 25/10/16.
 */
public class StartBatchTaskRequest extends BaseServiceRequest {
    private String id;
    private boolean synchronous;
    private Date when;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSynchronous() {
        return synchronous;
    }

    public void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
    }

    public Date getWhen() {
        return when;
    }

    public void setWhen(Date when) {
        this.when = when;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("StartBatchTaskRequest{");
        sb.append(super.toString());
        sb.append(", id='").append(id).append('\'');
        sb.append(", synchronous=").append(synchronous);
        sb.append(", when=").append(when);
        sb.append('}');
        return sb.toString();
    }
}
