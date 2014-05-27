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
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(getRequestorId(execution));
        idmAuditLog.setSource(AuditSource.DELEGATE.value());
        if (resource.getId() == null) {
            idmAuditLog.setAction(AuditAction.ADD_RESOURCE.value());
            idmAuditLog.setAuditDescription("Create new resource");
        } else {
            idmAuditLog.setAction(AuditAction.EDIT_RESOURCE.value());
            idmAuditLog.setAuditDescription("Edit resource");
        }
        try {
            final Response response = resourceService.saveResource(resource, getRequestorId(execution));
            if (response.isSuccess()) {
                String resourceId = (String) response.getResponseValue();
                idmAuditLog.setTargetResource(resourceId, resource.getName());
                idmAuditLog.succeed();
            } else {
                idmAuditLog.fail();
                idmAuditLog.setFailureReason(response.getErrorCode());
                idmAuditLog.setFailureReason(response.getErrorText());
                idmAuditLog.setTargetResource(resource.getId(), resource.getName());
            }
        } finally {
           auditLogService.enqueue(idmAuditLog);
        }
    }

}
