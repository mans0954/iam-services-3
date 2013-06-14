package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.role.domain.UserRoleEntity;
import org.openiam.idm.srvc.role.service.UserRoleDAO;
import org.springframework.beans.factory.annotation.Autowired;

public class RemoveUserFromRole extends AbstractEntitlementsDelegate {

	@Autowired
	private UserRoleDAO userRoleDAO;
	
	public RemoveUserFromRole() {
		super();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String roleId = (String)execution.getVariable(ActivitiConstants.ASSOCIATION_ID);
		final String userId = (String)execution.getVariable(ActivitiConstants.MEMBER_ASSOCIATION_ID);
		
		final UserRoleEntity entity = userRoleDAO.getRecord(userId, roleId);
		if(entity != null) {
			userRoleDAO.delete(entity);
		}
	}

	@Override
	protected String getNotificationType() {
		return null;
	}
}
