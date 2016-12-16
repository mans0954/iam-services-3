package org.openiam.bpm.activiti.delegate.user.edit;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.entitlements.AbstractEntitlementsDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.idm.srvc.user.service.UserProfileService;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.beans.factory.annotation.Autowired;

public class UpdateUser extends AbstractEntitlementsDelegate {
	@Autowired
	private UserProfileService userProfileService;

	public UpdateUser() {
		super();
	}

	@Override
	protected void doExecute(DelegateExecution execution) throws Exception {
		final UserProfileRequestModel profile = getObjectVariable(execution, ActivitiConstants.REQUEST, UserProfileRequestModel.class);
		//final String userId = (String)execution.getVariable(ActivitiConstants.ASSOCIATION_ID);
		
		final IdmAuditLogEntity idmAuditLog = createNewAuditLog(execution);
        idmAuditLog.setAction(AuditAction.MODIFY_USER.value());
        idmAuditLog.addAttributeAsJson(AuditAttributeName.PROFILE, profile, customJacksonMapper);
		try {
			User user = profile.getUser();
			if(user != null) {
				user.setNotifyUserViaEmail(false); /* edit user - don't send creds */
				Response wsResponse = userProfileService.saveUserProfileWrapper(profile);
				if(wsResponse == null || wsResponse.isFailure()) {
					throw new ActivitiException(String.format("userDataService.saveUserProfile failed for %s.  Response was %s", profile, wsResponse));
				} else {
					user = getUser(user.getId());
					final ProvisionUser pUser = new ProvisionUser(user);

					wsResponse = provisionService.modifyUser(pUser);
					if(wsResponse == null || wsResponse.isFailure()) {
						throw new ActivitiException(String.format("provisionService.modifyUser failed for %s.  Response was %s", user, wsResponse));
					}
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
