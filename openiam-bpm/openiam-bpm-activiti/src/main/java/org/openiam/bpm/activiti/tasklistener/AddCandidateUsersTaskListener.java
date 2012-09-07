package org.openiam.bpm.activiti.tasklistener;

import java.util.ArrayList;
import java.util.Collection;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class AddCandidateUsersTaskListener implements TaskListener {

	public static final String CANDIDATE_USERS_VARIABLE = "candidateUsers";
	private static Logger log = Logger.getLogger(AddCandidateUsersTaskListener.class);
	
	@Override
	public void notify(DelegateTask delegateTask) {
		log.info("Add Candidate Users");
		final Object variable = delegateTask.getExecution().getVariable(CANDIDATE_USERS_VARIABLE);
		final Collection<String> candidateUsers = new ArrayList<String>();
		if(variable != null) {
			if((variable instanceof Collection<?>)) {
				for(final String candidate : (Collection<String>)variable) {
					if(StringUtils.isNotBlank(candidate)) {
						candidateUsers.add(candidate);
					}
				}
			} else if(variable instanceof String) {
				if(StringUtils.isNotBlank((String)variable)) {
					candidateUsers.add((String)variable);
				}
			}
		}
		
		if(CollectionUtils.isEmpty(candidateUsers)) {
			throw new ActivitiException(String.format("'%s' variable is empty", CANDIDATE_USERS_VARIABLE));
		}
		
		for(final String candidate : candidateUsers) {
			delegateTask.addCandidateUser(candidate);
		}
	}
}
