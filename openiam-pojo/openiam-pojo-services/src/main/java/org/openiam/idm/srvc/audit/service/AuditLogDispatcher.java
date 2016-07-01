package org.openiam.idm.srvc.audit.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.thread.Sweepable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component("auditLogDispatcher")
public class AuditLogDispatcher implements Sweepable {

	private static final Log LOG = LogFactory.getLog(AuditLogDispatcher.class);

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
	@Override
	@Scheduled(fixedRate=500, initialDelay=500)
	public void sweep() {
		final Long size = redisTemplate.opsForList().size("logQueue");
		if(size != null) {
			for(long i = 0; i < size.intValue() ; i++) {
				final Object key = redisTemplate.opsForList().rightPop("logQueue");
				if(key != null) {
					final IdmAuditLogEntity log = (IdmAuditLogEntity)key;
					process(log);
				}
			}
		}
	}
    
    private void process(final IdmAuditLogEntity event) {
        if (StringUtils.isNotEmpty(event.getId())) {
        	final IdmAuditLogEntity srcLog = auditLogService.findById(event.getId());
            if (srcLog != null) {
            	/*
                for(IdmAuditLogEntity customLog : event.getCustomRecords()) {
                    if(!srcLog.getCustomRecords().contains(customLog)){
                        srcLog.addCustomRecord(customLog.getKey(),customLog.getValue());
                    }
                }
                for(IdmAuditLogEntity newChildren : event.getChildLogs()) {
                   if(!srcLog.getChildLogs().contains(newChildren)) {
                       srcLog.addChild(newChildren);
                   }
                }
                for(AuditLogTarget newTarget : event.getTargets()) {
                     if(!srcLog.getTargets().contains(newTarget)) {
                        srcLog.addTarget(newTarget.getId(), newTarget.getTargetType(), newTarget.getObjectPrincipal());
                    }
                }
                */
                auditLogService.save(srcLog);
            }
        } else {
            auditLogService.save(event);
        }
    }
}
