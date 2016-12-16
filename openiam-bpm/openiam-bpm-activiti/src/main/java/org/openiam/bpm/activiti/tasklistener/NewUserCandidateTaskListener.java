package org.openiam.bpm.activiti.tasklistener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;

public class NewUserCandidateTaskListener extends AbstractCandidateTaskListener {
	
	public NewUserCandidateTaskListener() {
		super();
	}

	@Override
	protected void doNotify(DelegateTask delegateTask) {
		final NewUserProfileRequestModel profileModel = getObjectVariable(delegateTask.getExecution(), ActivitiConstants.REQUEST, NewUserProfileRequestModel.class);
		
		super.notifyCandidates(delegateTask, profileModel.getSupervisorIds());
	}

	@Override
	public String getTargetUserId(DelegateExecution execution) {
		return null;
	}

}
