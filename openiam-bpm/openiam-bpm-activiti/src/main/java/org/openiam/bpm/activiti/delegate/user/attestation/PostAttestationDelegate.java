package org.openiam.bpm.activiti.delegate.user.attestation;

import java.util.Collection;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.request.NotificationParam;
import org.openiam.base.request.NotificationRequest;
import org.openiam.bpm.activiti.delegate.entitlements.AbstractEntitlementsDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;

public class PostAttestationDelegate extends AbstractEntitlementsDelegate {
	
	public PostAttestationDelegate() {
		super();
	}
	
	private static final Log LOG = LogFactory.getLog(PostAttestationDelegate.class);

	@Override
	protected void doExecute(DelegateExecution execution) throws Exception {
		final StopWatch sw = new StopWatch();
		sw.start();
		
		final IdmAuditLogEntity idmAuditLog = createNewAuditLog(execution);
        idmAuditLog.setAction(AuditAction.NOTIFICATION.value());
		try {
			final String employeeId = getTargetUserId(execution);
			if(employeeId != null) {
				final UserEntity employee = getUserEntity(employeeId);
				if(employee != null) {
					final Collection<String> candidateUsersIds = activitiHelper.getCandidateUserIds(execution, employeeId, null);
					for(final String candidateId : candidateUsersIds) {
						final UserEntity supervisor = getUserEntity(candidateId);
						if(supervisor != null) {
							sendNotificationRequest(supervisor, employee);
						}
					}
				}
			}
			sw.stop();
			LOG.info(String.format("Took %s ms to finalize re-certification request for user %s", sw.getTime(), employeeId));
			idmAuditLog.succeed();
		} catch(Throwable e) {
			idmAuditLog.setException(e);
			idmAuditLog.fail();
			throw new RuntimeException(e);
		} finally {
			addAuditLogChild(execution, idmAuditLog);
		}
	}

	private void sendNotificationRequest(final UserEntity supervisor, final UserEntity employee) {
		final NotificationRequest request = new NotificationRequest();
        request.setUserId(supervisor.getId());
        request.setNotificationType("ATTESTATION_REQUEST_DONE");
        request.getParamList().add(new NotificationParam("EMPLOYEE", employee));
        request.getParamList().add(new NotificationParam("SUPERVISOR", supervisor));
        mailService.sendNotification(request);
	}
	
	@Override
	protected ActivitiConstants getTargetVariable() {
		return ActivitiConstants.EMPLOYEE_ID;
	}
}
