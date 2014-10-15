package org.openiam.bpm.activiti.delegate.user.newuser;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.bpm.activiti.delegate.entitlements.AbstractEntitlementsDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.exception.CustomActivitiException;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.provision.NewUserModelToProvisionConverter;
import org.openiam.idm.srvc.pswd.service.PasswordGenerator;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserProfileService;
import org.openiam.idm.srvc.user.token.CreateUserToken;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.service.ProvisionService;
import org.openiam.util.SpringContextProvider;
import org.opensaml.saml1.core.validator.ResponseSchemaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.thoughtworks.xstream.XStream;

public class CreateNewUser extends AbstractEntitlementsDelegate {

	private static Logger log = Logger.getLogger(CreateNewUser.class);
	
	@Autowired
	private NewUserModelToProvisionConverter converter;
	
	public CreateNewUser() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final NewUserProfileRequestModel request = getObjectVariable(execution, ActivitiConstants.REQUEST, NewUserProfileRequestModel.class);
		final IdmAuditLog idmAuditLog = createNewAuditLog(execution);
        idmAuditLog.setAction(AuditAction.CREATE_USER.value());
        idmAuditLog.addAttributeAsJson(AuditAttributeName.PROFILE, request, customJacksonMapper);
		try {
			final ProvisionUser user = converter.convertNewProfileModel(request);
	        user.setEmailCredentialsToNewUsers(false);
			user.setStatus(UserStatusEnum.PENDING_INITIAL_LOGIN);
			user.setSecondaryStatus(null);
			
			final ProvisionUserResponse response = provisionService.addUser(user);
			if(ResponseStatus.SUCCESS.equals(response.getStatus()) && response.getUser() != null && StringUtils.isNotBlank(response.getUser().getId())) {
				final String userId = response.getUser().getId();
				execution.setVariable(ActivitiConstants.NEW_USER_ID.getName(), userId);
				idmAuditLog.setTargetUser(userId, null);
			} else {
				final String message = String.format("Could not save User Profile using Provisioning Service - can't continue.  Response Error Code: %s.  Response Error Text: %s", response.getErrorCode(), response.getErrorText());
				idmAuditLog.addWarning(message);
				throw new CustomActivitiException(response.getErrorCode(), message);
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
