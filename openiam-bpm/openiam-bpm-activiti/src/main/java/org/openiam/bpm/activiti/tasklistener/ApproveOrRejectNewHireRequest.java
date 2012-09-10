package org.openiam.bpm.activiti.tasklistener;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.log4j.Logger;
import org.openiam.bpm.activiti.util.ActivitiConstants;
import org.openiam.bpm.request.NewHireRequest;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;

public class ApproveOrRejectNewHireRequest implements TaskListener {

	private static Logger log = Logger.getLogger(ApproveOrRejectNewHireRequest.class);
	
	@Autowired
	private TaskService taskService;
	
	public ApproveOrRejectNewHireRequest() {
		SpringContextProvider.autowire(this);
	}
	
	@Override
	public void notify(DelegateTask delegateTask) {
		log.info("Approving request");
		
		final Object newHireObj = (delegateTask.getVariable(ActivitiConstants.NEW_HIRE_BPM_VAR));
		final Object isNewHireAcceptedObj = delegateTask.getVariable(ActivitiConstants.IS_NEW_HIRE_APPROVED);
		if(newHireObj == null || !(newHireObj instanceof NewHireRequest)) {
			throw new ActivitiException(String.format("Variable '%s' not provied", ActivitiConstants.NEW_HIRE_BPM_VAR));
		}
		
		if(isNewHireAcceptedObj == null || !(isNewHireAcceptedObj instanceof Boolean)) {
			throw new ActivitiException(String.format("Variable '%s' not provied", ActivitiConstants.IS_NEW_HIRE_APPROVED));
		}
		
		
		final NewHireRequest request = (NewHireRequest)newHireObj;
		final Boolean isNewHireAccepted = (Boolean)isNewHireAcceptedObj;
		
		//delegateTask.setAssignee(request.getCallerUserId());
		delegateTask.setVariable(ActivitiConstants.NEW_HIRE_BPM_VAR, isNewHireAccepted);
	}

}
