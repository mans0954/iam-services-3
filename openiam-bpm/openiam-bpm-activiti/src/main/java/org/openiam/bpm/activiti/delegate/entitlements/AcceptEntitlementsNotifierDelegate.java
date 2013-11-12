package org.openiam.bpm.activiti.delegate.entitlements;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections.CollectionUtils;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;

public class AcceptEntitlementsNotifierDelegate extends AbstractEntitlementsDelegate {

	@Autowired
	private UserDAO userDAO;
	
	private static Map<String, String> NOTIFICATION_MAP = new HashMap<String, String>();
	static {
		NOTIFICATION_MAP.put("ADD_USER_TO_ROLE", "ADD_USER_TO_ROLE_ACCEPT_NOTIFY");
		NOTIFICATION_MAP.put("REMOVE_USER_FROM_ROLE", "REMOVE_USER_FROM_ROLE_ACCEPT_NOTIFY");
		NOTIFICATION_MAP.put("ADD_USER_TO_GROUP", "ADD_USER_TO_GROUP_ACCEPT_NOTIFY");
		NOTIFICATION_MAP.put("REMOVE_USER_FROM_GROUP", "REMOVE_USER_FROM_GROUP_ACCEPT_NOTIFY");
		NOTIFICATION_MAP.put("ENTITLE_USER_TO_RESOURCE", "ENTITLE_USER_TO_RESOURCE_ACCEPT_NOTIFY");
		NOTIFICATION_MAP.put("DISENTITLE_USER_FROM_RESOURCE", "DISENTITLE_USER_FROM_RESOURCE_ACCEPT_NOTIFY");
		NOTIFICATION_MAP.put("DELETE_LOGIN", "DELETE_LOGIN_ACCEPT_NOTIFY");
		NOTIFICATION_MAP.put("ADD_UPDATE_LOGIN", "ADD_UPDATE_LOGIN_ACCEPT_NOTIFY");
		NOTIFICATION_MAP.put("ADD_USER_TO_ORG", "ADD_USER_TO_ORG_ACCEPT_NOTIFY");
		NOTIFICATION_MAP.put("REMOVE_USER_FROM_ORG", "REMOVE_USER_FROM_ORG_ACCEPT_NOTIFY");
		NOTIFICATION_MAP.put("REMOVE_SUPERIOR", "REMOVE_SUPERIOR_ACCEPT");
		NOTIFICATION_MAP.put("ADD_SUPERIOR", "ADD_SUPERIOR_ACCEPT");
	}
	
	public AcceptEntitlementsNotifierDelegate() {
		super();
	}
	

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final Set<String> userIds = new HashSet<String>();
		
		final String taskName = (String)execution.getVariable(ActivitiConstants.TASK_NAME);
		final String taskDescription = (String)execution.getVariable(ActivitiConstants.TASK_DESCRIPTION);
		final String comment = (String)execution.getVariable(ActivitiConstants.COMMENT);
		final String targetUserId = getTargetUserId(execution);
		final UserEntity targetUser = userDAO.findById(targetUserId);
		
		final String taskOwner = (String)execution.getVariable(ActivitiConstants.TASK_OWNER);
		final UserEntity owner = userDAO.findById(taskOwner);
		
		userIds.add(taskOwner);
		
		final List<ApproverAssociationEntity> approverAssociationEntities = activitiHelper.getApproverAssociations(execution);
		if(CollectionUtils.isNotEmpty(approverAssociationEntities)) {
			for(final ApproverAssociationEntity association : approverAssociationEntities) {
				userIds.addAll(activitiHelper.getNotifyUserIds(association.getOnRejectEntityType(), association.getOnRejectEntityId(), targetUserId));
			}
		}
		
		for(final String toNotifyUserId : userIds) {
			final UserEntity toNotify = userDAO.findById(toNotifyUserId);
			if(toNotify != null) {
				sendNotification(toNotify, owner, targetUser, comment, taskName, taskDescription);
			}
		}
	}
	
	protected String getTargetUserId(final DelegateExecution execution) {
		return (String)execution.getVariable(ActivitiConstants.MEMBER_ASSOCIATION_ID);
	}
	
	@Override
	protected String getNotificationType() {
		return NOTIFICATION_MAP.get(getOperation());
	}

}
