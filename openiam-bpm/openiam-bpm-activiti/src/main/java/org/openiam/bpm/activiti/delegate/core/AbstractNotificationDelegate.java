package org.openiam.bpm.activiti.delegate.core;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.el.FixedValue;
import org.apache.commons.lang.StringUtils;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.bpm.util.ActivitiRequestType;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditSource;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
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
