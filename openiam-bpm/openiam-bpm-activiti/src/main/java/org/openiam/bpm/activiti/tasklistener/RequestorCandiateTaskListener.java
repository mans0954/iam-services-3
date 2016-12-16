package org.openiam.bpm.activiti.tasklistener;

import org.activiti.engine.delegate.DelegateTask;
import org.openiam.bpm.util.ActivitiConstants;

public class RequestorCandiateTaskListener extends AbstractCandidateTaskListener {

	public RequestorCandiateTaskListener() {
		super();
	}
	
	@Override
	protected void doNotify(DelegateTask delegateTask) {
		super.doNotify(delegateTask);
	}
	
	@Override
	protected ActivitiConstants getTargetVariable() {
		return ActivitiConstants.REQUESTOR;
	}
}
