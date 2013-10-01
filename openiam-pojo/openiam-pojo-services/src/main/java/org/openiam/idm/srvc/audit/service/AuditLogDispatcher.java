package org.openiam.idm.srvc.audit.service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Component;

@Component("auditLogDispatcher")
public class AuditLogDispatcher implements SessionAwareMessageListener {

	private static Logger LOG = Logger.getLogger(AuditLogDispatcher.class);
	
	@Override
	public void onMessage(Message message, Session session) throws JMSException {
		final IdmAuditLogEntity log = (IdmAuditLogEntity)((ObjectMessage)message).getObject();
		LOG.info(String.format("Received message: %s", log));
	}

}
