package org.openiam.bpm.activiti.delegate.entitlements;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ReplaceSuperior extends AbstractActivitiJob {
    @Autowired
    @Qualifier("userManager")
    private UserDataService userManager;

    public ReplaceSuperior() {
        super();
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        final String currSuperiorId = (String)execution.getVariable("CurrentSuperiorID");
        final String newSuperiorId = (String)execution.getVariable("NewSuperiorID");
        final String subordinateId = getStringVariable(execution, ActivitiConstants.MEMBER_ASSOCIATION_ID);

        final User currSuperior = getUser(currSuperiorId);
        final User newSuperior = getUser(newSuperiorId);
        final User subordinate = getUser(subordinateId);

        final IdmAuditLogEntity idmAuditLog = createNewAuditLog(execution);
        idmAuditLog.setAction(AuditAction.REPLACE_SUPERVISOR.value());
        idmAuditLog.setTargetUser(subordinate.getId(), null);

        try {
            if (subordinate != null) {
                final ProvisionUser pUser = new ProvisionUser(subordinate);
                List<User> superiors = userManager.getSuperiorsDto(subordinateId, 0, Integer.MAX_VALUE);
                superiors = (superiors != null) ? superiors : new LinkedList<User>();

                if (currSuperior != null) {
                    for (User u: superiors) {
                        if (u.getId() == currSuperior.getId()) {
                            u.setOperation(AttributeOperationEnum.DELETE);
                            idmAuditLog.put(AuditAction.DELETE_SUPERVISOR.value(), u.getId());
                            break;
                        }
                    }
                }

                if(newSuperior != null) {
                    boolean exists = false;
                    for (User u: superiors) {
                        if (u.getId() == newSuperior.getId()) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        newSuperior.setOperation(AttributeOperationEnum.ADD);
                        superiors.add(newSuperior);
                        idmAuditLog.put(AuditAction.ADD_SUPERVISOR.value(), newSuperior.getId());
                    }
                } else {
                    throw new RuntimeException("Supervisor can not be null");
                }

                pUser.setSuperiors(new HashSet<User>(superiors));
                final Response wsResponse = provisionService.modifyUser(pUser);

                if(wsResponse.isFailure()) {
                    idmAuditLog.setFailureReason(wsResponse.getErrorCode());
                    idmAuditLog.setFailureReason(wsResponse.getErrorText());
                    throw new RuntimeException(String.format("Modify User failed; %s", wsResponse));
                } else {
                    idmAuditLog.succeed();
                }

            } else {
                throw new RuntimeException("Subordinate can not be null");
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
