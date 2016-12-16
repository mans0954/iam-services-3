package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;

public class SaveLogin extends AbstractActivitiJob {
	
	public SaveLogin() {
		super();
	}
	
	@Override
	protected void doExecute(DelegateExecution execution) throws Exception {
		final IdmAuditLogEntity idmAuditLog = createNewAuditLog(execution);
		idmAuditLog.setAction(AuditAction.SAVE_LOGIN.value());
		
		try {
			final Login loginObj = getObjectVariable(execution, ActivitiConstants.LOGIN, Login.class);
			final User user = getUser(loginObj.getUserId());
			ProvisionUser pUser = new ProvisionUser(user);
			
			if(loginObj.getId() != null) {
				if(CollectionUtils.isNotEmpty(pUser.getPrincipalList())) {
					for(final Login l : pUser.getPrincipalList()) {
						if(StringUtils.equals(l.getId(), loginObj.getId())) {
							l.setLogin(loginObj.getLogin());
							l.setManagedSysId(loginObj.getManagedSysId());
							l.setOperation(AttributeOperationEnum.REPLACE);
							break;
						}
					}
				}
			} else {
				final Login loginDTO = new Login();
				loginDTO.setUserId(loginObj.getUserId());
				loginDTO.setLogin(loginObj.getLogin());
				loginDTO.setManagedSysId(loginObj.getManagedSysId());
				loginDTO.setOperation(AttributeOperationEnum.ADD);
				pUser.addPrincipal(loginDTO);
			}
			
			final Response wsResponse = provisionService.modifyUser(pUser);
			if(wsResponse.isSuccess()) {
				idmAuditLog.succeed();
			} else {
                idmAuditLog.setFailureReason(wsResponse.getErrorCode());
                idmAuditLog.setFailureReason(wsResponse.getErrorText());
                throw new RuntimeException(String.format("Modify User failed; %s", wsResponse));
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
