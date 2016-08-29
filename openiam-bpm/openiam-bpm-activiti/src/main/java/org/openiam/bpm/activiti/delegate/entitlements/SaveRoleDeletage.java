package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.springframework.beans.factory.annotation.Autowired;

public class SaveRoleDeletage extends AbstractActivitiJob {

    @Autowired
    private RoleDataService roleService;

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
            final String id = roleService.saveRole(role, getRequestorId(execution));
            if (StringUtils.isNotBlank(id)) {
                idmAuditLog.setTargetRole(id, role.getName());
                idmAuditLog.succeed();
            } else {
                idmAuditLog.fail();
                idmAuditLog.setFailureReason(ResponseCode.INTERNAL_ERROR);
                idmAuditLog.setFailureReason("Can't save Role");
                idmAuditLog.setTargetRole(role.getId(), role.getName());
                throw new RuntimeException("Can't save role");
            }
        } catch (Throwable e) {
            idmAuditLog.setException(e);
            idmAuditLog.fail();
            throw new RuntimeException(e);
        } finally {
            addAuditLogChild(execution, idmAuditLog);
        }
    }
}
