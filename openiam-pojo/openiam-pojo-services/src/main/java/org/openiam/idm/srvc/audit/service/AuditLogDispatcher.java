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


    @Override
    public void sweep() {
        jmsTemplate.browse(queue, new BrowserCallback<Object>() {
            @Override
            public Object doInJms(Session session, QueueBrowser browser) throws JMSException {
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
                            for (final Set<IdmAuditLogEntity> entityList : batchList) {
                                process(entityList);
                            }
                        }
                    } finally {
                        LOG.info(String.format("Done with audit logger sweeper thread.  Took %s ms", sw.getTime()));
                    }
                return null;
            }
        });
    }

    private void process(final Collection<IdmAuditLogEntity> entityList) {
        if (CollectionUtils.isNotEmpty(entityList)) {
            for (IdmAuditLogEntity auditLogEntity : entityList) {
                if (StringUtils.isNotEmpty(auditLogEntity.getId())) {
                    IdmAuditLogEntity srcEntity = auditLogDAO.findById(auditLogEntity.getId());
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
                } else {
                    auditLogDAO.persist(auditLogEntity);
                }
            }
        }
    }
}
