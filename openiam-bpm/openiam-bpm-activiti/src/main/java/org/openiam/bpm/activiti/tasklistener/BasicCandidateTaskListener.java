package org.openiam.bpm.activiti.tasklistener;

import org.activiti.engine.delegate.DelegateTask;

public class BasicCandidateTaskListener extends AbstractCandidateTaskListener {
	
	public BasicCandidateTaskListener() {
		super();
	}

	@Override
	protected void doNotify(DelegateTask delegateTask) {
		super.doNotify(delegateTask);
	}
}
