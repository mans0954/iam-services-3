package org.openiam.bpm.activiti;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openiam.bpm.activiti.delegate.SendNewHireRequestDelegate;
import org.openiam.bpm.activiti.tasklistener.AddCandidateUsersTaskListener;
import org.openiam.bpm.activiti.tasklistener.ApproveOrRejectNewHireRequest;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;
import org.openiam.idm.srvc.prov.request.dto.RequestApprover;
import org.openiam.idm.srvc.user.dto.Supervisor;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:test-bpm-context.xml","classpath:applicationContext.xml", "classpath:idmservice-Context.xml"})
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
	  
	  @Autowired
	  private UserDAO userDao;
	  
	  private static final String USER_ID = "4028b8813979de7a013979e1b74e0000";
	  
	  @Test
	  @Deployment(resources={"bpm/test.newhire.bpmn20.xml"})
	  @Transactional
	  public void testNewHire() {
		  
		  final User requestor = userDao.findById(USER_ID);
		  if(requestor == null) {
			  throw new RuntimeException(String.format("User with ID: %s does not exist,  can't test", USER_ID));
		  }
		  
		  final ProvisionUser provisionUser = createNewUser(requestor.getUserId());
		  final Set<User> requestApprovers = createApproverList(requestor.getUserId());
		  
		  final HashMap<String, Object> variables = new HashMap<String, Object>();
		  variables.put(AddCandidateUsersTaskListener.CANDIDATE_USERS_VARIABLE, requestApprovers);
		  //variables.put(SendNewHireRequestDelegate.REQUESTOR, requestor);
		  //variables.put(SendNewHireRequestDelegate.REQUESTING_FOR, provisionUser);
		  //variables.put(SendNewHireRequestDelegate.APPROVER_LIST, requestApprovers);
		  final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("newHireWithApproval", variables);
		  
		  List<Task> taskList = taskService.createTaskQuery().taskCandidateUser("foobar").list();
		  log.info(taskList);
		  
		  for(final Task task : taskList) {
			  log.info("Claiming...");
			  final User user = new User("foobar");
			  taskService.claim(task.getId(), user.getUserId());
			  final HashMap<String, Object> localVariables = new HashMap<String, Object>();
			  localVariables.put(ApproveOrRejectNewHireRequest.ASSIGNEE, user);
			  taskService.complete(task.getId(), localVariables);
		  }
		  
		  taskList = taskService.createTaskQuery().taskAssignee("foobar").list();
		  log.info("second task list: " + taskList);
		  for(final Task task : taskList) {
			  taskService.complete(task.getId());
		  }
	  }
	  
	  private ProvisionUser createNewUser(final String createdBy) {
		  final ProvisionUser user = new ProvisionUser();
		  user.setAddress1(rs());
		  user.setAlternateContactId(rs());
		  user.setAreaCd(rs());
		  user.setBirthdate(new Date());
		  user.setBldgNum(rs());
		  user.setCity(rs());
		  user.setClassification(rs());
		  user.setCompanyId(rs());
		  user.setCompanyOwnerId(rs());
		  user.setCostCenter(rs());
		  user.setCountry(rs());
		  user.setCountryCd(rs());
		  user.setCreateDate(new Date());
		  user.setCreatedBy(createdBy);
		  return user;
	  }
	  
	  private  Set<User> createApproverList(final String createdBy) {
		  final Set<User> requestApproverSet = new HashSet<User>();
          final User user = new User();
          user.setUserId("foobar");

          requestApproverSet.add(user);
          return requestApproverSet;
	  }
	  
	  private String rs() {
		  return RandomStringUtils.randomAlphabetic(2);
	  }
}
