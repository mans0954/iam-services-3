package org.openiam.bpm.activiti.delegate.user.edit;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.openiam.bpm.activiti.delegate.entitlements.AbstractEntitlementsDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.idm.srvc.prov.request.domain.ProvisionRequestEntity;
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
		
		User user = profile.getUser();
		if(user != null) {
			user.setNotifyUserViaEmail(false); /* edit user - don't send creds */
			userDataService.saveUserProfile(profile);
			//userDataService.saveUserInfo(user, null);
			user = getUser(user.getUserId());
			
			final ProvisionUser pUser = new ProvisionUser(user);
			provisionService.modifyUser(pUser);
		}
	}

}
