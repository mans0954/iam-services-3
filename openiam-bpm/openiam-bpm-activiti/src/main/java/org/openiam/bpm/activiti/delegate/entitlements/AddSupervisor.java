package org.openiam.bpm.activiti.delegate.entitlements;

import java.util.LinkedList;
import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AddSupervisor extends AbstractActivitiJob {
	
	@Autowired
	@Qualifier("userWS")
	private UserDataWebService userDataWebService;
	
	public AddSupervisor() {
		super();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String superiorId = getStringVariable(execution, ActivitiConstants.ASSOCIATION_ID);
		final String subordinateId = getStringVariable(execution, ActivitiConstants.MEMBER_ASSOCIATION_ID);
		final User superior = getUser(superiorId);
		final User subordinate = getUser(subordinateId);
		
		if(superior != null && subordinate != null) {
			final ProvisionUser pUser = new ProvisionUser(subordinate);
			List<User> superiors = userDataWebService.getSuperiors(subordinateId, -1, -1);
			superiors = (superiors != null) ? superiors : new LinkedList<User>();
			superior.setOperation(AttributeOperationEnum.ADD);
			superiors.add(superior);
			pUser.addSuperiors(superiors);
			provisionService.modifyUser(pUser);
		}
	}
}
