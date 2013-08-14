package org.openiam.bpm.activiti.delegate.user.edit;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections.CollectionUtils;
import org.openiam.bpm.activiti.delegate.entitlements.AbstractEntitlementsDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;

public class AcceptEditUserNotifierDelegate extends AbstractEntitlementsDelegate {
	
	private static final String NOTIFY_TYPE = "EDIT_USER_NOTIFY_ACCEPT";

	@Autowired
	private UserDAO userDAO;
	
	public AcceptEditUserNotifierDelegate() {
		super();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final Set<String> userIds = new HashSet<String>();
		
		final String taskName = (String)execution.getVariable(ActivitiConstants.TASK_NAME);
		final String taskDescription = (String)execution.getVariable(ActivitiConstants.TASK_DESCRIPTION);
		final String comment = (String)execution.getVariable(ActivitiConstants.COMMENT);
		final String targetUserId = (String)execution.getVariable(ActivitiConstants.ASSOCIATION_ID);
		final UserEntity targetUser = userDAO.findById(targetUserId);
		
		final String taskOwner = (String)execution.getVariable(ActivitiConstants.TASK_OWNER);
		final UserEntity owner = userDAO.findById(taskOwner);
		
		userIds.add(taskOwner);
		final Collection<String> candidateUsersIds = getCandidateUserIds(execution);
		if(CollectionUtils.isNotEmpty(candidateUsersIds)) {
			userIds.addAll(candidateUsersIds);
		}
		/*
		final List<ApproverAssociationEntity> approverAssociationEntities = getApproverAssociations(execution);
		if(CollectionUtils.isNotEmpty(approverAssociationEntities)) {
			for(final ApproverAssociationEntity association : approverAssociationEntities) {
				userIds.addAll(getNotifyUserIds(association.getOnRejectEntityType(), association.getOnRejectEntityId(), targetUserId));
			}
		}
		*/
		
		for(final String toNotifyUserId : userIds) {
			final UserEntity toNotify = userDAO.findById(toNotifyUserId);
			if(toNotify != null) {
				sendNotification(toNotify, owner, targetUser, comment, taskName, taskDescription);
			}
		}
	}

	@Override
	protected String getNotificationType() {
		return NOTIFY_TYPE;
	}
}
