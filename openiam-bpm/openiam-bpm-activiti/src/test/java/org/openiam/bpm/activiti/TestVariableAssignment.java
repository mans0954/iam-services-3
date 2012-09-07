package org.openiam.bpm.activiti;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-bpm-context.xml")
public class TestVariableAssignment {
	private static Logger log = Logger.getLogger(TestVariableAssignment.class);
	
	  @Autowired
	  private RuntimeService runtimeService;
	  
	  @Autowired
	  private TaskService taskService;
	  
	  @Autowired
	  @Rule
	  public ActivitiRule activitiSpringRule;
	  
	  @Autowired
	  private RepositoryService repositoryService;
	  
	  @Autowired
	  private ManagementService managementService;
	  
	  @Test
	  @Deployment(resources={"bpm/test.variableassign.bpmn20.xml"})
	  public void testVariableAssignment() {

		  final Map<String, Object> variableMap = new HashMap<String, Object>();
		  final Collection<String> users = new LinkedList<String>();
		  users.add("variableUser1");
		  users.add("variableUser2");
		  variableMap.put("candidateUsers", users);
		  
		  final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("variableAssignment", variableMap);
		  
		  log.info("Process ID: " + processInstance.getId());
		  log.info("Process Business Key: " + processInstance.getBusinessKey());
		  
		  final List<Task> taskList = taskService.createTaskQuery().taskCandidateUser("variableUser1").list();
		  for(final Task task : taskList) {
			  taskService.complete(task.getId());
			  log.info(String.format("variableUser1: Task assigned to %s.  task id: %s.  task description: %s", task.getAssignee(), task.getId(), task.getName()));
		  }
		  Assert.assertTrue(taskService.createTaskQuery().taskAssignee("variableUser").list().size() == 0);

		  final List<Task> groupAssignee1Tasks = taskService.createTaskQuery().taskCandidateUser("variableUser1").list();
		  final List<Task> groupAssignee2Tasks = taskService.createTaskQuery().taskCandidateUser("variableUser2").list();
		  log.info("NUmber of tasks for variableUser1: " + groupAssignee1Tasks.size());
		  log.info("NUmber of tasks for variableUser2: " + groupAssignee2Tasks.size());
		  
		  for(final Task task : groupAssignee1Tasks) {
			  taskService.claim(task.getId(), "variableUser1");
		  }
		  
		  for(final Task task : groupAssignee2Tasks) {
			  taskService.claim(task.getId(), "variableUser2");
		  }
		  
		  log.info("Number of tasks assigned to variableUser1: " + taskService.createTaskQuery().taskAssignee("variableUser1").list());
		  log.info("Number of tasks assigned to variableUser2: " + taskService.createTaskQuery().taskAssignee("variableUser2").list());
		  
		  taskService.complete(groupAssignee1Tasks.get(0).getId());
		  log.info("Number of tasks assigned to variableUser1: " + taskService.createTaskQuery().taskAssignee("variableUser1").list());

	  }
}
