package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.springframework.beans.factory.annotation.Autowired;

public class SaveRoleDeletage extends AbstractActivitiJob {
	
	@Autowired
	private RoleDataWebService roleService;

	public SaveRoleDeletage() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final Role role = getObjectVariable(execution, ActivitiConstants.ROLE, Role.class);
		final Response wsResponse = roleService.saveRole(role, getRequestorId(execution));
		//TODO:  validate
	}
}
