package org.openiam.bpm.activiti.delegate.user.edit;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.entitlements.AbstractEntitlementsDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;
import org.openiam.idm.srvc.org.dto.OrganizationUserDTO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.Supervisor;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.ProvisionService;
import org.openiam.util.SpringContextProvider;
import org.opensaml.saml2.metadata.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.thoughtworks.xstream.XStream;

import java.util.Set;

public class UpdateUser extends AbstractEntitlementsDelegate {
	
	@Autowired
	@Qualifier("userWS")
	private UserDataWebService userDataService;
	
	public UpdateUser() {
		super();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final UserProfileRequestModel profile = getObjectVariable(execution, ActivitiConstants.REQUEST, UserProfileRequestModel.class);
		//final String userId = (String)execution.getVariable(ActivitiConstants.ASSOCIATION_ID);
		
		final IdmAuditLog idmAuditLog = createNewAuditLog(execution);
        idmAuditLog.setAction(AuditAction.MODIFY_USER.value());
        idmAuditLog.addAttributeAsJson(AuditAttributeName.PROFILE, profile, customJacksonMapper);
		try {
			User user = profile.getUser();
			if(user != null) {
				user.setNotifyUserViaEmail(false); /* edit user - don't send creds */
				Response wsResponse = userDataService.saveUserProfile(profile);
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
