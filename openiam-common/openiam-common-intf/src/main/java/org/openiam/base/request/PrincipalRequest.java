package org.openiam.base.request;

import java.util.Date;

/**
 * Created by Alexander Dukkardt on 2016-12-20.
 */
public class PrincipalRequest extends BaseServiceRequest {
    private Date lastExecTime;
    private Integer startDays;
    private Integer endDays;

    public Date getLastExecTime() {
        return lastExecTime;
    }

    public void setLastExecTime(Date lastExecTime) {
        this.lastExecTime = lastExecTime;
    }

    public Integer getStartDays() {
        return startDays;
    }

    public void setStartDays(Integer startDays) {
        this.startDays = startDays;
    }

    public Integer getEndDays() {
        return endDays;
    }

    public void setEndDays(Integer endDays) {
        this.endDays = endDays;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PrincipalRequest{");
        sb.append("                lastExecTime=").append(lastExecTime);
        sb.append(",                 startDays=").append(startDays);
        sb.append(",                 endDays=").append(endDays);
        sb.append('}');
        return sb.toString();
    }
}
