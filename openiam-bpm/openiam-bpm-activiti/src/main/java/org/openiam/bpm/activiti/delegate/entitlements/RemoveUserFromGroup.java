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

public class RemoveUserFromGroup extends AbstractEntitlementsDelegate {
	
	@Autowired
	private GroupDataService groupDataService;
	
	@Autowired
	@Qualifier("defaultProvision")
	private ProvisionService provisionService;
	
	@Autowired
	private UserDataService userDataService;
	
	@Autowired
	private GroupDozerConverter groupDozerConverter;
	
	public RemoveUserFromGroup() {
		super();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String groupId = (String)execution.getVariable(ActivitiConstants.ASSOCIATION_ID);
		final String userId = (String)execution.getVariable(ActivitiConstants.MEMBER_ASSOCIATION_ID);
		//groupDataService.removeUserFromGroup(groupId, userId);
		final GroupEntity entity = groupDataService.getGroup(groupId);
		if(entity != null) {
			final Group group = groupDozerConverter.convertToDTO(entity, false);
			group.setOperation(AttributeOperationEnum.DELETE);
			final User user = userDataService.getUserDto(userId);
			final ProvisionUser pUser = new ProvisionUser(user);
			pUser.getGroups().add(group);
			provisionService.modifyUser(pUser);
		}
	}

	@Override
	protected String getNotificationType() {
		return null;
	}
}
