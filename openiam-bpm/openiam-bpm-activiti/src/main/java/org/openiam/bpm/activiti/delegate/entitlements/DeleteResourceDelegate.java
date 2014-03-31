package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.springframework.beans.factory.annotation.Autowired;

public class DeleteResourceDelegate extends AbstractActivitiJob {
	
	@Autowired
	private ResourceDataService resourceService;
	
	public DeleteResourceDelegate() {
		super();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Response wsResponse = null;
		final Resource resource = getObjectVariable(execution, ActivitiConstants.RESOURCE, Resource.class);
		if(resource != null) {
			wsResponse = resourceService.deleteResource(resource.getId(), systemUserId);
		}
		//TODO:  validate
	}

}
