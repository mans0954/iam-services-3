package org.openiam.bpm.activiti.delegate.entitlements;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class RemoveSupervisor extends AbstractEntitlementsDelegate {
	
	@Autowired
	private UserDataService userDataService;
	
	@Autowired
	@Qualifier("userWS")
	private UserDataWebService userDataWebService;
	
	@Autowired
	@Qualifier("defaultProvision")
	private ProvisionService provisionService;
	
	public RemoveSupervisor() {
		super();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String superiorId = (String)execution.getVariable(ActivitiConstants.ASSOCIATION_ID);
		final String subordinateId = (String)execution.getVariable(ActivitiConstants.MEMBER_ASSOCIATION_ID);
		final User superior = userDataService.getUserDto(superiorId);
		final User subordinate = userDataService.getUserDto(subordinateId);
		
		if(superior != null && subordinate != null) {
			final ProvisionUser pUser = new ProvisionUser(subordinate);
			final List<User> superiors = userDataWebService.getSuperiors(subordinateId, -1, -1);
			if(CollectionUtils.isNotEmpty(superiors)) {
				superior.setOperation(AttributeOperationEnum.DELETE);
				superiors.remove(superior);
			}
            pUser.addSuperiors(superiors);
			provisionService.modifyUser(pUser);
		}
	}
	
	@Override
	protected String getNotificationType() {
		return null;
	}
}
