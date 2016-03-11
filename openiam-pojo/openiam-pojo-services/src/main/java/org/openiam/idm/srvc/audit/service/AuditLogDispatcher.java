package org.openiam.idm.srvc.audit.service;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.QueueBrowser;
import javax.jms.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.thread.Sweepable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Component("auditLogDispatcher")
public class AuditLogDispatcher {

	private static final Log LOG = LogFactory.getLog(AuditLogDispatcher.class);

    @Autowired
    private AuditLogService auditLogService;
    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager platformTransactionManager;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private RedisMessageListenerContainer listener;
    
    @PostConstruct
    public void init() {
    	listener.addMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message message, byte[] pattern) {
				final IdmAuditLogEntity log = (IdmAuditLogEntity)redisTemplate.getDefaultSerializer().deserialize(message.getBody());
				process(log);
			}
		}, Arrays.asList(new Topic[] { new ChannelTopic("logQueue")}));
    }
    
    
    private void persist(final List<IdmAuditLogEntity> messageList) {
    	/*
    	 * This goes to elastic search as of V4, so we no longer need a transaction
    	 * 
    	 * 
    	 * 
    	TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
        Boolean result = transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
            	for(final IdmAuditLogEntity message : messageList) {
                    process(message);
           	 	}
                return true;
            }
    	});
    	*/
    	
    	for(final IdmAuditLogEntity message : messageList) {
            process(message);
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
