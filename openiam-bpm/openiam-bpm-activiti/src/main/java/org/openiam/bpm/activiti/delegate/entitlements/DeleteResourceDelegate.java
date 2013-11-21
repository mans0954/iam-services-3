package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
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
		final String resourceId = getStringVariable(execution, ActivitiConstants.RESOURCE_ID);
		final Response wsReponse = resourceService.deleteResource(resourceId);
		//TODO:  validate
	}

}
