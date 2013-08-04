package org.openiam.bpm.activiti.delegate.user.edit;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.idm.srvc.prov.request.service.RequestDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.ProvisionService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.thoughtworks.xstream.XStream;

public class UpdateUser implements JavaDelegate {
	
	@Autowired
	@Qualifier("defaultProvision")
	private ProvisionService provisionService;
	
	@Autowired
	private UserDataWebService userDataService;
	
	@Autowired
	private UserDozerConverter dozerConverter;
	
	public UpdateUser() {
		SpringContextProvider.autowire(this);
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final UserProfileRequestModel profile = (UserProfileRequestModel)new XStream().fromXML((String)execution.getVariable(ActivitiConstants.USER_PROFILE));
		final String userId = (String)execution.getVariable(ActivitiConstants.ASSOCIATION_ID);
		
		User user = profile.getUser();
		if(user != null) {
			userDataService.saveUserInfo(user, null);
			user = userDataService.getUserWithDependent(user.getUserId(), null, true);
			
			final ProvisionUser pUser = new ProvisionUser(user);
			provisionService.modifyUser(pUser);
		}
	}

}
