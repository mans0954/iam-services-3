package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.dozer.converter.ResourceDozerConverter;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.dto.UserResourceAssociation;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class EntitleUserToResource extends AbstractEntitlementsDelegate {
	
	@Autowired
	private ResourceService resourceService;

	public EntitleUserToResource() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String resourceId = (String)execution.getVariable(ActivitiConstants.ASSOCIATION_ID);
		final String userId = getTargetUserId(execution);
		
		final User user = getUser(userId);
		final ResourceEntity entity = resourceService.findResourceById(resourceId);
		if(user != null && entity != null) {
			final ProvisionUser pUser = new ProvisionUser(user);
            final Resource resource = resourceService.getResourceDTO(resourceId);
            resource.setOperation(AttributeOperationEnum.ADD);
            pUser.addResource(resource);
			provisionService.modifyUser(pUser);
		}
	}

	protected String getTargetUserId(final DelegateExecution execution) {
		return (String)execution.getVariable(ActivitiConstants.MEMBER_ASSOCIATION_ID);
	}
}
