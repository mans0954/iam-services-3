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
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;

public class RejectEntitlementsNotifierDelegate extends AbstractEntitlementsDelegate {
	
	@Autowired
	private UserDAO userDAO;
	
	private static Map<String, String> NOTIFICATION_MAP = new HashMap<String, String>();
	static {
		NOTIFICATION_MAP.put("ADD_USER_TO_ROLE", "ADD_USER_TO_ROLE_REJECT_NOTIFY");
		NOTIFICATION_MAP.put("REMOVE_USER_FROM_ROLE", "REMOVE_USER_FROM_ROLE_REJECT_NOTIFY");
		NOTIFICATION_MAP.put("ADD_USER_TO_GROUP", "ADD_USER_TO_GROUP_REJECT_NOTIFY");
		NOTIFICATION_MAP.put("REMOVE_USER_FROM_GROUP", "REMOVE_USER_FROM_GROUP_REJECT_NOTIFY");
		NOTIFICATION_MAP.put("ENTITLE_USER_TO_RESOURCE", "ENTITLE_USER_TO_RESOURCE_REJECT_NOTIFY");
		NOTIFICATION_MAP.put("DISENTITLE_USER_FROM_RESOURCE", "DISENTITLE_USER_FROM_RESOURCE_REJECT_NOTIFY");
		NOTIFICATION_MAP.put("DELETE_LOGIN", "DELETE_LOGIN_REJECT_NOTIFY");
		NOTIFICATION_MAP.put("ADD_UPDATE_LOGIN", "ADD_UPDATE_LOGIN_REJECT_NOTIFY");
		NOTIFICATION_MAP.put("ADD_USER_TO_ORG", "ADD_USER_TO_ORG_REJECT_NOTIFY");
		NOTIFICATION_MAP.put("REMOVE_USER_FROM_ORG", "REMOVE_USER_FROM_ORG_REJECT_NOTIFY");
		NOTIFICATION_MAP.put("REMOVE_SUPERIOR", "REMOVE_SUPERIOR_REJECT");
		NOTIFICATION_MAP.put("ADD_SUPERIOR", "ADD_SUPERIOR_REJECT");
		NOTIFICATION_MAP.put("EDIT_RESOURCE", "EDIT_RESOURCE_REJECT");
		NOTIFICATION_MAP.put("DELETE_RESOURCE", "DELETE_RESOURCE_REJECT");
	}
	
	public RejectEntitlementsNotifierDelegate() {
		super();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final Set<String> userIds = new HashSet<String>();
		
		final String targetUserId = getTargetUserId(execution);
		final UserEntity targetUser = userDAO.findById(targetUserId);
		
		final String taskOwner = getRequestorId(execution);
		
		userIds.add(taskOwner);
		userIds.add(targetUserId);
		
		final List<ApproverAssociationEntity> approverAssociationEntities = activitiHelper.getApproverAssociations(execution);
		if(CollectionUtils.isNotEmpty(approverAssociationEntities)) {
			for(final ApproverAssociationEntity association : approverAssociationEntities) {
				userIds.addAll(activitiHelper.getNotifyUserIds(association.getOnRejectEntityType(), association.getOnRejectEntityId(), targetUserId));
			}
		}
		
		for(final String toNotifyUserId : userIds) {
			final UserEntity toNotify = userDAO.findById(toNotifyUserId);
			if(toNotify != null) {
				sendNotification(toNotify, targetUser, execution);
			}
		}
	}
	
	@Override
	protected String getNotificationType() {
		return NOTIFICATION_MAP.get(getOperation());
	}
}
