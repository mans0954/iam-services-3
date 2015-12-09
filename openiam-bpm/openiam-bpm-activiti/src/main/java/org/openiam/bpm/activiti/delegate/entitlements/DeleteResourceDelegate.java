package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.access.review.model.AccessViewResponse;
import org.openiam.access.review.service.AccessReviewService;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditSource;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
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
		final IdmAuditLogEntity idmAuditLog = createNewAuditLog(execution);
		idmAuditLog.setAction(AuditAction.DELETE_RESOURCE.value());
		try {
			if(resource != null) {
				idmAuditLog.setTargetResource(resource.getId(), resource.getName());
                wsResponse = resourceService.deleteResource(resource.getId(), systemUserId);
                if (wsResponse.isSuccess()) {
                    idmAuditLog.succeed();
                } else {
                    idmAuditLog.setFailureReason(wsResponse.getErrorCode());
                    idmAuditLog.setFailureReason(wsResponse.getErrorText());
                    throw new RuntimeException(String.format("Delete Resource failed: %s", wsResponse));
                }
			} else {
				throw new RuntimeException("Resource was null");
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
