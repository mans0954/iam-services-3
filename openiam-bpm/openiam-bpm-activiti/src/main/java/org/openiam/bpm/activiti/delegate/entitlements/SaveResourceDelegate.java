package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;

public class SaveResourceDelegate extends AbstractActivitiJob {
	
	@Autowired
	private ResourceService resourceService;
	
	public SaveResourceDelegate() {
		super();
	}

    @Override
    protected void doExecute(DelegateExecution execution) throws Exception {
        final Resource resource = getObjectVariable(execution, ActivitiConstants.RESOURCE, Resource.class);
        final IdmAuditLogEntity idmAuditLog = createNewAuditLog(execution);
        if (resource.getId() == null) {
            idmAuditLog.setAction(AuditAction.ADD_RESOURCE.value());
            idmAuditLog.setAuditDescription("Create new resource");
        } else {
            idmAuditLog.setAction(AuditAction.EDIT_RESOURCE.value());
            idmAuditLog.setAuditDescription("Edit resource");
        }
        try {
            final Response response = resourceService.saveResourceWeb(resource, getRequestorId(execution));
            if (response.isSuccess()) {
                String resourceId = (String) response.getResponseValue();
                idmAuditLog.setTargetResource(resourceId, resource.getName());
                idmAuditLog.succeed();
            } else {
                idmAuditLog.fail();
                idmAuditLog.setFailureReason(response.getErrorCode());
                idmAuditLog.setFailureReason(response.getErrorText());
                idmAuditLog.setTargetResource(resource.getId(), resource.getName());
                throw new RuntimeException(String.format("Can't save resource: %s", response));
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
