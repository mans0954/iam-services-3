package org.openiam.bpm.activiti.delegate.entitlements;

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
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AddSupervisor extends AbstractActivitiJob {
	
	@Autowired
	@Qualifier("userWS")
	private UserDataWebService userDataWebService;
	
	public AddSupervisor() {
		super();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String superiorId = getStringVariable(execution, ActivitiConstants.ASSOCIATION_ID);
		final String subordinateId = getStringVariable(execution, ActivitiConstants.MEMBER_ASSOCIATION_ID);
		final User superior = getUser(superiorId);
		final User subordinate = getUser(subordinateId);
		
		final IdmAuditLogEntity idmAuditLog = createNewAuditLog(execution);
        idmAuditLog.setAction(AuditAction.ADD_SUPERVISOR.value());
		try {
			if(superior != null && subordinate != null) {
				idmAuditLog.setTargetUser(subordinate.getId(), null);
				final ProvisionUser pUser = new ProvisionUser(subordinate);
				List<User> superiors = userDataWebService.getSuperiors(subordinateId, -1, -1);
				superiors = (superiors != null) ? superiors : new LinkedList<User>();
				superior.setOperation(AttributeOperationEnum.ADD);
				superiors.add(superior);
				pUser.addSuperiors(superiors);
				final Response wsResponse = provisionService.modifyUser(pUser);
				if(wsResponse.isFailure()) {
                    idmAuditLog.setFailureReason(wsResponse.getErrorCode());
                    idmAuditLog.setFailureReason(wsResponse.getErrorText());
                    throw new RuntimeException(String.format("Modify User failed; %s", wsResponse));
				} else {
					idmAuditLog.succeed();
				}
			} else {
				throw new RuntimeException("SUpervisor or Subordinate were null");
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
