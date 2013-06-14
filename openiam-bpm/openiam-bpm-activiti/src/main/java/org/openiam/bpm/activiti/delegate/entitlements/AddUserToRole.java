package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.domain.UserRoleEntity;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.openiam.idm.srvc.role.service.UserRoleDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;

public class AddUserToRole extends AbstractEntitlementsDelegate {
	
	@Autowired
	private RoleDAO roleDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private UserRoleDAO userRoleDAO;
	
	public AddUserToRole() {
		super();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String roleId = (String)execution.getVariable(ActivitiConstants.ASSOCIATION_ID);
		final String userId = (String)execution.getVariable(ActivitiConstants.MEMBER_ASSOCIATION_ID);
		final RoleEntity role = roleDAO.findById(roleId);
		final UserEntity user = userDAO.findById(userId);
		
		if(role != null && user != null && userRoleDAO.getRecord(userId, roleId) == null) {
			final UserRoleEntity entity = new UserRoleEntity();
			entity.setUserId(userId);
			entity.setRoleId(roleId);
			userRoleDAO.add(entity);
		}
	}

	@Override
	protected String getNotificationType() {
		return null;
	}

}
