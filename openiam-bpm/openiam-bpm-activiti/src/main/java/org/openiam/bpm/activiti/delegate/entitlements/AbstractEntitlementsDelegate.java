package org.openiam.bpm.activiti.delegate.entitlements;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.el.FixedValue;
import org.apache.commons.lang.StringUtils;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.activiti.delegate.core.AbstractNotificationDelegate;
import org.openiam.bpm.activiti.delegate.core.ActivitiHelper;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.bpm.util.ActivitiRequestType;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class AbstractEntitlementsDelegate extends AbstractNotificationDelegate {

	private static final Map<ActivitiRequestType, String> NOTIFICATION_MAP = new HashMap<ActivitiRequestType, String>();
	static {
		NOTIFICATION_MAP.put(ActivitiRequestType.ADD_USER_TO_ROLE, "ADD_USER_TO_ROLE_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.REMOVE_USER_FROM_ROLE, "REMOVE_USER_FROM_ROLE_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.ADD_USER_TO_GROUP, "ADD_USER_TO_GROUP_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.REMOVE_USER_FROM_GROUP, "REMOVE_USER_FROM_GROUP_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.ENTITLE_USER_TO_RESOURCE, "ENTITLE_USER_TO_RESOURCE_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.DISENTITLE_USR_FROM_RESOURCE, "DISENTITLE_USER_FROM_RESOURCE_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.REMOVE_SUPERIOR, "REMOVE_SUPERIOR_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.ADD_SUPERIOR, "ADD_SUPERIOR_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.DELETE_LOGIN, "DELETE_LOGIN_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.SAVE_LOGIN, "ADD_UPDATE_LOGIN_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.ADD_USER_TO_ORG, "ADD_USER_TO_ORG_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.REMOVE_USER_FROM_ORG, "REMOVE_USER_FROM_ORG_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.EDIT_USER, "EDIT_USER_NOTIFY");

		NOTIFICATION_MAP.put(ActivitiRequestType.EDIT_RESOURCE, "EDIT_RESOURCE_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.DELETE_RESOURCE, "DELETE_RESOURCE_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.NEW_RESOURCE, "NEW_RESOURCE_NOTIFY");

		NOTIFICATION_MAP.put(ActivitiRequestType.NEW_GROUP, "NEW_GROUP_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.EDIT_GROUP, "EDIT_GROUP_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.DELETE_GROUP, "DELETE_GROUP_NOTIFY");

		NOTIFICATION_MAP.put(ActivitiRequestType.NEW_ROLE, "NEW_ROLE_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.EDIT_ROLE, "EDIT_ROLE_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.DELETE_ROLE, "DELETE_ROLE_NOTIFY");

		NOTIFICATION_MAP.put(ActivitiRequestType.NEW_ORGANIZATION, "NEW_ORGANIZTION_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.EDIT_ORGANIZATION, "EDIT_ORGANIZTION_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.DELETE_ORGANIZATION, "DELETE_ORGANIZATION_NOTIFY");

		NOTIFICATION_MAP.put(ActivitiRequestType.SELF_REGISTRATION, "NEW_PENDING_REQUEST");
		NOTIFICATION_MAP.put(ActivitiRequestType.NEW_HIRE_WITH_APPROVAL, "NEW_PENDING_REQUEST");

		NOTIFICATION_MAP.put(ActivitiRequestType.ATTESTATION, "ATTESTATION_REQUEST");
        NOTIFICATION_MAP.put(ActivitiRequestType.GROUP_ATTESTATION, "GROUP_ATTESTATION_REQUEST");

		NOTIFICATION_MAP.put(ActivitiRequestType.ADD_GROUP_TO_GROUP, "ADD_GROUP_TO_GROUP_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.ADD_ROLE_TO_GROUP, "ADD_ROLE_TO_GROUP_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.ENTITLE_RESOURCE_TO_GROUP, "ENTITLE_RESOURCE_TO_GROUP_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.ADD_ROLE_TO_ROLE, "ADD_ROLE_TO_ROLE_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.ENTITLE_RESOURCE_TO_ROLE, "ENTITLE_RESOURCE_TO_ROLE_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.ADD_RESOURCE_TO_RESOURCE, "ADD_RESOURCE_TO_RESOURCE_NOTIFY");

		NOTIFICATION_MAP.put(ActivitiRequestType.REMOVE_GROUP_FROM_GROUP, "REMOVE_GROUP_FROM_GROUP_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.REMOVE_ROLE_FROM_GROUP, "REMOVE_ROLE_FROM_GROUP_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.DISENTITLE_RESOURCE_FROM_GROUP, "DISENTITLE_RESOURCE_FROM_GROUP_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.REMOVE_ROLE_FROM_ROLE, "REMOVE_ROLE_FROM_ROLE_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.DISENTITLE_RESOURCE_FROM_ROLE, "DISENTITLE_RESOURCE_FROM_ROLE_NOTIFY");
		NOTIFICATION_MAP.put(ActivitiRequestType.REMOVE_RESOURCE_FROM_RESOURCE, "REMOVE_RESOURCE_FROM_RESOURCE_NOTIFY");
		
		NOTIFICATION_MAP.put(ActivitiRequestType.RESOURCE_CERTIFICATION, "RESOURCE_CERTIFICATION_NOTIFY");
	}

	protected AbstractEntitlementsDelegate() {
		super();
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

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final IdmAuditLog idmAuditLog = createNewAuditLog(execution);
        idmAuditLog.setAction(AuditAction.ENTITLEMENTS_DELEGATE.value());
		try {
			final String targetUserId = getTargetUserId(execution);
			final UserEntity targetUser = getUserEntity(targetUserId);
			final String requestorId = getRequestorId(execution);
	
			final Collection<String> candidateUsersIds = activitiHelper.getCandidateUserIds(execution, targetUserId, null);
	
			boolean isRequestorOnlyApprover = false;
			boolean isRequestorCandidate = false;
			for(final String userId : candidateUsersIds) {
				final UserEntity user = getUserEntity(userId);
				if(user != null) {
					sendNotification(user, targetUser, execution);
				}
				if(StringUtils.equals(userId, requestorId)) {
					isRequestorCandidate = true;
				}
			}
			
			if(isRequestorCandidate) {
				isRequestorOnlyApprover = (candidateUsersIds.size() == 1);
			}
			
			execution.setVariable(ActivitiConstants.IS_REQUESTOR_ONLY_APROVER.getName(), isRequestorOnlyApprover);
			execution.setVariable(ActivitiConstants.IS_REQUESTOR_CANDIDATE.getName(), isRequestorCandidate);
			idmAuditLog.succeed();
		} catch(Throwable e) {
			idmAuditLog.setException(e);
			idmAuditLog.fail();
			throw new RuntimeException(e);
		} finally {
			addAuditLogChild(execution, idmAuditLog);
		}
	}
}
