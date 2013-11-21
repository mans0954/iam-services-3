package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.springframework.beans.factory.annotation.Autowired;

public class SaveGroupDelegate extends AbstractActivitiJob {

	@Autowired
	private GroupDataWebService groupDataService;
	
	public SaveGroupDelegate() {
		
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final Group group = getObjectVariable(execution, ActivitiConstants.GROUP, Group.class);
		final Response wsResponse = groupDataService.saveGroup(group, getRequestorId(execution));
		//TODO:  validate
	}
}
