package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;
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
			final Login login = loginDataService.getLoginDTO(loginId);
			if(login != null) {
				final User user = getUser(login.getUserId());
				final ProvisionUser pUser = new ProvisionUser(user);
				if (CollectionUtils.isNotEmpty(pUser.getPrincipalList())) {
                    for (final Login l : pUser.getPrincipalList()) {
                        if (l.getLoginId().equals(login.getLoginId())) {
                            l.setStatus("INACTIVE");
                            l.setOperation(AttributeOperationEnum.REPLACE);
                            break;
                        }
                    }
                }
				provisionService.modifyUser(pUser);
			}
		}
	}
	
	@Override
	protected String getNotificationType() {
		return null;
	}
}
