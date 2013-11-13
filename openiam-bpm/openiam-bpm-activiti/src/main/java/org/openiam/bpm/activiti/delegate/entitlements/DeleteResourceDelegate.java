package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;

public class DeleteResourceDelegate extends AbstractActivitiJob {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String resourceId = getStringVariable(execution, ActivitiConstants.RESOURCE_ID);
	}

}
