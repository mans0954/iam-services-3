package org.openiam.bpm.activiti.delegate.entitlements;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;

public class RejectSupervisorNotifierDelegate extends AbstractEntitlementsDelegate {	
	
	@Autowired
	private UserDAO userDAO;
	
	private static Map<String, String> NOTIFICATION_MAP = new HashMap<String, String>();
	static {
		NOTIFICATION_MAP.put("REMOVE_SUPERIOR", "REMOVE_SUPERIOR_REJECT");
		NOTIFICATION_MAP.put("ADD_SUPERIOR", "ADD_SUPERIOR_REJECT");
	}
	
	@Override
	protected String getNotificationType() {
		return NOTIFICATION_MAP.get(getOperation());
	}

	public RejectSupervisorNotifierDelegate() {
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
}
