package org.openiam.bpm.activiti.tasklistener;

import java.util.ArrayList;
import java.util.Collection;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.idm.srvc.user.dto.User;

public class AddCandidateUsersTaskListener implements TaskListener {

	public static final String CANDIDATE_USERS_VARIABLE = "candidateUsers";
	private static Logger log = Logger.getLogger(AddCandidateUsersTaskListener.class);
	
	@Override
	public void notify(DelegateTask delegateTask) {
		log.info("Add Candidate Users");
		final Object variable = delegateTask.getExecution().getVariable(CANDIDATE_USERS_VARIABLE);
		final Collection<User> candidateUsers = new ArrayList<User>();
		if(variable != null) {
			if((variable instanceof Collection<?>)) {
				for(final User candidate : (Collection<User>)variable) {
					if(candidate != null) {
						if(StringUtils.isNotBlank(candidate.getUserId())) {
							candidateUsers.add(candidate);
						}
					}
				}
			} else if(variable instanceof User) {
				if(StringUtils.isNotBlank(((User)variable).getUserId())) {
					candidateUsers.add(((User)variable));
				}
			}
		}
		
		if(CollectionUtils.isEmpty(candidateUsers)) {
			throw new ActivitiException(String.format("'%s' variable is empty", CANDIDATE_USERS_VARIABLE));
		}
		
		for(final User candidate : candidateUsers) {
			delegateTask.addCandidateUser(candidate.getUserId());
		}
	}
}
