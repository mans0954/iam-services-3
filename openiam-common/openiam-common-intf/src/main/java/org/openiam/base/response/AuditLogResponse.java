package org.openiam.base.response;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;

/**
 * Created by alexander on 29/08/16.
 */
public class AuditLogResponse extends Response {
    private IdmAuditLogEntity event;

    public IdmAuditLogEntity getEvent() {
        return event;
    }

    public void setEvent(IdmAuditLogEntity event) {
        this.event = event;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AuditLogResponse{");
        sb.append(super.toString());
        sb.append(", event=").append(event);
        sb.append('}');
        return sb.toString();
    }
}
