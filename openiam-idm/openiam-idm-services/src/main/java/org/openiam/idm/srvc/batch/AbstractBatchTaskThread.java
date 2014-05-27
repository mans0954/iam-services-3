package org.openiam.idm.srvc.batch;

import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.idm.srvc.batch.service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

public abstract class AbstractBatchTaskThread implements Runnable {

    protected ApplicationContext ctx;
    protected BatchTaskEntity entity;
    @Value("${org.openiam.idm.system.user.id}")
    protected String systemUserId;
    @Autowired
    protected AuditLogService auditLogService;
    @Autowired
    protected BatchService batchService;

    public AbstractBatchTaskThread(final BatchTaskEntity entity,
            final ApplicationContext ctx) {
        this.entity = entity;
        this.ctx = ctx;
        ctx.getAutowireCapableBeanFactory().autowireBean(this);
    }

    protected void logSuccess() {
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(systemUserId);
        idmAuditLog.setAction(AuditAction.BATCH_TASK_EXECUTE.value());
        idmAuditLog.setAuditDescription(entity.getName());
        idmAuditLog.addAttribute(AuditAttributeName.URL, entity.getTaskUrl());
        idmAuditLog.setSessionID(ctx.getId());
        idmAuditLog.succeed();
        auditLogService.enqueue(idmAuditLog);
    }

    protected void logFail(Throwable e) {
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(systemUserId);
        idmAuditLog.setAction(AuditAction.BATCH_TASK_EXECUTE.value());
        idmAuditLog.setAuditDescription(entity.getName());
        idmAuditLog.addAttribute(AuditAttributeName.URL, entity.getTaskUrl());
        idmAuditLog.setSessionID(ctx.getId());
        idmAuditLog.fail();
        auditLogService.enqueue(idmAuditLog);
    }

    @Override
    public void run() {
        onStart();
        doRun();
        onDone();
    }

    protected void onStart() {

    }

    protected void onDone() {

    }

    protected abstract void doRun();
}
