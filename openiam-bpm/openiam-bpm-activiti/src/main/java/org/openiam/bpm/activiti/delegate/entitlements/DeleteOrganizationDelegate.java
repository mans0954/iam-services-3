package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;

public class DeleteOrganizationDelegate extends AbstractActivitiJob {
	
	@Autowired
	private OrganizationService organizationService;

	public DeleteOrganizationDelegate() {
		super();
	}

    @Override
    protected void doExecute(DelegateExecution execution) throws Exception {
        Response wsResponse = null;
        final Organization organization = getObjectVariable(execution, ActivitiConstants.ORGANIZATION, Organization.class);
        final IdmAuditLogEntity idmAuditLog = createNewAuditLog(execution);
        idmAuditLog.setAction(AuditAction.DELETE_ORG.value());
        try {
        	if (organization != null) {
        		idmAuditLog.setTargetOrg(organization.getId(), organization.getName());
                wsResponse = organizationService.deleteOrganization(organization.getId(), systemUserId);
                if (wsResponse.isSuccess()) {
                    idmAuditLog.succeed();
                } else {
                    idmAuditLog.setFailureReason(wsResponse.getErrorCode());
                    idmAuditLog.setFailureReason(wsResponse.getErrorText());
                    throw new RuntimeException(String.format("Delete Organization failed; %s", wsResponse));
                }
	        } else {
	        	throw new RuntimeException("Organization was null");
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
