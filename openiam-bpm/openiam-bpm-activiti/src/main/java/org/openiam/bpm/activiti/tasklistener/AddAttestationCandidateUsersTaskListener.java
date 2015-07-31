package org.openiam.bpm.activiti.tasklistener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.openiam.bpm.util.ActivitiConstants;

public class AddAttestationCandidateUsersTaskListener extends AbstractCandidateTaskListener {
	
	public AddAttestationCandidateUsersTaskListener() {
		super();
	}

	@Override
	public void notify(DelegateTask delegateTask) {
		super.notify(delegateTask, null);
	}
	
	 @Override
     protected String getTargetUserId(final DelegateExecution execution) {
		 return getStringVariable(execution, ActivitiConstants.EMPLOYEE_ID);
     }
}
