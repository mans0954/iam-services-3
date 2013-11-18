package org.openiam.bpm.activiti.delegate.entitlements.preprocess;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.activiti.delegate.entitlements.AbstractEntitlementsDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;

public class CRUDPreprocessor extends AbstractEntitlementsDelegate {
	
	@Autowired
	private ResourceService resourceService;
	
	@Autowired
	private AuthorizationManagerService authManagerService;

	public CRUDPreprocessor() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final AssociationType type = AssociationType.getByValue(getStringVariable(execution, ActivitiConstants.ASSOCIATION_TYPE));
		final String associationId = getStringVariable(execution, ActivitiConstants.ASSOCIATION_ID);
		final String requestorId = getRequestorId(execution);
		
		boolean isAdmin = false;
		if(type != null) {
			switch(type) {
				case RESOURCE:
					final ResourceEntity resource = resourceService.findResourceById(associationId);
					if(resource != null && resource.getAdminResource() != null) {
						isAdmin = authManagerService.isEntitled(requestorId, resource.getAdminResource().getResourceId());
					}
					break;
				default:
					break;
			}
		}
		execution.setVariable(ActivitiConstants.IS_ADMIN.getName(), isAdmin);
	}
}
