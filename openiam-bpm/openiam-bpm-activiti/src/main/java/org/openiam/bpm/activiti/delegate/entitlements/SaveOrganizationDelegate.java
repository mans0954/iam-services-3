package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.springframework.beans.factory.annotation.Autowired;

public class SaveOrganizationDelegate extends AbstractActivitiJob {
	
	@Autowired
	private OrganizationDataService organizationService;

	public SaveOrganizationDelegate() {
		super();
	}
	

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final Organization organization = getObjectVariable(execution, ActivitiConstants.ORGANIZATION, Organization.class);
		final Response response = organizationService.saveOrganization(organization, getRequestorId(execution));
		//TODO: set error flag
	}
}
