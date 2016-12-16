package org.openiam.bpm.activiti.tasklistener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.openiam.bpm.util.ActivitiConstants;

public class AddEditUserCandidateTaskListener extends AbstractCandidateTaskListener {

	public AddEditUserCandidateTaskListener() {
		super();
	}
	
	@Override
	protected void doNotify(DelegateTask delegateTask) {
		super.doNotify(delegateTask);
	}

	@Override
	public String getTargetUserId(final DelegateExecution execution) {
		return getStringVariable(execution, ActivitiConstants.ASSOCIATION_ID);
	}
}
