package org.openiam.bpm.activiti.delegate.entitlements.displaymapper;

import java.util.LinkedHashMap;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;

public class LoginDisplayMapper implements JavaDelegate {
	
	@Autowired
	protected UserDataService userDataService;
	
	@Autowired
	private LoginDataService loginDataService;
	
	@Autowired
	private ManagedSystemService managedSystemService;

	public LoginDisplayMapper() {
		SpringContextProvider.autowire(this);
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final LinkedHashMap<String, String> metadataMap = new LinkedHashMap<String, String>();
		final String userId = (String)execution.getVariable(ActivitiConstants.USER_ID);
		final String login = (String)execution.getVariable(ActivitiConstants.LOGIN);
		final String managedSysId = (String)execution.getVariable(ActivitiConstants.MANAGED_SYS_ID);
		//final String domainId = (String)execution.getVariable(ActivitiConstants.SECURITY_DOMAIN_ID);
		String loginId = null;
		LoginEntity previousLogin = null;
		if(execution.hasVariable(ActivitiConstants.LOGIN_ID)) {
			loginId = (String)execution.getVariable(ActivitiConstants.LOGIN_ID);
			previousLogin = loginDataService.getLoginDetails(loginId);
		}
		
		final User user = userDataService.getUserDto(userId);
		metadataMap.put("Target User", user.getDisplayName());
		
		if(previousLogin != null) {
			metadataMap.put("Previous Login", previousLogin.getLogin());
		}
		metadataMap.put("New Login", login);
		
		if(previousLogin != null) {
			final ManagedSysEntity mSys = managedSystemService.getManagedSysById(previousLogin.getManagedSysId());
			metadataMap.put("Previous Managed System", mSys.getName());
		}
		final ManagedSysEntity newSys = managedSystemService.getManagedSysById(managedSysId);
		metadataMap.put("New Managed System", newSys.getName());
		
		execution.setVariable(ActivitiConstants.REQUEST_METADATA_MAP, metadataMap);
	}
}
