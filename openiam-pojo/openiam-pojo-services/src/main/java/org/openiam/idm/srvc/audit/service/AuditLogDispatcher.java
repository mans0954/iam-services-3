package org.openiam.idm.srvc.audit.service;

import java.util.*;

import javax.jms.*;
import javax.jms.Queue;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.thread.Sweepable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void sweep() {
        jmsTemplate.browse(queue, new BrowserCallback<Object>() {
            @Override
            public Object doInJms(Session session, QueueBrowser browser) throws JMSException {
                final StopWatch sw = new StopWatch();
                sw.start();
                try {
                    LOG.info("Starting audit log sweeper thread");

                    final List<Set<IdmAuditLogEntity>> batchList = new LinkedList<Set<IdmAuditLogEntity>>();
                    Set<IdmAuditLogEntity> list = new HashSet<IdmAuditLogEntity>(100);
                    Enumeration e = browser.getEnumeration();
                    int count = 0;
                    while (e.hasMoreElements()) {
                        list.add((IdmAuditLogEntity) ((ObjectMessage) jmsTemplate.receive(queue)).getObject());
                        if (count++ >= 100) {
                            batchList.add(list);
                            list = new HashSet<IdmAuditLogEntity>(100);
                        }
                        e.nextElement();
                    }
                    batchList.add(list);
                    for (final Set<IdmAuditLogEntity> entityList : batchList) {
                        process(entityList);
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
                    auditLogDAO.merge(auditLogEntity);
                } else {
                    auditLogDAO.persist(auditLogEntity);
                }
            }
        }
    }
}
