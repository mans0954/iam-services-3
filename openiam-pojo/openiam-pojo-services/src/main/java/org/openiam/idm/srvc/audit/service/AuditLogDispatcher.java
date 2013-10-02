package org.openiam.idm.srvc.audit.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.thread.Sweepable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("auditLogDispatcher")
public class AuditLogDispatcher implements SessionAwareMessageListener, Sweepable {

	private static Logger LOG = Logger.getLogger(AuditLogDispatcher.class);
	
	@Autowired
	private IdmAuditLogDAO auditLogDAO;
	
	private ConcurrentLinkedQueue<IdmAuditLogEntity> queue = new ConcurrentLinkedQueue<IdmAuditLogEntity>();
	
	@Override
	public void onMessage(Message message, Session session) throws JMSException {
		final IdmAuditLogEntity log = (IdmAuditLogEntity)((ObjectMessage)message).getObject();
		queue.add(log);
	}

	@Override
	@Transactional
	public void sweep() {
		final List<List<IdmAuditLogEntity>> batchList = new LinkedList<List<IdmAuditLogEntity>>();
		List<IdmAuditLogEntity> list = new ArrayList<IdmAuditLogEntity>(100);
		while(queue.peek() != null) {
			final IdmAuditLogEntity next = queue.poll();
			if(list.size() == 100) {
				batchList.add(list);
				list = new ArrayList<IdmAuditLogEntity>(100);
			}
			list.add(next);
		}
		
		if(list.size() > 0) {
			batchList.add(list);
		}
		
		for(final List<IdmAuditLogEntity> entityList : batchList) {
			batchInsert(entityList);
		}
	}

	public void batchInsert(final List<IdmAuditLogEntity> entityList) {
		if(CollectionUtils.isNotEmpty(entityList)) {
			auditLogDAO.save(entityList);
		}
	}
}
