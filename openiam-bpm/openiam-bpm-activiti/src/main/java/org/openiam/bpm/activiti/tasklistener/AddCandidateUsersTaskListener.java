package org.openiam.bpm.activiti.tasklistener;

import java.util.ArrayList;
import java.util.Collection;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.user.dto.User;

public class AddCandidateUsersTaskListener implements TaskListener {

	private static Logger log = Logger.getLogger(AddCandidateUsersTaskListener.class);
	
	@Override
	public void notify(DelegateTask delegateTask) {
		log.info("Add Candidate Users");
		final String taskOwner = StringUtils.trimToNull((String)delegateTask.getExecution().getVariable(ActivitiConstants.TASK_OWNER));
		final String taskDescription = (String)delegateTask.getExecution().getVariable(ActivitiConstants.TASK_DESCRIPTION);
		final String taskName = (String)delegateTask.getExecution().getVariable(ActivitiConstants.TASK_NAME);
		final Object candidateUserIdsObj = delegateTask.getExecution().getVariable(ActivitiConstants.CANDIDATE_USERS_IDS);
		final Collection<String> candidateUsersIds = new ArrayList<String>();
		if(candidateUserIdsObj != null) {
			if((candidateUserIdsObj instanceof Collection<?>)) {
				for(final String candidateId : (Collection<String>)candidateUserIdsObj) {
					if(candidateId != null) {
						candidateUsersIds.add(candidateId);
					}
				}
			} else if(candidateUserIdsObj instanceof String) {
				if(StringUtils.isNotBlank(((String)candidateUserIdsObj))) {
					candidateUsersIds.add(((String)candidateUserIdsObj));
				}
			}
		}
		
		/*
		if(CollectionUtils.isEmpty(candidateUsersIds)) {
			throw new ActivitiException(String.format("'%s' variable is empty", ActivitiConstants.CANDIDATE_USERS_IDS));
		}
		*/
		
		for(final String candidateId : candidateUsersIds) {
			delegateTask.addCandidateUser(candidateId);
		}
		
		if(StringUtils.isNotBlank(taskName)) {
			delegateTask.setName(taskName);
		} else {
			log.warn(String.format("No task name specified for %s", delegateTask.getId()));
		}
		
		if(StringUtils.isNotBlank(taskDescription)) {
			delegateTask.setDescription(taskDescription);
		}
		
		delegateTask.setOwner(taskOwner);
	}
}
