package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditSource;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
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
            IdmAuditLog idmAuditLog = new IdmAuditLog();
            idmAuditLog.setRequestorUserId(getRequestorId(execution));
            idmAuditLog.setAction(AuditAction.DELETE_RESOURCE.value());
            idmAuditLog.setAuditDescription("Delete resource");
            idmAuditLog.setTargetResource(resource.getId(), resource.getName());
            idmAuditLog.setTargetTask(execution.getId(),execution.getCurrentActivityName());
            idmAuditLog.setSource(AuditSource.WORKFLOW.value());
            try {
                wsResponse = resourceService.deleteResource(resource.getId(), systemUserId);
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
