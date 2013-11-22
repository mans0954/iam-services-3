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
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class AbstractEntitlementsDelegate extends AbstractNotificationDelegate {
	
	@Autowired
	@Qualifier("defaultProvision")
	protected ProvisionService provisionService;
	
	protected static final Map<String, String> NOTIFICATION_MAP = new HashMap<String, String>();
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
		NOTIFICATION_MAP.put("ADD_USER_TO_ORG", "ADD_USER_TO_ORG_NOTIFY");
		NOTIFICATION_MAP.put("REMOVE_USER_FROM_ORG", "REMOVE_USER_FROM_ORG_NOTIFY");
		NOTIFICATION_MAP.put("EDIT_USER", "EDIT_USER_NOTIFY");
		
		NOTIFICATION_MAP.put("EDIT_RESOURCE", "EDIT_RESOURCE_NOTIFY");
		NOTIFICATION_MAP.put("DELETE_RESOURCE", "DELETE_RESOURCE_NOTIFY");
		NOTIFICATION_MAP.put("NEW_RESOURCE", "NEW_RESOURCE_NOTIFY");
		
		NOTIFICATION_MAP.put("NEW_GROUP", "NEW_GROUP_NOTIFY");
		NOTIFICATION_MAP.put("EDIT_GROUP", "EDIT_GROUP_NOTIFY");
		NOTIFICATION_MAP.put("DELETE_GROUP", "DELETE_GROUP_NOTIFY");
		
		
		NOTIFICATION_MAP.put("NEW_ROLE", "NEW_ROLE_NOTIFY");
		NOTIFICATION_MAP.put("EDIT_ROLE", "EDIT_ROLE_NOTIFY");
		NOTIFICATION_MAP.put("DELETE_ROLE", "DELETE_ROLE_NOTIFY");
	}
	
	protected AbstractEntitlementsDelegate() {
		super();
	}
	
	protected String getNotificationType() {
		return NOTIFICATION_MAP.get(getOperation());
	}
	
	protected final String getTargetUserId(final DelegateExecution execution) {
		return getStringVariable(execution, getTargetVariable());
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String targetUserId = getTargetUserId(execution);
		final UserEntity targetUser = getUserEntity(targetUserId);
		
		final Collection<String> candidateUsersIds = activitiHelper.getCandidateUserIds(execution, targetUserId, null);
				
		for(final String userId : candidateUsersIds) {
			final UserEntity user = getUserEntity(userId);
			if(user != null) {
				sendNotification(user, targetUser, execution);
			}
		}
	}
	
	
}
