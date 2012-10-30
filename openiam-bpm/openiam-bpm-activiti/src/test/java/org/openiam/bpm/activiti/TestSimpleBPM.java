package org.openiam.bpm.activiti;

import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@ContextConfiguration("classpath:test-bpm-context.xml")
public class TestSimpleBPM extends AbstractTestNGSpringContextTests {

	  @Autowired
	  private RuntimeService runtimeService;
	  
	  @Autowired
	  private TaskService taskService;

	  @Autowired
	  private HistoryService historyService;
	  
	  @Test
	  @Deployment(resources={"bpm/test.simple.bpmn20.xml"})
	  public void testNewHire() {
			// Start a process instance
			final String procId = runtimeService.startProcessInstanceByKey("financialReport").getId();
		
			// Get the first task
			List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("accountancy").list();
			for (final Task task : tasks) {
			  System.out.println("Following task is available for accountancy group: " + task.getName());
			  
			  // claim it
			  taskService.claim(task.getId(), "fozzie");
			}
			
			// Verify Fozzie can now retrieve the task
			tasks = taskService.createTaskQuery().taskAssignee("fozzie").list();
			for (Task task : tasks) {
			  System.out.println("Task for fozzie: " + task.getName());
			  
			  // Complete the task
			  taskService.complete(task.getId());
			}
			
			System.out.println("Number of tasks for fozzie: " 
			        + taskService.createTaskQuery().taskAssignee("fozzie").count());
			
			// Retrieve and claim the second task
			tasks = taskService.createTaskQuery().taskCandidateGroup("management").list();
			for (Task task : tasks) {
			  System.out.println("Following task is available for accountancy group: " + task.getName());
			  taskService.claim(task.getId(), "kermit");
			}
			
			// Completing the second task ends the process
			for (Task task : tasks) {
			  taskService.complete(task.getId());
			}
			
			// verify that the process is actually finished
			HistoricProcessInstance historicProcessInstance = 
			  historyService.createHistoricProcessInstanceQuery().processInstanceId(procId).singleResult();
			System.out.println("Process instance end time: " + historicProcessInstance.getEndTime());
	  }
}
