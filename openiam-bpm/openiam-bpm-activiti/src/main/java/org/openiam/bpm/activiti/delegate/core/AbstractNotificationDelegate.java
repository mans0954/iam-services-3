package org.openiam.bpm.activiti.delegate.core;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.el.FixedValue;
import org.apache.commons.lang.StringUtils;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractNotificationDelegate extends AbstractActivitiJob {
	
	private FixedValue operation;
	private FixedValue targetVariable;

	@Autowired
	protected ActivitiHelper activitiHelper;
	
	protected String getOperation() {
		return (operation != null) ? StringUtils.trimToNull(operation.getExpressionText()) : null;
	}
	
	protected ActivitiConstants getTargetVariable() {
		final ActivitiConstants retVal =  (targetVariable != null) ? ActivitiConstants.getByDeclarationName(StringUtils.trimToNull(targetVariable.getExpressionText())) : null;
		return retVal;
	}

	protected void sendNotification(final UserEntity toNotify,
		  	final UserEntity targetUser,
		  	final DelegateExecution execution) {
		
		final String taskName = getStringVariable(execution, ActivitiConstants.TASK_NAME);
		final String taskDescription = getStringVariable(execution, ActivitiConstants.TASK_DESCRIPTION);
		final String comment = getStringVariable(execution, ActivitiConstants.COMMENT);
		
		final String taskOwner = getRequestorId(execution);
		final UserEntity owner = getUserEntity(taskOwner);
		
		final NotificationRequest request = new NotificationRequest();
		request.setUserId(toNotify.getUserId());
		request.setNotificationType(getNotificationType());
		request.getParamList().add(new NotificationParam("TO_NOTIFY", toNotify));
		request.getParamList().add(new NotificationParam("TARGET_USER", targetUser));
		request.getParamList().add(new NotificationParam("REQUESTOR", owner));
		request.getParamList().add(new NotificationParam("COMMENT", comment));
		request.getParamList().add(new NotificationParam("REQUEST_REASON", taskName));
		request.getParamList().add(new NotificationParam("REQUEST_DESCRIPTION", taskDescription));
		mailService.sendNotification(request);
	}
	
	protected abstract String getNotificationType();
}