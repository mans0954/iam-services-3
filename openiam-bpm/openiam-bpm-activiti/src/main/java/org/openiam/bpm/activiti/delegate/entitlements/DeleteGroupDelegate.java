package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
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
		final String groupId = getStringVariable(execution, ActivitiConstants.GROUP_ID);
		final Response wsReponse = groupDataService.deleteGroup(groupId);
		//TODO:  validate
	}
}
