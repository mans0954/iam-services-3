package org.openiam.bpm.activiti.delegate.entitlements;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.bpm.activiti.delegate.core.AbstractNotificationDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;

public class RejectSupervisorNotifierDelegate extends RejectEntitlementsNotifierDelegate {	
	
	public RejectSupervisorNotifierDelegate() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final IdmAuditLog idmAuditLog = createNewAuditLog(execution);
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
