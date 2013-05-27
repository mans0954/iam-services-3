package org.openiam.bpm.activiti.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.log4j.Logger;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.prov.request.domain.ProvisionRequestEntity;
import org.openiam.idm.srvc.prov.request.service.RequestDataService;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.service.UserProfileService;
import org.openiam.idm.srvc.user.token.CreateUserToken;
import org.openiam.util.SpringContextProvider;
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
	
	public CreateNewUser() {
		SpringContextProvider.autowire(this);
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final Object provisionRequestIdObj = execution.getVariable(ActivitiConstants.PROVISION_REQUEST_ID);
		final String provisionRequestId = (String)provisionRequestIdObj;
		
		final ProvisionRequestEntity provisionRequest = provRequestService.getRequest(provisionRequestId);
		final NewUserProfileRequestModel request = (NewUserProfileRequestModel)new XStream().fromXML(provisionRequest.getRequestXML());
		final CreateUserToken token = userProfileService.createNewUserProfile(request);		
		execution.setVariable(ActivitiConstants.NEW_USER_ID, token.getUser().getUserId());
	}
}
