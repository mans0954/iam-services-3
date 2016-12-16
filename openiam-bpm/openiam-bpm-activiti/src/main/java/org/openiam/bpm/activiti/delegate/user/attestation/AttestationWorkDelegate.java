package org.openiam.bpm.activiti.delegate.user.attestation;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;

public class AttestationWorkDelegate extends AbstractActivitiJob {
	private static final Log LOG = LogFactory.getLog(AttestationWorkDelegate.class);
	
	public AttestationWorkDelegate() {
		super();
	}

	@Override
	protected void doExecute(DelegateExecution execution) throws Exception {
		final StopWatch sw = new StopWatch();
		sw.start();
		
		final String employeeId = getStringVariable(execution, ActivitiConstants.EMPLOYEE_ID);
		
		sw.stop();
		LOG.info(String.format("Took %s ms to send process request for user %s", sw.getTime(), employeeId));
	}
}
