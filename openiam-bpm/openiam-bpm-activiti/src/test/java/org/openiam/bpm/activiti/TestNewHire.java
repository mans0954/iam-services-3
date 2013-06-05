package org.openiam.bpm.activiti;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

@ContextConfiguration(locations={"classpath:test-bpm-context.xml","classpath:applicationContext.xml", "classpath:idmservice-Context.xml"})
public class TestNewHire extends AbstractTestNGSpringContextTests {
	
	/*
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
		  variables.put(ActivitiConstants.CANDIDATE_USERS, requestApprovers);
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
			  localVariables.put(ActivitiConstants.CANDIDATE_USERS, user);
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
	  */
}
