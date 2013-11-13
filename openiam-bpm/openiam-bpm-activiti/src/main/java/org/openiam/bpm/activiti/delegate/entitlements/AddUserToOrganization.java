package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.org.service.OrganizationService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AddUserToOrganization extends AbstractEntitlementsDelegate {

	@Autowired
	private OrganizationService organizationService;
	
	public AddUserToOrganization() {
		super();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String organizationId = getStringVariable(execution, ActivitiConstants.ASSOCIATION_ID);
		final String userId = getTargetUserId(execution);

		final Organization entity = organizationService.getOrganizationDTO(organizationId);
		if(entity != null) {
			entity.setOperation(AttributeOperationEnum.ADD);
			final User user = getUser(userId);
			final ProvisionUser pUser = new ProvisionUser(user);
			pUser.addAffiliation(entity);
			provisionService.modifyUser(pUser);
		}
	}
}
