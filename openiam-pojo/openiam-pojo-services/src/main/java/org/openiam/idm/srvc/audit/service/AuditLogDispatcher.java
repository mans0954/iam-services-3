package org.openiam.idm.srvc.audit.service;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.QueueBrowser;
import javax.jms.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.converter.IdmAuditLogCustomDozerConverter;
import org.openiam.dozer.converter.IdmAuditLogDozerConverter;
import org.openiam.dozer.converter.IdmAuditLogTargetDozerConverter;
import org.openiam.idm.srvc.audit.dto.AuditLogTarget;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.dto.IdmAuditLogCustom;
import org.openiam.thread.Sweepable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Component("auditLogDispatcher")
public class AuditLogDispatcher implements Sweepable {

	private static final Log LOG = LogFactory.getLog(AuditLogDispatcher.class);

    @Autowired
    private AuditLogService auditLogService;
    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager platformTransactionManager;
    private final Object mutex = new Object();

    @Autowired
    private IdmAuditLogDozerConverter idmAuditLogDozerConverter;

    @Autowired
    private IdmAuditLogTargetDozerConverter idmAuditLogTargetDozerConverter;

    @Autowired
    private IdmAuditLogCustomDozerConverter idmAuditLogCustomDozerConverter;

    @Override
    @Scheduled(fixedRateString="${org.openiam.audit.threadsweep}", initialDelayString="${org.openiam.audit.threadsweep}")
    public void sweep() {
        jmsTemplate.browse("logQueue", new BrowserCallback<Object>() {
            @Override
            public Object doInJms(Session session, QueueBrowser browser) throws JMSException {
              synchronized (mutex){

               final StopWatch sw = new StopWatch();
                sw.start();
                    try {
                        LOG.info("Starting audit log sweeper thread");

                        Enumeration e = browser.getEnumeration();

                        final List<IdmAuditLog> messageList = new LinkedList<>();
                        while (e.hasMoreElements()) {
                            final IdmAuditLog messageObject = (IdmAuditLog) ((ObjectMessage) jmsTemplate.receive("logQueue")).getObject();

                            messageList.add(messageObject);
                            if(messageList.size() > 100) {
                            	persist(messageList);
                            	messageList.clear();
                            }
                            /*
                             * comment by Lev Bornovalov
                             * This code:
                             * 1) Unnecessarily waits 500ms upon EACH iteration.  This will cause the JMS Queue to fill up to the maximum size upon very high load (due to the blocking of the 500 ms)
                             * 2) Does an insert into the DB on EVERY SINGLE iteration.
                             * 
                             *  Solution:  let's insert 100 at a time in a single transaction.  If the app crashes during this transactio - no big deal - it's just audit info.
                             * 
                             */
                            /*
                            TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
                            transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
                            Boolean result = transactionTemplate.execute(new TransactionCallback<Boolean>() {
                                @Override
                                public Boolean doInTransaction(TransactionStatus status) {
                                        process(message);
                                        try {
                                            // to give other threads chance to be executed
                                            Thread.sleep(500);
                                        } catch (InterruptedException e1) {
                                            LOG.warn(e1.getMessage());
                                        }
                                    return true;
                                }
                        	});
        					*/
                           e.nextElement();
                        }
                        if(messageList.size() > 0) {
                        	persist(messageList);
                        	messageList.clear();
                        }
                    } finally {
                        LOG.info(String.format("Done with audit logger sweeper thread.  Took %s ms", sw.getTime()));
                    }
                return null;
            }
        }
        });
    }
    
    private void persist(final List<IdmAuditLog> messageList) {
    	TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
        Boolean result = transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
            	for(final IdmAuditLog message : messageList) {
                    process(message);
           	 	}
                return true;
            }
    	});
    }

    private void process(final IdmAuditLog event) {
        if (StringUtils.isNotEmpty(event.getId())) {
            IdmAuditLog srcLog = auditLogService.findById(event.getId());
            if (srcLog != null) {
                for(IdmAuditLogCustom customLog : event.getCustomRecords()) {
                    if(!srcLog.getCustomRecords().contains(customLog)){
                        srcLog.addCustomRecord(customLog.getKey(),customLog.getValue());
                    }
                }
                for(IdmAuditLog newChildren : event.getChildLogs()) {
                   if(!srcLog.getChildLogs().contains(newChildren)) {
                       srcLog.addChild(newChildren);
                   }
                }
                for(AuditLogTarget newTarget : event.getTargets()) {
                     if(!srcLog.getTargets().contains(newTarget)) {
                        srcLog.addTarget(newTarget.getId(), newTarget.getTargetType(), newTarget.getObjectPrincipal());
                    }
                }
                auditLogService.save(srcLog);
            }
        } else {
            auditLogService.save(event);
        }
    }
}
