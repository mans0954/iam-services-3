package org.openiam.idm.srvc.batch.thread;

import java.util.List;

import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.idm.srvc.batch.domain.BatchTaskScheduleEntity;
import org.openiam.idm.srvc.batch.service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

public abstract class AbstractBatchTaskThread implements Runnable {

    protected ApplicationContext ctx;
    protected BatchTaskEntity entity;
    protected List<BatchTaskScheduleEntity> scheduledTasks;
    @Value("${org.openiam.idm.system.user.id}")
    protected String systemUserId;
    @Autowired
    protected AuditLogService auditLogService;
    @Autowired
    protected BatchService batchService;

    public AbstractBatchTaskThread(final BatchTaskEntity entity, final ApplicationContext ctx, final List<BatchTaskScheduleEntity> scheduledTasks) {
        this.entity = entity;
        this.ctx = ctx;
        this.scheduledTasks = scheduledTasks;
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
    	try {
    		markAsRunning();
    		onStart();
    		doRun();
    		onDone();
    	} finally {
    		setBatchScheduledTasksToCompleted();
    	}
    }

    protected void onStart() {

    }

    protected void onDone() {

    }
    
    private void markAsRunning() {
    	if(scheduledTasks != null) {
    		scheduledTasks.forEach(schedule -> {
    			//batchService.markTaskAsCompleted(schedule.getId());
    			batchService.markTaskAsRunning(schedule.getId());
    		});
    	}
    }
    
    private void setBatchScheduledTasksToCompleted() {
    	if(scheduledTasks != null) {
    		scheduledTasks.forEach(schedule -> {
    			batchService.markTaskAsCompleted(schedule.getId());
    		});
    	}
    }

    protected abstract void doRun();
}
