package org.openiam.bpm.activiti.delegate.user.edit;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections.CollectionUtils;
import org.openiam.bpm.activiti.delegate.entitlements.AbstractEntitlementsDelegate;
import org.openiam.bpm.activiti.delegate.entitlements.AcceptEntitlementsNotifierDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;

public class AcceptEditUserNotifierDelegate extends AcceptEntitlementsNotifierDelegate {
	
	public AcceptEditUserNotifierDelegate() {
		super();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final Set<String> userIds = new HashSet<String>();
		
		final String targetUserId = getTargetUserId(execution);
		final UserEntity targetUser = getUserEntity(targetUserId);
		
		final String taskOwner = getRequestorId(execution);
		
		userIds.add(taskOwner);
		userIds.add(targetUserId);
		final Collection<String> candidateUsersIds = activitiHelper.getOnAcceptUserIds(execution, targetUserId, getSupervisorsForUser(targetUser));
		if(CollectionUtils.isNotEmpty(candidateUsersIds)) {
			userIds.addAll(candidateUsersIds);
		}
		
		for(final String toNotifyUserId : userIds) {
			final UserEntity toNotify = getUserEntity(toNotifyUserId);
			if(toNotify != null) {
				sendNotification(toNotify, targetUser, execution);
			}
		}
	}
	
	@Override
	protected ActivitiConstants getTargetVariable() {
		return ActivitiConstants.ASSOCIATION_ID;
	}
}
