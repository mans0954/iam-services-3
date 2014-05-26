package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditSource;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.springframework.beans.factory.annotation.Autowired;

public class DeleteGroupDelegate extends AbstractActivitiJob {

	@Autowired
	private GroupDataWebService groupDataService;
	
	public DeleteGroupDelegate() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Response wsResponse = null;
		final Group group = getObjectVariable(execution, ActivitiConstants.GROUP, Group.class);

        if(group != null) {
            IdmAuditLog idmAuditLog = new IdmAuditLog();
            idmAuditLog.setRequestorUserId(getRequestorId(execution));
            idmAuditLog.setAction(AuditAction.DELETE_GROUP.value());
            idmAuditLog.setAuditDescription("Delete group");
            idmAuditLog.setTargetGroup(group.getId(), group.getName());
            idmAuditLog.setTargetTask(execution.getId(),execution.getCurrentActivityName());
            idmAuditLog.setSource(AuditSource.WORKFLOW.value());
            try {
                wsResponse = groupDataService.deleteGroup(group.getId(), systemUserId);
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
