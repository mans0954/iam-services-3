package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.bpm.activiti.delegate.core.AbstractDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class RemoveUserFromOrganization extends AbstractDelegate {

	//@Autowired
	//private OrganizationDataService organizationDataService;
	
	public RemoveUserFromOrganization() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String organizationId = (String)execution.getVariable(ActivitiConstants.ASSOCIATION_ID);
		final String userId = (String)execution.getVariable(ActivitiConstants.MEMBER_ASSOCIATION_ID);

		//final Organization entity = organizationDataService.getOrganization(organizationId, null);
		//if(entity != null) {
		//	entity.setOperation(AttributeOperationEnum.DELETE);
			final User user = getUser(userId);
			final ProvisionUser pUser = new ProvisionUser(user);
			pUser.markAffiliateAsDeleted(organizationId);
			//pUser.getAffiliations().add(entity);
			provisionService.modifyUser(pUser);
		//}
	}
}
