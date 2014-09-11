package org.openiam.bpm.activiti.tasklistener;

import java.util.Collection;
import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.activiti.delegate.core.ActivitiHelper;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.constant.AuditSource;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractCandidateTaskListener extends AbstractActivitiJob {

	protected Logger LOG = Logger.getLogger(AbstractCandidateTaskListener.class);
	

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
			
			idmAuditLog.addAttributeAsJson(AuditAttributeName.CANDIDATE_USER_IDS, candidateUsersIds, customJacksonMapper);
		
			if(candidateUsersIds != null) {
				if(candidateUsersIds.size() == 1) {
					delegateTask.setAssignee(candidateUsersIds.iterator().next());
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
