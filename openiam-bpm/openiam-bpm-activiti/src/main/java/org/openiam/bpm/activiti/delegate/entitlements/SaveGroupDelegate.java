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

public class SaveGroupDelegate extends AbstractActivitiJob {

	@Autowired
	private GroupDataWebService groupDataService;
	
	public SaveGroupDelegate() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final Group group = getObjectVariable(execution, ActivitiConstants.GROUP, Group.class);
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(getRequestorId(execution));
        idmAuditLog.setSource(AuditSource.WORKFLOW.value());
        idmAuditLog.setTargetTask(execution.getId(),execution.getCurrentActivityName());
        if (group.getId() == null) {
            idmAuditLog.setAction(AuditAction.ADD_GROUP.value());
            idmAuditLog.setAuditDescription("Create new group");
        } else {
            idmAuditLog.setAction(AuditAction.EDIT_GROUP.value());
            idmAuditLog.setAuditDescription("Edit group");
        }
        try {
            final Response response =  groupDataService.saveGroup(group, getRequestorId(execution));
            if (response.isSuccess()) {
                String groupId = (String) response.getResponseValue();
                idmAuditLog.setTargetGroup(groupId, group.getName());
                idmAuditLog.succeed();
            } else {
                idmAuditLog.fail();
                idmAuditLog.setFailureReason(response.getErrorCode());
                idmAuditLog.setFailureReason(response.getErrorText());
                idmAuditLog.setTargetGroup(group.getId(), group.getName());
            }
        } finally {
            auditLogService.enqueue(idmAuditLog);
        }
	}
}
