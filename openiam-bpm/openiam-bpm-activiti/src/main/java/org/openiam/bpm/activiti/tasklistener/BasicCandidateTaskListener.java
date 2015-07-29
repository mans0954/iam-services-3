package org.openiam.bpm.activiti.tasklistener;

import org.activiti.engine.delegate.DelegateTask;

public class BasicCandidateTaskListener extends AbstractCandidateTaskListener {
	
	public BasicCandidateTaskListener() {
		super();
	}

	@Override
	public void notify(DelegateTask delegateTask) {
		super.notify(delegateTask, null);
	}
}
