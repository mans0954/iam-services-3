package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.springframework.beans.factory.annotation.Autowired;

public class DeleteGroupDelegate extends AbstractActivitiJob {

	@Autowired
	private GroupDataWebService groupDataService;
	
	public DeleteGroupDelegate() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Response wsResponse = null;
		final Group group = getObjectVariable(execution, ActivitiConstants.GROUP, Group.class);
		if(group != null) {
			wsResponse = groupDataService.deleteGroup(group.getId());
		}
		//TODO:  validate
	}
}
