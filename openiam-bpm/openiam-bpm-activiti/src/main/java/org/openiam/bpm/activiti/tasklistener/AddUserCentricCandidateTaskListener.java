package org.openiam.bpm.activiti.tasklistener;

import java.util.Collection;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.bpm.activiti.delegate.core.ActivitiHelper;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;

public class AddUserCentricCandidateTaskListener extends AbstractCandidateTaskListener {
	
	public AddUserCentricCandidateTaskListener() {
		super();
	}

	@Override
	public void notify(DelegateTask delegateTask) {
		super.notify(delegateTask, null);
	}

	@Override
	public String getTargetUserId(final DelegateExecution execution) {
		return getStringVariable(execution, ActivitiConstants.MEMBER_ASSOCIATION_ID);
	}

}
