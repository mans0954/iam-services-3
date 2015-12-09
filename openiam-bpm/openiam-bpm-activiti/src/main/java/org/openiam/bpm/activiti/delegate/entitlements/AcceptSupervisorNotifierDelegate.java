package org.openiam.bpm.activiti.delegate.entitlements;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;

public class AcceptSupervisorNotifierDelegate extends AcceptEntitlementsNotifierDelegate {	
	public AcceptSupervisorNotifierDelegate() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final IdmAuditLogEntity idmAuditLog = createNewAuditLog(execution);
        idmAuditLog.setAction(AuditAction.NOTIFICATION.value());
		try {
			final String targetUserId = getTargetUserId(execution);
			final UserEntity targetUser = getUserEntity(targetUserId);
			
			final List<String> notifyIds = getSupervisorsForUser(targetUser);
			notifyIds.add(targetUserId);
			notifyIds.add(getStringVariable(execution, ActivitiConstants.ASSOCIATION_ID));
			
			final Set<String> notifySet = new HashSet<String>(notifyIds);
			for(final String userId : notifySet) {
				final UserEntity user = getUserEntity(userId);
				if(user != null) {
					sendNotification(user, targetUser, execution);
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
}
