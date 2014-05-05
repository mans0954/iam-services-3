package org.openiam.bpm.activiti.tasklistener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.openiam.bpm.util.ActivitiConstants;

public class AddEntityCentricCandidateTaskListener extends AbstractCandidateTaskListener {

	public AddEntityCentricCandidateTaskListener() {
		super();
	}
	
	@Override
	public void notify(DelegateTask delegateTask) {
		super.notify(delegateTask, null);
	}

	@Override
	public String getTargetUserId(final DelegateExecution execution) {
		return null;
	}
}
