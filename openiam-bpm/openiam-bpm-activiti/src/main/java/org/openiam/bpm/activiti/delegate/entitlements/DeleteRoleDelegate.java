package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
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
		final String roleId = getStringVariable(execution, ActivitiConstants.ROLE_ID);
		final Response wsReponse = roleService.removeRole(roleId);
		//TODO:  validate
	}
}
