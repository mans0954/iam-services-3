package org.openiam.bpm.activiti;

import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openiam.bpm.activiti.tasklistener.AddCandidateUsersTaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-bpm-context.xml")
public class TestNewHire {
	
	private static Logger log = Logger.getLogger(TestNewHire.class);
	
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
	  @Deployment(resources={"bpm/test.newhire.bpmn20.xml"})
	  public void testNewHire() {
		  final HashMap<String, Object> variables = new HashMap<String, Object>();
		  variables.put(AddCandidateUsersTaskListener.CANDIDATE_USERS_VARIABLE, "foobarUser");
		  final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("newHireWithApproval", variables);
		  
		  final List<Task> taskList = taskService.createTaskQuery().taskCandidateUser("foobarUser").list();
		  log.info(taskList);
		  
		  for(final Task task : taskList) {
			  log.info("Claiming...");
			  taskService.claim(task.getId(), "foobarUser");
			  taskService.complete(task.getId());
		  }
		  
	  }
}
