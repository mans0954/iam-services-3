package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.dozer.converter.GroupDozerConverter;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AddUserToGroup extends AbstractEntitlementsDelegate {

	@Autowired
	private GroupDataService groupDataService;
	
	public AddUserToGroup() {
		super();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String groupId = (String)execution.getVariable(ActivitiConstants.ASSOCIATION_ID);
		final String userId = getTargetUserId(execution);
		//groupDataService.addUserToGroup(groupId, userId);
		
		final GroupEntity entity = groupDataService.getGroup(groupId);
		if(entity != null) {
			final Group group = groupDataService.getGroupDTO(groupId);
			group.setOperation(AttributeOperationEnum.ADD);
			final User user = getUser(userId);
			final ProvisionUser pUser = new ProvisionUser(user);
			pUser.addGroup(group);
			provisionService.modifyUser(pUser);
		}
	}

	@Override
	protected String getNotificationType() {
		return null;
	}
	
	protected String getTargetUserId(final DelegateExecution execution) {
		return (String)execution.getVariable(ActivitiConstants.MEMBER_ASSOCIATION_ID);
	}
}
