package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;

public class SaveResourceDelegate extends AbstractActivitiJob {
	
	@Autowired
	private ResourceDataService resourceService;
	
	public SaveResourceDelegate() {
		super();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final Resource resource = getObjectVariable(execution, ActivitiConstants.RESOURCE, Resource.class);
		final Response response = resourceService.saveResource(resource, getRequestorId(execution));
		//TODO: set error flag
	}

}
