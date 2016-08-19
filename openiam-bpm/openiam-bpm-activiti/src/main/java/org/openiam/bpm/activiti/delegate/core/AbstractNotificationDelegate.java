package org.openiam.bpm.activiti.delegate.core;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.base.request.NotificationParam;
import org.openiam.base.request.NotificationRequest;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractNotificationDelegate extends AbstractActivitiJob {

	@Autowired
	protected ActivitiHelper activitiHelper;

	protected void sendNotification(final UserEntity toNotify,
		  							final UserEntity targetUser,
		  							final DelegateExecution execution) {
		
		final String taskName = getStringVariable(execution, ActivitiConstants.TASK_NAME);
		final String taskDescription = getStringVariable(execution, ActivitiConstants.TASK_DESCRIPTION);
        final String cardinalityUserId = getStringVariable(execution, ActivitiConstants.CARDINALITY_OBJECT);
		
		final String taskOwner = getRequestorId(execution);
		final UserEntity owner = getUserEntity(taskOwner);
        final UserEntity cardinalityUser = getUserEntity(cardinalityUserId);
		
		final NotificationRequest request = new NotificationRequest();
		request.setUserId(toNotify.getId());
		request.setNotificationType(getNotificationType(execution));
		request.getParamList().add(new NotificationParam("TO_NOTIFY", toNotify));
		request.getParamList().add(new NotificationParam("TARGET_USER", targetUser));
		request.getParamList().add(new NotificationParam("REQUESTOR", owner));
		request.getParamList().add(new NotificationParam("COMMENT", getComment(execution)));
		request.getParamList().add(new NotificationParam("REQUEST_REASON", taskName));
		request.getParamList().add(new NotificationParam("REQUEST_DESCRIPTION", taskDescription));
        if (cardinalityUser != null) {
            request.getParamList().add(new NotificationParam("CARDINALITY_USER", cardinalityUser));
        }
		mailService.sendNotification(request);
	}
}
