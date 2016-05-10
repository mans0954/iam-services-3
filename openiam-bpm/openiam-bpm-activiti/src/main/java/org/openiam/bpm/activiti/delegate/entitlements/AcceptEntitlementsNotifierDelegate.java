package org.openiam.bpm.activiti.delegate.entitlements;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections.CollectionUtils;
import org.openiam.bpm.activiti.delegate.core.AbstractNotificationDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.bpm.util.ActivitiRequestType;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;

public class AcceptEntitlementsNotifierDelegate extends AbstractNotificationDelegate {
	
	private static final Map<ActivitiRequestType, String> NOTIFICATION_MAP = new HashMap<ActivitiRequestType, String>();
	static {
		NOTIFICATION_MAP.put(ActivitiRequestType.ADD_USER_TO_ROLE, "ADD_USER_TO_ROLE_ACCEPT_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.REMOVE_USER_FROM_ROLE, "REMOVE_USER_FROM_ROLE_ACCEPT_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.ADD_USER_TO_GROUP, "ADD_USER_TO_GROUP_ACCEPT_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.REMOVE_USER_FROM_GROUP, "REMOVE_USER_FROM_GROUP_ACCEPT_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.ENTITLE_USER_TO_RESOURCE, "ENTITLE_USER_TO_RESOURCE_ACCEPT_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.DISENTITLE_USR_FROM_RESOURCE, "DISENTITLE_USER_FROM_RESOURCE_ACCEPT_NOTIFY");
		
		NOTIFICATION_MAP.put(ActivitiRequestType.REMOVE_SUPERIOR, "REMOVE_SUPERIOR_ACCEPT");
		NOTIFICATION_MAP.put(ActivitiRequestType.ADD_SUPERIOR, "ADD_SUPERIOR_ACCEPT");
        NOTIFICATION_MAP.put(ActivitiRequestType.REPLACE_SUPERIOR, "REPLACE_SUPERIOR_ACCEPT");
		NOTIFICATION_MAP.put(ActivitiRequestType.DELETE_LOGIN, "DELETE_LOGIN_ACCEPT_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.SAVE_LOGIN, "ADD_UPDATE_LOGIN_ACCEPT_NOTIFY");
		
		NOTIFICATION_MAP.put(ActivitiRequestType.ADD_USER_TO_ORG, "ADD_USER_TO_ORG_ACCEPT_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.REMOVE_USER_FROM_ORG, "REMOVE_USER_FROM_ORG_ACCEPT_NOTIFY");
		
		NOTIFICATION_MAP.put(ActivitiRequestType.EDIT_USER, "EDIT_USER_NOTIFY_ACCEPT");
		
		NOTIFICATION_MAP.put(ActivitiRequestType.EDIT_RESOURCE, "EDIT_RESOURCE_ACCEPT");
		NOTIFICATION_MAP.put(ActivitiRequestType.DELETE_RESOURCE, "DELETE_RESOURCE_ACCEPT");
		NOTIFICATION_MAP.put(ActivitiRequestType.NEW_RESOURCE, "NEW_RESOURCE_ACCEPT");
		
		NOTIFICATION_MAP.put(ActivitiRequestType.NEW_GROUP, "NEW_GROUP_ACCEPT");
		NOTIFICATION_MAP.put(ActivitiRequestType.EDIT_GROUP, "EDIT_GROUP_ACCEPT");
		NOTIFICATION_MAP.put(ActivitiRequestType.DELETE_GROUP, "DELETE_GROUP_ACCEPT");
		
		NOTIFICATION_MAP.put(ActivitiRequestType.NEW_ROLE, "NEW_ROLE_ACCEPT");
		NOTIFICATION_MAP.put(ActivitiRequestType.EDIT_ROLE, "EDIT_ROLE_ACCEPT");
		NOTIFICATION_MAP.put(ActivitiRequestType.DELETE_ROLE, "DELETE_ROLE_ACCEPT");
		
		NOTIFICATION_MAP.put(ActivitiRequestType.NEW_ORGANIZATION, "NEW_ORGANIZTION_ACCEPT");
		NOTIFICATION_MAP.put(ActivitiRequestType.EDIT_ORGANIZATION, "EDIT_ORGANIZTION_ACCEPT");
		NOTIFICATION_MAP.put(ActivitiRequestType.DELETE_ORGANIZATION, "DELETE_ORGANIZATION_ACCEPT");
		
		NOTIFICATION_MAP.put(ActivitiRequestType.SELF_REGISTRATION, "REQUEST_APPROVED");
		NOTIFICATION_MAP.put(ActivitiRequestType.NEW_HIRE_WITH_APPROVAL, "REQUEST_APPROVED");
		NOTIFICATION_MAP.put(ActivitiRequestType.NEW_HIRE_NO_APPROVAL, "REQUEST_APPROVED");
		
		
		NOTIFICATION_MAP.put(ActivitiRequestType.ADD_GROUP_TO_GROUP, "ADD_GROUP_TO_GROUP_APPROVED");
		NOTIFICATION_MAP.put(ActivitiRequestType.ADD_ROLE_TO_GROUP, "ADD_ROLE_TO_GROUP_APPROVED");
		NOTIFICATION_MAP.put(ActivitiRequestType.ENTITLE_RESOURCE_TO_GROUP, "ENTITLE_RESOURCE_TO_GROUP_APPROVED");
		NOTIFICATION_MAP.put(ActivitiRequestType.ADD_ROLE_TO_ROLE, "ADD_ROLE_TO_ROLE_APPROVED");
		NOTIFICATION_MAP.put(ActivitiRequestType.ENTITLE_RESOURCE_TO_ROLE, "ENTITLE_RESOURCE_TO_ROLE_APPROVED");
		NOTIFICATION_MAP.put(ActivitiRequestType.ADD_RESOURCE_TO_RESOURCE, "ADD_RESOURCE_TO_RESOURCE_APPROVED");
		
		NOTIFICATION_MAP.put(ActivitiRequestType.REMOVE_GROUP_FROM_GROUP, "REMOVE_GROUP_FROM_GROUP_APPROVED");
		NOTIFICATION_MAP.put(ActivitiRequestType.REMOVE_ROLE_FROM_GROUP, "REMOVE_ROLE_FROM_GROUP_APPROVED");
		NOTIFICATION_MAP.put(ActivitiRequestType.DISENTITLE_RESOURCE_FROM_GROUP, "DISENTITLE_RESOURCE_FROM_GROUP_APPROVED");
		NOTIFICATION_MAP.put(ActivitiRequestType.REMOVE_ROLE_FROM_ROLE, "REMOVE_ROLE_FROM_ROLE_APPROVED");
		NOTIFICATION_MAP.put(ActivitiRequestType.DISENTITLE_RESOURCE_FROM_ROLE, "DISENTITLE_RESOURCE_FROM_ROLE_APPROVED");
		NOTIFICATION_MAP.put(ActivitiRequestType.REMOVE_RESOURCE_FROM_RESOURCE, "REMOVE_RESOURCE_FROM_RESOURCE_APPROVED");
		
		NOTIFICATION_MAP.put(ActivitiRequestType.RESOURCE_CERTIFICATION, "RESOURCE_CERTIFICATION_ACCEPTED");
		NOTIFICATION_MAP.put(ActivitiRequestType.ROLE_CERTIFICATION, "RESOURCE_CERTIFICATION_ACCEPTED");
	}
	
	public AcceptEntitlementsNotifierDelegate() {
		super();
	}
	

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final IdmAuditLog idmAuditLog = createNewAuditLog(execution);
        idmAuditLog.setAction(AuditAction.NOTIFICATION.value());
		try {
			final Set<String> userIds = new HashSet<String>();
			
			final String targetUserId = getTargetUserId(execution);
			final UserEntity targetUser = getUserEntity(targetUserId);
			
			final String taskOwner = getRequestorId(execution);
			userIds.add(taskOwner);
			userIds.add(targetUserId);
			userIds.addAll(activitiHelper.getOnAcceptUserIds(execution, targetUserId, null));
			
			for(final String toNotifyUserId : userIds) {
				final UserEntity toNotify = getUserEntity(toNotifyUserId);
				if(toNotify != null) {
					sendNotification(toNotify, targetUser, execution);
				}
			}
			idmAuditLog.succeed();
		} catch(Throwable e) {
			idmAuditLog.setException(e);
			idmAuditLog.fail();
			throw new RuntimeException(e);
		} finally {
			addAuditLogChild(execution, idmAuditLog);
		}
	}
	
	@Override
	protected String getNotificationType(final DelegateExecution execution) {
		String operation = super.getNotificationType(execution);
		if(operation == null) {
			final ActivitiRequestType requestType = getRequestType(execution);
			if(requestType != null) {
				operation = NOTIFICATION_MAP.get(requestType);
			}
		}
		return operation;
	}
}
