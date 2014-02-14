package org.openiam.idm.srvc.audit.service;

import java.util.*;

import javax.jms.*;
import javax.jms.Queue;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogCustomEntity;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
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
    private IdmAuditLogDAO auditLogDAO;
    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier(value = "logQueue")
    private Queue queue;

    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager platformTransactionManager;
    private final Object mutex = new Object();

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

                        final List<Set<IdmAuditLogEntity>> batchList = new LinkedList<Set<IdmAuditLogEntity>>();
                        Set<IdmAuditLogEntity> set = new HashSet<IdmAuditLogEntity>();
                        batchList.add(set);
                        Enumeration e = browser.getEnumeration();
                        int count = 0;
                        while (e.hasMoreElements()) {
                            IdmAuditLogEntity message = (IdmAuditLogEntity) ((ObjectMessage) jmsTemplate.receive(queue)).getObject();
                            set.add(message);
                            if (count++ >= 100) {
                                set = new HashSet<IdmAuditLogEntity>();
                                batchList.add(set);
                            }
                            e.nextElement();
                        }


                        if (batchList.size() > 0 && batchList.get(0) != null && batchList.get(0).size() > 0) {
                           TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
                            transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
                            Boolean result = transactionTemplate.execute(new TransactionCallback<Boolean>() {
                                @Override
                                public Boolean doInTransaction(TransactionStatus status) {
                                    for (final Set<IdmAuditLogEntity> entityList : batchList) {
                                        process(entityList);
                                        try {
                                            // to give other threads chance to be executed
                                            Thread.sleep(100);
                                        } catch (InterruptedException e1) {
                                            LOG.warn(e1.getMessage());
                                        }
                                    }
                                    return true;
                                }});

                        }
                    } finally {
                        LOG.info(String.format("Done with audit logger sweeper thread.  Took %s ms", sw.getTime()));
                    }
                return null;
            }
        }
        });

    }

    private void process(final Collection<IdmAuditLogEntity> entityList) {
        if (CollectionUtils.isNotEmpty(entityList)) {
            for (IdmAuditLogEntity auditLogEntity : entityList) {
                if (StringUtils.isNotEmpty(auditLogEntity.getId())) {
                    IdmAuditLogEntity srcEntity = auditLogDAO.findById(auditLogEntity.getId());
                    if (srcEntity != null) {
                        for(IdmAuditLogCustomEntity customEntity : auditLogEntity.getCustomRecords()) {
                            if(!srcEntity.getCustomRecords().contains(customEntity)){
                                srcEntity.addCustomRecord(customEntity.getKey(),customEntity.getValue());
                            }
                        }
                        for(IdmAuditLogEntity newChildren : auditLogEntity.getChildLogs()) {
                           if(!srcEntity.getChildLogs().contains(newChildren)) {
                               srcEntity.addChild(newChildren);
                           }
                        }
                        auditLogDAO.merge(srcEntity);
                    }
                } else {
                    auditLogDAO.persist(auditLogEntity);
                }
            }
        }
    }
}
