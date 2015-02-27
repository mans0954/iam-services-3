package org.openiam.bpm.activiti.delegate.group.attestation;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.openiam.bpm.activiti.delegate.entitlements.AbstractEntitlementsDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.user.domain.UserEntity;

import java.util.Collection;

public class PostGroupAttestationDelegate extends AbstractEntitlementsDelegate {
	
	public PostGroupAttestationDelegate() {
		super();
	}
	
	private static Logger LOG = Logger.getLogger(PostGroupAttestationDelegate.class);

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final StopWatch sw = new StopWatch();
		sw.start();
		
		final IdmAuditLog idmAuditLog = createNewAuditLog(execution);
        idmAuditLog.setAction(AuditAction.NOTIFICATION.value());
		try {
			final String groupId = getTargetGroupId(execution);
			if(groupId != null) {
				final Group group = getGroup(groupId);
                if(group != null) {
					final Collection<String> candidateUsersIds = activitiHelper.getCandidateUserIds(execution, null, null);
					for(final String candidateId : candidateUsersIds) {
						final UserEntity owner = getUserEntity(candidateId);
						if(owner != null) {
							sendNotificationRequest(owner, group);
						}
					}
				}
			}
			sw.stop();
			LOG.info(String.format("Took %s ms to finalize attestation request for group %s", sw.getTime(), groupId));
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

	private void sendNotificationRequest(final UserEntity owner, final Group group) {
		final NotificationRequest request = new NotificationRequest();
        request.setUserId(owner.getId());
        request.setNotificationType("GROUP_ATTESTATION_REQUEST_DONE");
        request.getParamList().add(new NotificationParam("GROUP", group));
        request.getParamList().add(new NotificationParam("OWNER", owner));
        mailService.sendNotification(request);
	}
	
	@Override
	protected ActivitiConstants getTargetVariable() {
		return ActivitiConstants.EMPLOYEE_ID;
	}
}
