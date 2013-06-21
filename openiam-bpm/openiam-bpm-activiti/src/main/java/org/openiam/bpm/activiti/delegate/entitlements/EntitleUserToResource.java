package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.dozer.converter.ResourceDozerConverter;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceUserEntity;
import org.openiam.idm.srvc.res.dto.ResourceUser;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.res.service.ResourceUserDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.dto.UserResourceAssociation;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class EntitleUserToResource extends AbstractEntitlementsDelegate {

	@Autowired
	@Qualifier("defaultProvision")
	private ProvisionService provisionService;
	
	@Autowired
	private UserDataService userDataService;
	
	@Autowired
	private ResourceService resourceService;
	
	@Autowired
	private ResourceDozerConverter resourceDozerConverter;
	
	public EntitleUserToResource() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String resourceId = (String)execution.getVariable(ActivitiConstants.ASSOCIATION_ID);
		final String userId = (String)execution.getVariable(ActivitiConstants.MEMBER_ASSOCIATION_ID);	
		
		final User user = userDataService.getUserDto(userId);
		final ResourceEntity entity = resourceService.findResourceById(resourceId);
		if(user != null && entity != null) {
			final ProvisionUser pUser = new ProvisionUser(user);
			final UserResourceAssociation association = new UserResourceAssociation(resourceId, AttributeOperationEnum.ADD);
			association.setManagedSystemId(entity.getManagedSysId());
			pUser.addResourceUserAssociation(association);
			provisionService.modifyUser(pUser);
		}
		
		/*
		final ResourceEntity resource = resourceDAO.findById(resourceId);
		final UserEntity user = userDAO.findById(userId);
		if(resource != null && user != null) {
			final ResourceUserEntity toSave = new ResourceUserEntity();
			toSave.setUserId(userId);
			toSave.setResourceId(resourceId);
			resourceUserDAO.save(toSave);
		}
		*/
	}

	@Override
	protected String getNotificationType() {
		return null;
	}
}
