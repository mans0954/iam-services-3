package org.openiam.idm.srvc.batch;

import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.util.SpringContextProvider;
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

    public AbstractBatchTaskThread(final BatchTaskEntity entity,
            final ApplicationContext ctx) {
        this.entity = entity;
        this.ctx = ctx;
        ctx.getAutowireCapableBeanFactory().autowireBean(this);
    }

    protected void logSuccess() {
        auditLogService.enqueue(new AuditLogBuilder()
                .setRequestorUserId(systemUserId)
                .setAction(AuditAction.BATCH_TASK_EXECUTE)
                .setAuditDescription(entity.getName())
                .addAttribute(AuditAttributeName.URL, entity.getTaskUrl())
                .setSessionID(ctx.getId()).succeed());
    }

    protected void logFail(Throwable e) {
        auditLogService.enqueue(new AuditLogBuilder()
                .setRequestorUserId(systemUserId)
                .setAction(AuditAction.BATCH_TASK_EXECUTE)
                .setAuditDescription(entity.getName())
                .addAttribute(AuditAttributeName.URL, entity.getTaskUrl())
                .setSessionID(ctx.getId()).fail());
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
