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
        if (organization != null) {
            IdmAuditLog idmAuditLog = new IdmAuditLog();
            idmAuditLog.setRequestorUserId(getRequestorId(execution));
            idmAuditLog.setAction(AuditAction.DELETE_ORG.value());
            idmAuditLog.setAuditDescription("Delete organization");
            idmAuditLog.setTargetOrg(organization.getId(), organization.getName());
            idmAuditLog.setTargetTask(execution.getId(),execution.getCurrentActivityName());
            idmAuditLog.setSource(AuditSource.WORKFLOW.value());
            try {
                wsResponse = organizationService.deleteOrganization(organization.getId());
                if (wsResponse.isSuccess()) {
                    idmAuditLog.succeed();
                } else {
                    idmAuditLog.fail();
                    idmAuditLog.setFailureReason(wsResponse.getErrorCode());
                    idmAuditLog.setFailureReason(wsResponse.getErrorText());
                }
            } finally {
                auditLogService.enqueue(idmAuditLog);
            }
        }

    }
}
