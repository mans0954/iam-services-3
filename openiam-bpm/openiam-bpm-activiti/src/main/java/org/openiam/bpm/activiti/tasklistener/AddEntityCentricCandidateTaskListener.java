package org.openiam.bpm.activiti.tasklistener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.openiam.bpm.util.ActivitiConstants;

public class AddEntityCentricCandidateTaskListener extends AbstractCandidateTaskListener {

	public AddEntityCentricCandidateTaskListener() {
		super();
	}
	
	@Override
	protected void doNotify(DelegateTask delegateTask) {
		super.doNotify(delegateTask);
	}

	@Override
	public String getTargetUserId(final DelegateExecution execution) {
		return null;
	}
}
