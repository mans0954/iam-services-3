package org.openiam.bpm.activiti.delegate.user.edit;

import java.util.ArrayList;
import java.util.Collection;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang.StringUtils;
import org.openiam.bpm.activiti.delegate.entitlements.AbstractEntitlementsDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;

public class SendEditUserRequestDelegate extends AbstractEntitlementsDelegate {
	
	@Autowired
	private UserDAO userDAO;

	private static final String NOTIFY_TYPE = "EDIT_USER_NOTIFY";
	
	public SendEditUserRequestDelegate() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String associationId = (String)execution.getVariable(ActivitiConstants.ASSOCIATION_ID);
		final UserEntity targetUser = userDAO.findById(associationId);
		
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
		return NOTIFY_TYPE;
	}
}
