package org.openiam.bpm.activiti.delegate.user.newuser;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.prov.request.domain.ProvisionRequestEntity;
import org.openiam.idm.srvc.prov.request.service.RequestDataService;
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

public class CreateNewUser implements JavaDelegate {

	private static Logger log = Logger.getLogger(CreateNewUser.class);
	
	@Autowired
	@Qualifier("provRequestService")
	private RequestDataService provRequestService;
	
	@Autowired
	private UserProfileService userProfileService;
	
	@Autowired
	@Qualifier("defaultProvision")
	private ProvisionService provisionService;
	
	@Autowired
	LoginDataService loginDataService;
	
	@Autowired
	private NewUserModelToProvisionConverter converter;
	
	public CreateNewUser() {
		SpringContextProvider.autowire(this);
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String provisionRequestId = (String)execution.getVariable(ActivitiConstants.PROVISION_REQUEST_ID);
		
		final ProvisionRequestEntity provisionRequest = provRequestService.getRequest(provisionRequestId);
		final NewUserProfileRequestModel request = (NewUserProfileRequestModel)new XStream().fromXML(provisionRequest.getRequestXML());
		//final CreateUserToken token = userProfileService.createNewUserProfile(request);
		final ProvisionUser user = converter.convertNewProfileModel(request);
		user.setStatus(UserStatusEnum.PENDING_INITIAL_LOGIN);
		user.setSecondaryStatus(null);
		
		final ProvisionUserResponse response = provisionService.addUser(user);
		if(ResponseStatus.SUCCESS.equals(response.getStatus()) && response.getUser() != null && StringUtils.isNotBlank(response.getUser().getUser().getUserId())) {
			final String userId = response.getUser().getUserId();
			execution.setVariable(ActivitiConstants.NEW_USER_ID, userId);
		} else {
			throw new Exception("Could not save User Profile using Provisioning Service - can't continue");
		}
	}
}
