package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.springframework.beans.factory.annotation.Autowired;

public class SaveLogin extends AbstractEntitlementsDelegate {
	
	@Autowired
	private LoginDataService loginDataService;

	public SaveLogin() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String userId = (String)execution.getVariable(ActivitiConstants.USER_ID);
		final String login = (String)execution.getVariable(ActivitiConstants.LOGIN);
		final String managedSysId = (String)execution.getVariable(ActivitiConstants.MANAGED_SYS_ID);
		final String domainId = (String)execution.getVariable(ActivitiConstants.SECURITY_DOMAIN_ID);
		String loginId = null;
		if(execution.hasVariable(ActivitiConstants.LOGIN_ID)) {
			loginId = (String)execution.getVariable(ActivitiConstants.LOGIN_ID);
		}
		LoginEntity loginEntity = null;
		if(loginId != null) {
			loginEntity = loginDataService.getLoginDetails(loginId);
		} else {
			loginEntity = new LoginEntity();
			loginEntity.setUserId(userId);
		}
		loginEntity.setLogin(login);
		loginEntity.setManagedSysId(managedSysId);
		loginEntity.setDomainId(domainId);
		
		if(loginId == null) {
			loginDataService.addLogin(loginEntity);
		} else {
			loginDataService.updateLogin(loginEntity);
		}
	}
	
	@Override
	protected String getNotificationType() {
		return null;
	}
}
