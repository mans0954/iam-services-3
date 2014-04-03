package org.openiam.idm.srvc.audit.service;

import java.util.*;

import javax.jms.*;
import javax.jms.Queue;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.openiam.dozer.converter.IdmAuditLogCustomDozerConverter;
import org.openiam.dozer.converter.IdmAuditLogDozerConverter;
import org.openiam.dozer.converter.IdmAuditLogTargetDozerConverter;
import org.openiam.idm.srvc.audit.domain.AuditLogTargetEntity;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogCustomEntity;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.dto.AuditLogTarget;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.dto.IdmAuditLogCustom;
import org.openiam.thread.Sweepable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Component("auditLogDispatcher")
public class AuditLogDispatcher implements Sweepable {

    private static Logger LOG = Logger.getLogger(AuditLogDispatcher.class);

    @Autowired
    private AuditLogService auditLogService;
    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier(value = "logQueue")
    private Queue queue;

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
    public void sweep() {
        jmsTemplate.browse(queue, new BrowserCallback<Object>() {
            @Override
            public Object doInJms(Session session, QueueBrowser browser) throws JMSException {
              synchronized (mutex){

               final StopWatch sw = new StopWatch();
                sw.start();
                    try {
                        LOG.info("Starting audit log sweeper thread");

                        Enumeration e = browser.getEnumeration();

                        while (e.hasMoreElements()) {
                            final IdmAuditLog message = (IdmAuditLog) ((ObjectMessage) jmsTemplate.receive(queue)).getObject();

                            TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
                            transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
                            Boolean result = transactionTemplate.execute(new TransactionCallback<Boolean>() {
                                @Override
                                public Boolean doInTransaction(TransactionStatus status) {
                                        process(message);
                                        try {
                                            // to give other threads chance to be executed
                                            Thread.sleep(100);
                                        } catch (InterruptedException e1) {
                                            LOG.warn(e1.getMessage());
                                        }

                                    return true;
                                }});

                            e.nextElement();
                        }

                    } finally {
                        LOG.info(String.format("Done with audit logger sweeper thread.  Took %s ms", sw.getTime()));
                    }
                return null;
            }
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
                                srcLog.addTarget(newTarget.getId(), newTarget.getTargetType());
                            }
                        }
                        auditLogService.save(srcLog);
                    }
                } else {
                    auditLogService.save(event);
                }


    }
}
