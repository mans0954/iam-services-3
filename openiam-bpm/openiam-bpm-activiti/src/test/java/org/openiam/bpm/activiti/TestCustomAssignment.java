package org.openiam.bpm.activiti;

import java.util.List;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

@ContextConfiguration("classpath:test-bpm-context.xml")
public class TestCustomAssignment extends AbstractTestNGSpringContextTests {

	private static Logger log = Logger.getLogger(TestCustomAssignment.class);
	
	  @Autowired
	  private RuntimeService runtimeService;
	  
	  @Autowired
	  private TaskService taskService;
	  
	  @Test
	  @Deployment(resources={"bpm/test.customassign.bpmn20.xml"})
	  public void testCustomAssignment() {

		  final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("customAssignment");
		  log.info("Process ID: " + processInstance.getId());
		  log.info("Process Business Key: " + processInstance.getBusinessKey());
		  
		  
		  final List<Task> taskList = taskService.createTaskQuery().taskAssignee("testAsignee").list();
		  for(final Task task : taskList) {
			  taskService.complete(task.getId());
			  log.info(String.format("Task assigned to %s.  task id: %s.  task description: %s", task.getAssignee(), task.getId(), task.getName()));
		  }
		  
		  Assert.assertTrue(taskService.createTaskQuery().taskAssignee("testAsignee").list().size() == 0);
		  
		  final List<Task> groupAssignee1Tasks = taskService.createTaskQuery().taskCandidateUser("groupAsignee1").list();
		  final List<Task> groupAssignee2Tasks = taskService.createTaskQuery().taskCandidateUser("groupAsignee2").list();
		  log.info("NUmber of tasks for groupAsignee1: " + groupAssignee1Tasks.size());
		  log.info("NUmber of tasks for groupAsignee2: " + groupAssignee2Tasks.size());
		  
		  for(final Task task : groupAssignee1Tasks) {
			  taskService.claim(task.getId(), "groupAsignee1");
		  }
		  
		  for(final Task task : groupAssignee2Tasks) {
			  taskService.claim(task.getId(), "groupAsignee1");
		  }
		  
		  log.info("Number of tasks assigned to groupAsignee1: " + taskService.createTaskQuery().taskAssignee("groupAsignee1").list());
		  log.info("Number of tasks assigned to groupAsignee2: " + taskService.createTaskQuery().taskAssignee("groupAsignee2").list());
		  
		  taskService.complete(groupAssignee1Tasks.get(0).getId());
		  log.info("Number of tasks assigned to groupAsignee1: " + taskService.createTaskQuery().taskAssignee("groupAsignee1").list());
	  }
}
