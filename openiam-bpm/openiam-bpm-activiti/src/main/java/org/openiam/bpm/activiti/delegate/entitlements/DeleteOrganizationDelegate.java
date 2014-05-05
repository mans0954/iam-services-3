package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.org.dto.Organization;
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
		Response wsResponse = null;
		final Organization organization = getObjectVariable(execution, ActivitiConstants.ORGANIZATION, Organization.class);
		if(organization != null) {
			wsResponse = organizationService.deleteOrganization(organization.getId());
		}
		//TODO:  validate
	}
}
