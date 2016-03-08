package org.openiam.bpm.activiti.delegate.entitlements;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class RemoveSupervisor extends AbstractActivitiJob {
	
	@Autowired
	private UserDataService userDataService;
	
	@Autowired
	@Qualifier("userWS")
	private UserDataWebService userDataWebService;
	
	@Autowired
	@Qualifier("defaultProvision")
	private ProvisionService provisionService;
	
	public RemoveSupervisor() {
		super();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String superiorId = getStringVariable(execution, ActivitiConstants.ASSOCIATION_ID);
		final String subordinateId = getStringVariable(execution, ActivitiConstants.MEMBER_ASSOCIATION_ID);
		final User superior = userDataService.getUserDto(superiorId);
		final User subordinate = userDataService.getUserDto(subordinateId);
		
		final IdmAuditLogEntity idmAuditLog = createNewAuditLog(execution);
        idmAuditLog.setAction(AuditAction.DELETE_SUPERVISOR.value());
        try {
			if(superior != null && subordinate != null) {
				final ProvisionUser pUser = new ProvisionUser(subordinate);
				final List<User> superiors = userDataWebService.getSuperiors(subordinateId, -1, -1);
				if(CollectionUtils.isNotEmpty(superiors)) {
					superior.setOperation(AttributeOperationEnum.DELETE);
					pUser.addSuperior(superior);
					//superiors.remove(superior);
				}
	            pUser.addSuperiors(superiors);
				final Response wsResponse = provisionService.modifyUser(pUser);
				if(wsResponse.isSuccess()) {
   				 	idmAuditLog.succeed();
   			 	} else {
                    idmAuditLog.setFailureReason(wsResponse.getErrorCode());
                    idmAuditLog.setFailureReason(wsResponse.getErrorText());
                    throw new RuntimeException(String.format("Modify User failed; %s", wsResponse));
   			 	}
			} else {
				throw new RuntimeException(String.format("superior or subordinate was null"));
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
