package org.openiam.bpm.activiti.tasklistener;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.log4j.Logger;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;

public class ApproveOrRejectNewHireRequest implements TaskListener {

	public static final String IS_NEWHIRE_APPROVED = "IsNewHireApproved";
	public static final String ASSIGNEE = "Assignee";
	private static Logger log = Logger.getLogger(ApproveOrRejectNewHireRequest.class);
	
	@Autowired
	private TaskService taskService;
	
	public ApproveOrRejectNewHireRequest() {
		SpringContextProvider.autowire(this);
	}
	
	@Override
	public void notify(DelegateTask delegateTask) {
		log.info("Approving request");
		
		final Object assigneeObj = (delegateTask.getVariable(ASSIGNEE));
		final Object isNewHireAcceptedObj = delegateTask.getVariable(IS_NEWHIRE_APPROVED);
		if(assigneeObj == null || !(assigneeObj instanceof User)) {
			throw new ActivitiException(String.format("Variable '%s' not provied", ASSIGNEE));
		}
		
		if(isNewHireAcceptedObj == null || !(isNewHireAcceptedObj instanceof Boolean)) {
			throw new ActivitiException(String.format("Variable '%s' not provied", IS_NEWHIRE_APPROVED));
		}
		
		
		final User assignee = (User)assigneeObj;
		final Boolean isNewHireAccepted = (Boolean)isNewHireAcceptedObj;
		
		delegateTask.setAssignee(assignee.getUserId());
		delegateTask.setVariable("isHireAccepted", isNewHireAccepted);
	}

}
