package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditSource;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.springframework.beans.factory.annotation.Autowired;

public class DeleteRoleDelegate extends AbstractActivitiJob {
	
	@Autowired
	private RoleDataWebService roleService;

	public DeleteRoleDelegate() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Response wsResponse = null;
		final Role role = getObjectVariable(execution, ActivitiConstants.ROLE, Role.class);
		final IdmAuditLog idmAuditLog = createNewAuditLog(execution);
		idmAuditLog.setAction(AuditAction.DELETE_ROLE.value());
		try {
	        if(role != null) {
	        	idmAuditLog.setTargetRole(role.getId(), role.getName());
                wsResponse = roleService.removeRole(role.getId(), systemUserId);
                if (wsResponse.isSuccess()) {
                    idmAuditLog.succeed();
                } else {
                    idmAuditLog.fail();
                    idmAuditLog.setFailureReason(wsResponse.getErrorCode());
                    idmAuditLog.setFailureReason(wsResponse.getErrorText());
                }
	        } else {
	        	throw new RuntimeException("Role was null");
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
