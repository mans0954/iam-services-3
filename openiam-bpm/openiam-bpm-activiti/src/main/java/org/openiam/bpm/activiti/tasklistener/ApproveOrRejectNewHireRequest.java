package org.openiam.bpm.activiti.tasklistener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.log4j.Logger;

public class ApproveOrRejectNewHireRequest implements TaskListener {

	private static Logger log = Logger.getLogger(ApproveOrRejectNewHireRequest.class);
	
	@Override
	public void notify(DelegateTask delegateTask) {
		log.info("Approve or Reject New Hire Request");
	}

}
