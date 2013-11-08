package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.bpm.activiti.delegate.core.AbstractDelegate;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.dozer.converter.RoleDozerConverter;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class RemoveUserFromRole extends AbstractDelegate {

	@Autowired
	private RoleDataService roleDataService;

	public RemoveUserFromRole() {
		super();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String roleId = (String)execution.getVariable(ActivitiConstants.ASSOCIATION_ID);
		final String userId = (String)execution.getVariable(ActivitiConstants.MEMBER_ASSOCIATION_ID);
		
		final User user = getUser(userId);
		
		final ProvisionUser pUser = new ProvisionUser(user);
		pUser.markRoleAsDeleted(roleId);
		provisionService.modifyUser(pUser);
	}
}
