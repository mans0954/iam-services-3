package org.openiam.bpm.activiti.delegate.user.edit;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.idm.srvc.prov.request.service.RequestDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.ProvisionService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class UpdateUser implements JavaDelegate {
	
	@Autowired
	@Qualifier("defaultProvision")
	private ProvisionService provisionService;
	
	@Autowired
	private UserDataService userDataService;
	
	@Autowired
	private UserDozerConverter dozerConverter;
	
	public UpdateUser() {
		SpringContextProvider.autowire(this);
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final UserStatusEnum primaryStatus = UserStatusEnum.getFromString((String)execution.getVariable(ActivitiConstants.USER_STATUS));
		final UserStatusEnum secondaryStatus = UserStatusEnum.getFromString((String)execution.getVariable(ActivitiConstants.USER_SECONDARY_STATUS));
		final String userId = (String)execution.getVariable(ActivitiConstants.ASSOCIATION_ID);
		
		final User user = userDataService.getUserDto(userId);
		if(user != null) {
			user.setStatus(primaryStatus);
			user.setSecondaryStatus(secondaryStatus);
			
			final ProvisionUser pUser = new ProvisionUser(user);
			provisionService.modifyUser(pUser);
			//userDataService.updateUser(user);
		}
	}

}
