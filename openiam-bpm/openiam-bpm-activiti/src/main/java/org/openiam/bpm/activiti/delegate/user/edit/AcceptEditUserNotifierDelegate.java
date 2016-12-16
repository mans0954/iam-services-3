package org.openiam.bpm.activiti.delegate.user.edit;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections.CollectionUtils;
import org.openiam.bpm.activiti.delegate.entitlements.AcceptEntitlementsNotifierDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;

public class AcceptEditUserNotifierDelegate extends AcceptEntitlementsNotifierDelegate {
	
	public AcceptEditUserNotifierDelegate() {
		super();
	}

	@Override
	public void doExecute(DelegateExecution execution) throws Exception {
		final IdmAuditLogEntity idmAuditLog = createNewAuditLog(execution);
        idmAuditLog.setAction(AuditAction.NOTIFICATION.value());
		try {
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
	protected ActivitiConstants getTargetVariable() {
		return ActivitiConstants.ASSOCIATION_ID;
	}
}
