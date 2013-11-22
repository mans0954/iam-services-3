package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.dozer.converter.GroupDozerConverter;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;
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
		//groupDataService.removeUserFromGroup(groupId, userId);
		//final Group entity = groupDataService.getGroupDTO(groupId);
		//if(entity != null) {
		final User user = getUser(userId);
		final ProvisionUser pUser = new ProvisionUser(user);
		pUser.markGroupAsDeleted(groupId);
		//pUser.getGroups().add(group);
		provisionService.modifyUser(pUser);
	}

	@Override
	protected String getNotificationType() {
		return null;
	}
}
