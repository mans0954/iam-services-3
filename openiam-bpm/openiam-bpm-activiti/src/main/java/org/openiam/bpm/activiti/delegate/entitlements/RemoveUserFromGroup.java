package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.domain.UserGroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.grp.service.UserGroupDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;

public class RemoveUserFromGroup extends AbstractEntitlementsDelegate {
	
	@Autowired
	private GroupDataService groupDataService;
	
	public RemoveUserFromGroup() {
		super();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String groupId = (String)execution.getVariable(ActivitiConstants.ASSOCIATION_ID);
		final String userId = (String)execution.getVariable(ActivitiConstants.MEMBER_ASSOCIATION_ID);
		groupDataService.removeUserFromGroup(groupId, userId);
		/*
		final UserGroupEntity entity = userGroupDAO.getRecord(groupId, userId); 
		if(entity != null) {
			
			userGroupDAO.delete(entity);
		}
		*/
	}

	@Override
	protected String getNotificationType() {
		return null;
	}
}
