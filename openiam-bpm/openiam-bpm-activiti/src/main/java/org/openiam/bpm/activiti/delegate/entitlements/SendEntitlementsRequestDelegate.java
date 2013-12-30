package org.openiam.bpm.activiti.delegate.entitlements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;

public class SendEntitlementsRequestDelegate extends AbstractEntitlementsDelegate {
	
	@Autowired
	private UserDAO userDAO;
	
	private static Map<String, String> NOTIFICATION_MAP = new HashMap<String, String>();
	static {
		NOTIFICATION_MAP.put("ADD_USER_TO_ROLE", "ADD_USER_TO_ROLE_NOTIFY");
		NOTIFICATION_MAP.put("REMOVE_USER_FROM_ROLE", "REMOVE_USER_FROM_ROLE_NOTIFY");
		NOTIFICATION_MAP.put("ADD_USER_TO_GROUP", "ADD_USER_TO_GROUP_NOTIFY");
		NOTIFICATION_MAP.put("REMOVE_USER_FROM_GROUP", "REMOVE_USER_FROM_GROUP_NOTIFY");
		NOTIFICATION_MAP.put("ENTITLE_USER_TO_RESOURCE", "ENTITLE_USER_TO_RESOURCE_NOTIFY");
		NOTIFICATION_MAP.put("DISENTITLE_USER_FROM_RESOURCE", "DISENTITLE_USER_FROM_RESOURCE_NOTIFY");
		NOTIFICATION_MAP.put("REMOVE_SUPERIOR", "REMOVE_SUPERIOR_NOTIFY");
		NOTIFICATION_MAP.put("ADD_SUPERIOR", "ADD_SUPERIOR_NOTIFY");
		NOTIFICATION_MAP.put("DELETE_LOGIN", "DELETE_LOGIN_NOTIFY");
		NOTIFICATION_MAP.put("ADD_UPDATE_LOGIN", "ADD_UPDATE_LOGIN_NOTIFY");
	}
	
	public SendEntitlementsRequestDelegate() {
		super();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String associationId = (String)execution.getVariable(ActivitiConstants.ASSOCIATION_ID);
		final String targetUserId = (String)execution.getVariable(ActivitiConstants.MEMBER_ASSOCIATION_ID);
		final UserEntity targetUser = userDAO.findById(targetUserId);
		
		final String taskName = (String)execution.getVariable(ActivitiConstants.TASK_NAME);
		final String taskDescription = (String)execution.getVariable(ActivitiConstants.TASK_DESCRIPTION);
		final String taskOwner = (String)execution.getVariable(ActivitiConstants.TASK_OWNER);
		final UserEntity owner = userDAO.findById(taskOwner);
		
		final Collection<String> candidateUsersIds = getCandidateUserIds(execution);
				
		for(final String userId : candidateUsersIds) {
			final UserEntity user = userDAO.findById(userId);
			if(user != null) {
				sendNotification(user, owner, targetUser, null, taskName, taskDescription);
			}
		}
	}
	
	@Override
	protected String getNotificationType() {
		return NOTIFICATION_MAP.get(getOperation());
	}
}