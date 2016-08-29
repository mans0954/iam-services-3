package org.openiam.bpm.activiti.notifier;

import java.util.List;

import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.request.NotificationParam;
import org.openiam.base.request.NotificationRequest;
import org.openiam.srvc.common.MailService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("taskEmailNotifier")
public class TaskEmailNotifier {
	
	private static final Log LOG = LogFactory.getLog(TaskEmailNotifier.class);
	
	@Autowired
	@Qualifier("activitiTaskService")
	private TaskService taskService;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	protected MailService mailService;

	@Transactional
	public void sweep() {
		
		List<Task> taskList = null;
		int firstResult = 0;
		int maxResults = 1000;
		
		taskList = taskService.createTaskQuery().listPage(firstResult, maxResults);
		sendAssigneeReminders(taskList);
		while(CollectionUtils.isNotEmpty(taskList)) {
			firstResult += maxResults;
			taskList = taskService.createTaskQuery().listPage(firstResult, maxResults);
			sendAssigneeReminders(taskList);
		}
	}
	
	private void sendAssigneeReminders(final List<Task> taskList) {
		if(CollectionUtils.isNotEmpty(taskList)) {
			for(final Task task : taskList) {
				final TaskEntity entity = (TaskEntity)task;
				if(StringUtils.isNotBlank(entity.getAssignee())) {
					LOG.info(entity.getAssignee());
					sendAssigneeReminder(entity.getAssignee(), entity);
				}
			}
		}
	}
	
	private void sendAssigneeReminder(final String userId, final TaskEntity task) {
		final UserEntity user = userDAO.findById(userId);
		if(user != null) {
			final NotificationRequest request = new NotificationRequest();
	        request.setUserId(user.getId());
	        request.setNotificationType("TASK_ASSIGNEE_REMINDER");
	        request.getParamList().add(new NotificationParam("TASK_NAME", task.getName()));
	        request.getParamList().add(new NotificationParam("TASK_DESCRIPTION", task.getDescription()));
	        request.getParamList().add(new NotificationParam("TARGET_USER", user.getDisplayName()));
	        mailService.sendNotification(request);
		}
	}
}
