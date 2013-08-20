package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.springframework.beans.factory.annotation.Autowired;

public class DeleteLogin extends AbstractEntitlementsDelegate {
	
	@Autowired
	private LoginDataService loginDataService;

	public DeleteLogin() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String loginId = (String)execution.getVariable(ActivitiConstants.LOGIN_ID);
		if(loginId != null) {
			loginDataService.deleteLogin(loginId);
		}
	}
	
	@Override
	protected String getNotificationType() {
		return null;
	}
}
