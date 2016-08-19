package org.openiam.base.request;

import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;

/**
 * Created by alexander on 08/08/16.
 */
public class IdmAuditLogRequest extends BaseServiceRequest {
    private IdmAuditLogEntity logEntity;

    public IdmAuditLogEntity getLogEntity() {
        return logEntity;
    }

    public void setLogEntity(IdmAuditLogEntity logEntity) {
        this.logEntity = logEntity;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("IdmAuditLogRequest{");
        sb.append("logEntity=").append(logEntity);
        sb.append('}');
        return sb.toString();
    }
}
