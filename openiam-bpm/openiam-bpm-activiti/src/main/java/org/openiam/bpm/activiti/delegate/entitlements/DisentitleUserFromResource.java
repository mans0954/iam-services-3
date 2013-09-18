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

public class DisentitleUserFromResource extends AbstractEntitlementsDelegate {

	@Autowired
	@Qualifier("defaultProvision")
	private ProvisionService provisionService;
	
	@Autowired
	private UserDataService userDataService;
	
	@Autowired
	private ResourceService resourceService;
	
	@Autowired
	private ResourceDozerConverter resourceDozerConverter;

    @Autowired
    private ResourceDozerConverter resourceDozerMapper;
	
	public DisentitleUserFromResource() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String resourceId = (String)execution.getVariable(ActivitiConstants.ASSOCIATION_ID);
		final String userId = (String)execution.getVariable(ActivitiConstants.MEMBER_ASSOCIATION_ID);
		
		User user = userDataService.getUserDto(userId);
		final ResourceEntity entity = resourceService.findResourceById(resourceId);
		if(user != null && entity != null) {
            final ProvisionUser pUser = new ProvisionUser(user);
            final Resource resource = resourceService.getResourceDTO(resourceId);
            resource.setOperation(AttributeOperationEnum.DELETE);
            pUser.getResources().add(resource);
            provisionService.modifyUser(pUser);
		}
		/*
		final ResourceUserEntity entity = resourceUserDAO.getRecord(resourceId, userId);
		if (entity != null) {
			resourceUserDAO.delete(entity);
		}
		*/
	}

	@Override
	protected String getNotificationType() {
		return null;
	}
}
