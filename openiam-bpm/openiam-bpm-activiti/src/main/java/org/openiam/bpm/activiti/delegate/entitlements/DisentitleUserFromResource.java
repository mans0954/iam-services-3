package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
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
	
	
	public DisentitleUserFromResource() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String resourceId = getStringVariable(execution, ActivitiConstants.ASSOCIATION_ID);
		final String userId = getTargetUserId(execution);
		
		final User user = getUser(userId);
		if(user != null) {
            final ProvisionUser pUser = new ProvisionUser(user);
            pUser.markResourceAsDeleted(resourceId);
            provisionService.modifyUser(pUser);
		}
	}
}
