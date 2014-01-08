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

    private final Object mutext = new Object();

    @Override
    public void sweep() {
        jmsTemplate.browse(queue, new BrowserCallback<Object>() {
            @Override
            public Object doInJms(Session session, QueueBrowser browser) throws JMSException {
              //  synchronized (mutext) {
                final StopWatch sw = new StopWatch();
                sw.start();
                    try {
                        LOG.info("Starting audit log sweeper thread");

                        final List<Set<IdmAuditLogEntity>> batchList = new LinkedList<Set<IdmAuditLogEntity>>();
                        Set<IdmAuditLogEntity> list = new HashSet<IdmAuditLogEntity>();
                        Enumeration e = browser.getEnumeration();
                        int count = 0;
                        while (e.hasMoreElements()) {
                            IdmAuditLogEntity message = (IdmAuditLogEntity) ((ObjectMessage) jmsTemplate.receive(queue)).getObject();
                            list.add(message);
                            if (count++ >= 100) {
                                batchList.add(list);
                                list = new HashSet<IdmAuditLogEntity>();
                            }
                            e.nextElement();
                        }

                        if (list.size() > 0) {
                            batchList.add(list);
                            final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
                            transactionTemplate.execute(new TransactionCallback<Object>() {
                                @Override
                                public Object doInTransaction(TransactionStatus status) {

                                    for (final Set<IdmAuditLogEntity> entityList : batchList) {
                                        process(entityList);
                                    }
                                    return new Object();
                                }
                            });
                        }
                    } finally {
                        LOG.info(String.format("Done with audit logger sweeper thread.  Took %s ms", sw.getTime()));
                    }
                //}
                return null;
            }
        });
    }

    private void process(final Collection<IdmAuditLogEntity> entityList) {
        if (CollectionUtils.isNotEmpty(entityList)) {
            for (IdmAuditLogEntity auditLogEntity : entityList) {
                if (StringUtils.isNotEmpty(auditLogEntity.getId())) {
                    IdmAuditLogEntity srcEntity = auditLogDAO.findById(auditLogEntity.getId());
                    for(IdmAuditLogCustomEntity customEntity : srcEntity.getCustomRecords()) {
                        if(auditLogEntity.getCustomRecords().contains(customEntity)){
                        for(IdmAuditLogCustomEntity srcCustomEntity : auditLogEntity.getCustomRecords()) {
                           if(customEntity.equals(srcCustomEntity)) {
                              customEntity.setId(srcCustomEntity.getId());
                               break;
                           }
                       }
                        } else {
                            auditLogEntity.addCustomRecord(customEntity.getKey(),customEntity.getValue());
                        }

                    }
                    for(IdmAuditLogEntity srcChildren : srcEntity.getChildLogs()) {
                       if(!auditLogEntity.getChildLogs().contains(srcChildren)) {
                           auditLogEntity.addChild(srcChildren);
                       }
                    }
                    for(IdmAuditLogEntity srcParent : srcEntity.getParentLogs()) {
                        if(!auditLogEntity.getParentLogs().contains(srcParent)) {
                            auditLogEntity.addParent(srcParent);
                        }
                    }
                    auditLogDAO.merge(auditLogEntity);
                } else {
                    auditLogDAO.persist(auditLogEntity);
                }
            }
        }
    }
}
