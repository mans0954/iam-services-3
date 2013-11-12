package org.openiam.bpm.activiti.delegate.core;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.el.FixedValue;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractNotificationDelegate extends AbstractDelegate {
	
	private FixedValue operation;
	

	@Autowired
	protected ActivitiHelper activitiHelper;
	
	protected String getOperation() {
		return (operation != null) ? StringUtils.trimToNull(operation.getExpressionText()) : null;
	}

	protected void sendNotification(final UserEntity toNotify, 
		  	final UserEntity owner, 
		  	final UserEntity targetUser, 
		  	final String comment, 
		  	final String requestName, 
		  	final String requestDescription) {
		final NotificationRequest request = new NotificationRequest();
		request.setUserId(toNotify.getUserId());
		request.setNotificationType(getNotificationType());
		request.getParamList().add(new NotificationParam("TO_NOTIFY", toNotify));
		request.getParamList().add(new NotificationParam("TARGET_USER", targetUser));
		request.getParamList().add(new NotificationParam("REQUESTOR", owner));
		request.getParamList().add(new NotificationParam("COMMENT", comment));
		request.getParamList().add(new NotificationParam("REQUEST_REASON", requestName));
		request.getParamList().add(new NotificationParam("REQUEST_DESCRIPTION", requestDescription));
		mailService.sendNotification(request);
	}
	
	protected abstract String getNotificationType();
}
