package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditSource;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
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

        final IdmAuditLog idmAuditLog = createNewAuditLog(execution);
        if (organization.getId() == null) {
            idmAuditLog.setAction(AuditAction.ADD_ORG.value());
            idmAuditLog.setAuditDescription("Create new organization");
        } else {
            idmAuditLog.setAction(AuditAction.EDIT_ORG.value());
            idmAuditLog.setAuditDescription("Edit organization");
        }
        try {
            final Response response = organizationService.saveOrganization(organization, getRequestorId(execution));
            if (response.isSuccess()) {
                String orgId = (String) response.getResponseValue();
                idmAuditLog.setTargetOrg(orgId, organization.getName());
                idmAuditLog.succeed();
            } else {
                idmAuditLog.fail();
                idmAuditLog.setFailureReason(response.getErrorCode());
                idmAuditLog.setFailureReason(response.getErrorText());
                idmAuditLog.setTargetOrg(organization.getId(), organization.getName());
                throw new RuntimeException(String.format("Can't save organization: %s", response));
            }
        } catch(Throwable e) {
 			idmAuditLog.setException(e);
 			idmAuditLog.fail();
 			throw new RuntimeException(e);
 		} finally {
 			addAuditLogChild(execution, idmAuditLog);
 		}
	}
}
