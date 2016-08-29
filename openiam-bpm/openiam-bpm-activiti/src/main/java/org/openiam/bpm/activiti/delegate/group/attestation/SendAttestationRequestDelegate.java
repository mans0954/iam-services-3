package org.openiam.bpm.activiti.delegate.group.attestation;

import java.util.Collection;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.bpm.activiti.delegate.entitlements.AbstractEntitlementsDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.base.request.NotificationParam;
import org.openiam.base.request.NotificationRequest;
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
			final String groupId = getTargetGroupId(execution);
			
			if(groupId != null) {
				final Group group = getGroup(groupId);
                final Collection<String> candidateUsersIds = activitiHelper.getCandidateUserIds(execution, null, null);

                if(CollectionUtils.isNotEmpty(candidateUsersIds)) {
                    for (final String candidateId : candidateUsersIds) {
                        final UserEntity candidate = getUserEntity(candidateId);
                        if (candidate != null) {
                            sendNotificationRequest(candidate, group, execution);
                        }
                    }
                }
			}
			sw.stop();
			LOG.info(String.format("Took %s ms to send attestation requests for group %s", sw.getTime(), groupId));
			idmAuditLog.succeed();
		} catch(Throwable e) {
			idmAuditLog.setException(e);
			idmAuditLog.fail();
			throw new RuntimeException(e);
		} finally {
			addAuditLogChild(execution, idmAuditLog);
		}
	}
    protected String getTargetGroupId(final DelegateExecution execution) {
        ActivitiConstants targetVariable = getTargetVariable();
        if(targetVariable == null) {
            targetVariable = ActivitiConstants.EMPLOYEE_ID;
        }
        String retVal = null;
        if(targetVariable != null) {
            retVal = getStringVariable(execution, targetVariable);
        }
        return retVal;
    }

	private void sendNotificationRequest(final UserEntity candidate, final Group group, final DelegateExecution execution) {
		final NotificationRequest request = new NotificationRequest();
        request.setUserId(candidate.getId());
        request.setNotificationType(getNotificationType(execution));
        request.getParamList().add(new NotificationParam("GROUP", group));
        request.getParamList().add(new NotificationParam("OWNER", candidate));
        request.getParamList().add(new NotificationParam("COMMENT", getComment(execution)));
        mailService.sendNotification(request);
	}
	
	@Override
	protected ActivitiConstants getTargetVariable() {
		return ActivitiConstants.EMPLOYEE_ID;
	}
}
