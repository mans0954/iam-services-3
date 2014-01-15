package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.springframework.beans.factory.annotation.Autowired;

public class DeleteRoleDelegate extends AbstractActivitiJob {
	
	@Autowired
	private RoleDataWebService roleService;

	public DeleteRoleDelegate() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Response wsResponse = null;
		final Role role = getObjectVariable(execution, ActivitiConstants.ROLE, Role.class);
		if(role != null) {
			wsResponse = roleService.removeRole(role.getId());
		}
		//TODO:  validate
	}
}
