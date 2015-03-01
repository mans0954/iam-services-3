package org.openiam.bpm.activiti.delegate.group.attestation;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;

public class GroupAttestationWorkDelegate extends AbstractActivitiJob {
	private static Logger LOG = Logger.getLogger(GroupAttestationWorkDelegate.class);

	public GroupAttestationWorkDelegate() {
		super();
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final StopWatch sw = new StopWatch();
		sw.start();
		
		final String groupId = getStringVariable(execution, ActivitiConstants.EMPLOYEE_ID);
		
		sw.stop();
		LOG.info(String.format("Took %s ms to send process request for group %s", sw.getTime(), groupId));
	}
}
