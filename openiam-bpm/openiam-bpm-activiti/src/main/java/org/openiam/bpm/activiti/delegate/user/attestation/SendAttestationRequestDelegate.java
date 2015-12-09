package org.openiam.bpm.activiti.delegate.user.attestation;

import java.util.Collection;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.bpm.activiti.delegate.entitlements.AbstractEntitlementsDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.user.domain.UserEntity;

public class SendAttestationRequestDelegate extends AbstractEntitlementsDelegate {
	
	private static final Log LOG = LogFactory.getLog(SendAttestationRequestDelegate.class);
	
	public SendAttestationRequestDelegate() {
		super();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final IdmAuditLogEntity idmAuditLog = createNewAuditLog(execution);
        idmAuditLog.setAction(AuditAction.NOTIFICATION.value());
		try {
			final StopWatch sw = new StopWatch();
			sw.start();
			final String employeeId = getTargetUserId(execution);
			
			if(employeeId != null) {
				final UserEntity employee = getUserEntity(employeeId);
				final Collection<String> candidateUsersIds = activitiHelper.getCandidateUserIds(execution, employeeId, null);
					
				for(final String candidateId : candidateUsersIds) {
					final UserEntity supervisor = getUserEntity(candidateId);
					if(supervisor != null) {
						sendNotificationRequest(supervisor, employee, execution);
					}
				}
			}
			sw.stop();
			LOG.info(String.format("Took %s ms to send re-certification requests for user %s", sw.getTime(), employeeId));
			idmAuditLog.succeed();
		} catch(Throwable e) {
			idmAuditLog.setException(e);
			idmAuditLog.fail();
			throw new RuntimeException(e);
		} finally {
			addAuditLogChild(execution, idmAuditLog);
		}
	}

	private void sendNotificationRequest(final UserEntity supervisor, final UserEntity employee, final DelegateExecution execution) {
		final NotificationRequest request = new NotificationRequest();
        request.setUserId(supervisor.getId());
        request.setNotificationType(getNotificationType(execution));
        request.getParamList().add(new NotificationParam("EMPLOYEE", employee));
        request.getParamList().add(new NotificationParam("SUPERVISOR", supervisor));
        request.getParamList().add(new NotificationParam("COMMENT", getComment(execution)));
        mailService.sendNotification(request);
	}
	
	@Override
	protected ActivitiConstants getTargetVariable() {
		return ActivitiConstants.EMPLOYEE_ID;
	}
}
