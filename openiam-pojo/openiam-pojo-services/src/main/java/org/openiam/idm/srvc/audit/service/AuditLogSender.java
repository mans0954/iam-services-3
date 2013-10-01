package org.openiam.idm.srvc.audit.service;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;

import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.msg.service.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component("auditLogSender")
public class AuditLogSender {

	@Autowired
    private JmsTemplate jmsTemplate;
	
	@Autowired
    @Qualifier(value = "logQueue")
    private Queue queue;
	
	 public void send(final IdmAuditLogEntity log) {
		 jmsTemplate.send(queue, new MessageCreator() {
			 public javax.jms.Message createMessage(Session session) throws JMSException {
				 javax.jms.Message message = session.createObjectMessage(log);
				 return message;
			 }
		 });
	 }
}
