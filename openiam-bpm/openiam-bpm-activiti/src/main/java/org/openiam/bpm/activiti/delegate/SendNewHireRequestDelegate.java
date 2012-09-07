package org.openiam.bpm.activiti.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.apache.log4j.Logger;

public class SendNewHireRequestDelegate implements JavaDelegate {

	private static Logger log = Logger.getLogger(SendNewHireRequestDelegate.class);

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		log.info("Send New Hire Request");
	}

}
