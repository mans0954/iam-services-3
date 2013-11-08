package org.openiam.bpm.activiti.delegate.entitlements;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.bpm.activiti.delegate.core.AbstractNotificationDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;

public class AcceptSupervisorNotifierDelegate extends AbstractNotificationDelegate {

	@Autowired
	private UserDAO userDAO;
	
	private static Map<String, String> NOTIFICATION_MAP = new HashMap<String, String>();
	static {
		NOTIFICATION_MAP.put("REMOVE_SUPERIOR", "REMOVE_SUPERIOR_ACCEPT");
		NOTIFICATION_MAP.put("ADD_SUPERIOR", "ADD_SUPERIOR_ACCEPT");
	}
	
	@Override
	protected String getNotificationType() {
		return NOTIFICATION_MAP.get(getOperation());
	}
	
	public AcceptSupervisorNotifierDelegate() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String targetUserId = (String)execution.getVariable(ActivitiConstants.MEMBER_ASSOCIATION_ID);
		final UserEntity targetUser = userDAO.findById(targetUserId);
		
		final String taskName = (String)execution.getVariable(ActivitiConstants.TASK_NAME);
		final String taskDescription = (String)execution.getVariable(ActivitiConstants.TASK_DESCRIPTION);
		final String taskOwner = (String)execution.getVariable(ActivitiConstants.TASK_OWNER);
		final UserEntity owner = userDAO.findById(taskOwner);
		
		final Set<String> candidateUsersIds = new HashSet<String>(activitiHelper.getCandidateUserIds(execution, targetUserId, null));
		candidateUsersIds.addAll(activitiHelper.getCandidateUserIds(execution));
		
		for(final String userId : candidateUsersIds) {
			final UserEntity user = userDAO.findById(userId);
			if(user != null) {
				sendNotification(user, owner, targetUser, null, taskName, taskDescription);
			}
		}
	}
}
