package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.springframework.beans.factory.annotation.Autowired;

public class DeleteOrganizationDelegate extends AbstractActivitiJob {
	
	@Autowired
	private OrganizationDataService organizationService;

	public DeleteOrganizationDelegate() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String organizationId = getStringVariable(execution, ActivitiConstants.ORGANIZATION_ID);
		final Response wsReponse = organizationService.deleteOrganization(organizationId);
		//TODO:  validate
	}
}
