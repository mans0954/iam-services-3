package org.openiam.idm.srvc.msg.service;

import javax.annotation.PostConstruct;

import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.gateway.RequestServiceGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component("mailSender")
public class MailSender {
    
    @Autowired
    private RequestServiceGateway requestServiceGateway;
    
    @Autowired
    @Qualifier("scheduler")
    private ThreadPoolTaskScheduler scheduler;
    
    @PostConstruct
    public void init() {

    }

    public void send(final Message mail) {
    	if(mail.getProcessingTime() != null) {
    		scheduler.schedule(new ScheduledMail(mail), mail.getProcessingTime());
    	} else {
			doSend(mail);
    	}
    }
    
    private class ScheduledMail implements Runnable {
    	
    	Message mail;
    	
    	ScheduledMail(final Message mail) {
    		this.mail = mail;
    	}

		@Override
		public void run() {
			doSend(mail);
		}
    	
    }

	private void doSend(final Message mail){
		MQRequest<Message> mqRequest = new MQRequest<>();
		mqRequest.setRequestBody(mail);
		mqRequest.setRequestApi(OpenIAMAPI.UpdateAttributesByMetadata);
		requestServiceGateway.send(OpenIAMQueue.MailQueue, mqRequest);
	}
}
