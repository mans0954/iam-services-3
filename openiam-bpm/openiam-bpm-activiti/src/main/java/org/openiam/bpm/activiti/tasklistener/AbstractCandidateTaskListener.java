package org.openiam.bpm.activiti.tasklistener;

import java.util.Collection;
import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.activiti.delegate.core.ActivitiHelper;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractCandidateTaskListener extends AbstractActivitiJob {

	private static final Log LOG = LogFactory.getLog(AbstractCandidateTaskListener.class);
	

	@Autowired
	private ActivitiHelper activitiHelper;
	
	public AbstractCandidateTaskListener() {
		SpringContextProvider.autowire(this);
	}
	
	public void notify(DelegateTask delegateTask, final List<String> supervisorIds) {
		final DelegateExecution execution = delegateTask.getExecution();
		
		final IdmAuditLog idmAuditLog = createNewAuditLog(delegateTask);
        idmAuditLog.setAction(AuditAction.TASK_LISTENER.value());
		try {
			final String targetUserId = getTargetUserId(execution);
			final String taskOwner = getRequestorId(delegateTask.getExecution());
			final String taskDescription = getTaskDescription(execution);
			final String taskName = getTaskName(execution);
			
		
			final Collection<String> candidateUsersIds = activitiHelper.getCandidateUserIds(execution, targetUserId, supervisorIds);
			addUsersToProtectingResource(delegateTask, candidateUsersIds, null);
			
			//delegateTask.setVariableLocal(variableName, value)
			delegateTask.setVariableLocal(ActivitiConstants.CANDIDATE_USERS_IDS.getName(), candidateUsersIds);
			idmAuditLog.addAttributeAsJson(AuditAttributeName.CANDIDATE_USER_IDS, candidateUsersIds, customJacksonMapper);
		
			if(candidateUsersIds != null) {
				if(candidateUsersIds.size() == 1) {
					final String assigneeId = candidateUsersIds.iterator().next(); 
					delegateTask.setAssignee(assigneeId);
					delegateTask.setVariable(ActivitiConstants.ASSIGNEE_ID.getName(), assigneeId);
				} else {
					for(final String candidateId : candidateUsersIds) {
						delegateTask.addCandidateUser(candidateId);
					}
				}
			}
		
			if(StringUtils.isNotBlank(taskName)) {
				delegateTask.setName(taskName);
			} else {
				LOG.warn(String.format("No task name specified for %s", delegateTask.getId()));
			}
		
			if(StringUtils.isNotBlank(taskDescription)) {
				delegateTask.setDescription(taskDescription);
			}
		
			delegateTask.setOwner(taskOwner);
			
			idmAuditLog.succeed();
		} catch(Throwable e) {
			idmAuditLog.setException(e);
			idmAuditLog.fail();
			throw new RuntimeException(e);
		} finally {
			addAuditLogChild(execution, idmAuditLog);
		}
	}
}
