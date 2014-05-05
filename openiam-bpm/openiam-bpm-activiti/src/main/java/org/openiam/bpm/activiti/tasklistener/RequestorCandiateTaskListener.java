package org.openiam.bpm.activiti.tasklistener;

import org.activiti.engine.delegate.DelegateTask;
import org.openiam.bpm.activiti.delegate.entitlements.AbstractEntitlementsDelegate;
import org.openiam.bpm.util.ActivitiConstants;

public class RequestorCandiateTaskListener extends AbstractCandidateTaskListener {

	public RequestorCandiateTaskListener() {
		super();
	}
	
	@Override
	public void notify(DelegateTask delegateTask) {
		super.notify(delegateTask, null);
	}
	
	@Override
	protected ActivitiConstants getTargetVariable() {
		return ActivitiConstants.REQUESTOR;
	}
}
