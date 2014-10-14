package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.beans.factory.annotation.Autowired;

public class DeleteLogin extends AbstractActivitiJob {
	
	@Autowired
	private LoginDataService loginDataService;

	public DeleteLogin() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final Login loginObj = getObjectVariable(execution, ActivitiConstants.LOGIN, Login.class);
		final String loginId = loginObj.getId();
		final IdmAuditLog idmAuditLog = createNewAuditLog(execution);
         idmAuditLog.setAction(AuditAction.DELETE_PRINCIPAL.value());
         try {
        	 if(loginId != null) {
        		 final Login login = loginDataService.getLoginDTO(loginId);
        		 if(login != null) {
        			 final User user = getUser(login.getUserId());
        			 idmAuditLog.setTargetUser(user.getId(), login.getLogin());
        			 idmAuditLog.setPrincipal(login.getLogin());
        			 
        			 final ProvisionUser pUser = new ProvisionUser(user);
        			 if (CollectionUtils.isNotEmpty(pUser.getPrincipalList())) {
        				 for (final Login l : pUser.getPrincipalList()) {
        					 if (l.getId().equals(login.getId())) {
	                             l.setStatus(LoginStatusEnum.INACTIVE);
	                             l.setOperation(AttributeOperationEnum.REPLACE);
	                             break;
        					 }
        				 }
        			 }
        			 final Response wsResponse = provisionService.modifyUser(pUser);
        			 if(wsResponse.isSuccess()) {
        				 idmAuditLog.succeed();
        			 } else {
                         idmAuditLog.setFailureReason(wsResponse.getErrorCode());
                         idmAuditLog.setFailureReason(wsResponse.getErrorText());
                         throw new RuntimeException(String.format("Modify User failed; %s", wsResponse));
        			 }
        		 } else {
        			 throw new RuntimeException("Login was null");
        		 }
        	 } else {
        		 throw new RuntimeException("Login ID was null");
        	 }
         } catch(Throwable e) {
 			idmAuditLog.setException(e);
 			idmAuditLog.fail();
 			throw new RuntimeException(e);
 		} finally {
 			addAuditLogChild(execution, idmAuditLog);
 		}
	}
	
	protected String getTargetUserId(final DelegateExecution execution) {
		return null;
	}
}
