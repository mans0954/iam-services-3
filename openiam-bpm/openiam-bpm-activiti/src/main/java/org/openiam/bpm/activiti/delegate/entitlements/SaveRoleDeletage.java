package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.springframework.beans.factory.annotation.Autowired;

public class SaveRoleDeletage extends AbstractActivitiJob {
	
	@Autowired
	private RoleDataWebService roleService;

	public SaveRoleDeletage() {
		super();
	}

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final Role role = getObjectVariable(execution, ActivitiConstants.ROLE, Role.class);
        final IdmAuditLogEntity idmAuditLog = createNewAuditLog(execution);
        if (role.getId() == null) {
            idmAuditLog.setAction(AuditAction.ADD_ROLE.value());
            idmAuditLog.setAuditDescription("Create new role");
        } else {
            idmAuditLog.setAction(AuditAction.EDIT_ROLE.value());
            idmAuditLog.setAuditDescription("Edit role");
        }
        try {
            final Response wsResponse = roleService.saveRole(role, getRequestorId(execution));
            if (wsResponse.isSuccess()) {
                String roleId = (String) wsResponse.getResponseValue();
                idmAuditLog.setTargetRole(roleId, role.getName());
                idmAuditLog.succeed();
            } else {
                idmAuditLog.fail();
                idmAuditLog.setFailureReason(wsResponse.getErrorCode());
                idmAuditLog.setFailureReason(wsResponse.getErrorText());
                idmAuditLog.setTargetRole(role.getId(), role.getName());
                throw new RuntimeException(String.format("Can't save role", wsResponse));
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
