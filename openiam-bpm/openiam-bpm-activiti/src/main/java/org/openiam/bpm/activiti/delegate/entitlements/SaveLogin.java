package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.bpm.activiti.delegate.core.AbstractDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.beans.factory.annotation.Autowired;

public class SaveLogin extends AbstractDelegate {
	
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
		final User user = getUser(userId);
		ProvisionUser pUser = new ProvisionUser(user);
		
		if(loginId != null) {
			if(CollectionUtils.isNotEmpty(pUser.getPrincipalList())) {
				for(final Login l : pUser.getPrincipalList()) {
					if(StringUtils.equals(l.getLoginId(), loginId)) {
						l.setLogin(login);
						l.setManagedSysId(managedSysId);
						l.setDomainId(domainId);
						l.setOperation(AttributeOperationEnum.REPLACE);
						break;
					}
				}
			}
		} else {
			final Login loginDTO = new Login();
			loginDTO.setUserId(userId);
			loginDTO.setLogin(login);
			loginDTO.setManagedSysId(managedSysId);
			loginDTO.setDomainId(domainId);
			loginDTO.setOperation(AttributeOperationEnum.ADD);
			pUser.addPrincipal(loginDTO);
		}
		
		provisionService.modifyUser(pUser);
	}
}
