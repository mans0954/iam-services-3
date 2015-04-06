package org.openiam.service.integration.activiti;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.common.lang3.StringUtils;
import org.openiam.activiti.model.dto.TaskSearchBean;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.PatternMatchMode;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.dto.URIPatternParameter;
import org.openiam.am.srvc.searchbeans.ContentProviderSearchBean;
import org.openiam.am.srvc.ws.AuthResourceAttributeWebService;
import org.openiam.base.Tuple;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.SearchParam;
import org.openiam.bpm.activiti.ActivitiService;
import org.openiam.bpm.activiti.delegate.user.edit.displaymapper.EditUserDisplayMapperDelegate;
import org.openiam.bpm.request.ActivitiRequestDecision;
import org.openiam.bpm.request.HistorySearchBean;
import org.openiam.bpm.response.ActivitiHistoricDetail;
import org.openiam.bpm.response.ActivitiUserField;
import org.openiam.bpm.response.TaskHistoryWrapper;
import org.openiam.bpm.response.TaskListWrapper;
import org.openiam.bpm.response.TaskWrapper;
import org.openiam.bpm.util.ActivitiRequestType;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.ws.LoginDataWebService;
import org.openiam.idm.srvc.auth.ws.LoginResponse;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.service.integration.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ActivitiServiceTest extends AbstractServiceTest {
	
	@Autowired
	@Qualifier("activitiClient")
	private ActivitiService activitiClient;
	
	@Autowired
	@Qualifier("loginServiceClient")
	private LoginDataWebService loginServiceClient;
	
	private User requestor = null;
	
	@BeforeClass
	protected void _setUp() throws Exception {
		 requestor = super.createUser();
		 Assert.assertNotNull(requestor);
	}
	
	@AfterClass
    public void _tearDown() throws Exception {
		if(requestor != null) {
    		userServiceClient.removeUser(requestor.getId());
    	}
    }
	
	private User confirmUser(final NewUserProfileRequestModel request) throws InterruptedException {
		/* confirm user */
		final String emailAddress = request.getEmails().get(0).getEmailAddress();
		final UserSearchBean userSearchBean = new UserSearchBean();
		userSearchBean.setEmailAddressMatchToken(new SearchParam(emailAddress, MatchType.EXACT));
		final List<User> userList = userServiceClient.findBeans(userSearchBean, 0, 1);
		Assert.assertTrue(CollectionUtils.isNotEmpty(userList), String.format("Could not find use with email '%s'", emailAddress));
		final User user = userList.get(0);
		//blocked by IDMAPPS-2742
		
		return user;
	}
	
	private User sendAndConfirmRequestWithNoApprover(final NewUserProfileRequestModel request) throws InterruptedException {
		final SaveTemplateProfileResponse templateResponse = activitiClient.initiateNewHireRequest(request);
		Assert.assertTrue(templateResponse.isSuccess());
		Thread.sleep(5000L);
		return confirmUser(request);
	}
	
	private User sendAndConfirmRequestWithApprover(final NewUserProfileRequestModel request) throws InterruptedException {
		final SaveTemplateProfileResponse templateResponse = activitiClient.initiateNewHireRequest(request);
		Assert.assertTrue(templateResponse.isSuccess());
		Thread.sleep(5000L);
		
		final TaskSearchBean searchBean = new TaskSearchBean();
		searchBean.setProcessDefinitionId(templateResponse.getProcessDefinitionId());
		searchBean.setAssigneeId(requestor.getId());
		List<TaskWrapper> wrappers = activitiClient.findTasks(searchBean, 0, Integer.MAX_VALUE);
		Assert.assertTrue(CollectionUtils.isNotEmpty(wrappers));
		
		final ActivitiRequestDecision decision = new ActivitiRequestDecision();
		decision.setAccepted(true);
		decision.setComment(getRandomName());
		decision.setTaskId(wrappers.get(0).getId());
		decision.setRequestorUserId(requestor.getId());
		final Response response = activitiClient.makeDecision(decision);
		Assert.assertTrue(response.isSuccess());
		Thread.sleep(5000L);
		
		wrappers = activitiClient.findTasks(searchBean, 0, Integer.MAX_VALUE);
		Assert.assertTrue(CollectionUtils.isEmpty(wrappers));
		Thread.sleep(10000L);
		
		return confirmUser(request);
	}
	
	@Test
	public void testSelfRegistrationAccept() throws InterruptedException {
		final NewUserProfileRequestModel request = createNewHireRequest(ActivitiRequestType.SELF_REGISTRATION);
		sendAndConfirmRequestWithApprover(request);
	}
	
	@Test
	public void testGetHistoryDetail() throws InterruptedException {
		final NewUserProfileRequestModel request = createNewHireRequest(ActivitiRequestType.SELF_REGISTRATION);
		final User user = sendAndConfirmRequestWithApprover(request);
		
		final HistorySearchBean searchBean = new HistorySearchBean();
		searchBean.setAssigneeId(requestor.getId());
		final List<TaskWrapper> beans = activitiClient.getHistory(searchBean, 0, 1);
		Assert.assertTrue(CollectionUtils.isNotEmpty(beans));
		final TaskWrapper wrapper = beans.get(0);

		final String executionId = wrapper.getExecutionId();
		final List<TaskHistoryWrapper> wrappers = activitiClient.getHistoryForInstance(executionId);
		Assert.assertTrue(CollectionUtils.isNotEmpty(wrappers));
	
		//boolean hasListUsers = false;
		boolean hasSingleUser = false;
		for(final TaskHistoryWrapper historyWrapper : wrappers) {
			final ActivitiHistoricDetail detail = historyWrapper.getVariableDetails();
			Assert.assertNotNull(detail);
			
			/*
			if(CollectionUtils.isNotEmpty(detail.getCandidateUserIds()) && CollectionUtils.isNotEmpty(detail.getCandidateUsers())) {
				hasListUsers = true;
			}
			
			if(CollectionUtils.isNotEmpty(detail.getCustomApproverIds()) && CollectionUtils.isNotEmpty(detail.getCustomApprovers())) {
				hasListUsers = true;
			}
			*/
			
			if(StringUtils.isNotBlank(detail.getNewUserId()) && detail.getNewUser() != null) {
				hasSingleUser = true;
			}
			
			if(StringUtils.isNotBlank(detail.getRequestor()) && detail.getRequestorUser() != null) {
				hasSingleUser = true;
			}
			
			if(StringUtils.isNotBlank(detail.getExecutorId()) && detail.getExecutor() != null) {
				hasSingleUser = true;
			}
			
			if(StringUtils.isNotBlank(detail.getAssigneeUserId()) && detail.getAssigneeUser() != null) {
				hasSingleUser = true;
			}
		}
		
		//Assert.assertTrue(hasListUsers);
		Assert.assertTrue(hasSingleUser);
	}
	
	@Test
	public void testNewHireWithApprover() throws InterruptedException {
		final NewUserProfileRequestModel request = createNewHireRequest(ActivitiRequestType.NEW_HIRE_WITH_APPROVAL);
		sendAndConfirmRequestWithApprover(request);
	}
	
	@Test
	public void testNewHireWithNoApprover() throws InterruptedException {
		final NewUserProfileRequestModel request = createNewHireRequest(ActivitiRequestType.NEW_HIRE_NO_APPROVAL);
		sendAndConfirmRequestWithNoApprover(request);
	}
	
	@Test
	public void testEditUserWorkflow() throws InterruptedException {
		final NewUserProfileRequestModel request = createNewHireRequest(ActivitiRequestType.NEW_HIRE_NO_APPROVAL);
		final User user = sendAndConfirmRequestWithNoApprover(request);
		final UserProfileRequestModel editUserRequest = createEditUserRequest(user);
		final Response response = activitiClient.initiateEditUserWorkflow(editUserRequest);
		Assert.assertTrue(response.isSuccess());
		//TODO: validate user got edited
	}
	
	private UserProfileRequestModel createEditUserRequest(final User user) {
		final UserProfileRequestModel request = new UserProfileRequestModel();
		//TODO: create reqeust
		return request;
	}
	
	private NewUserProfileRequestModel createNewHireRequest(final ActivitiRequestType type) {
		final NewUserProfileRequestModel request = new NewUserProfileRequestModel();
		request.setActivitiRequestType(type);
		
		final List<Address> addresses = new LinkedList<Address>();
		for(int i = 0; i < 3; i++) {
			final Address address = new Address();
			address.setAddress1(getRandomName());
			address.setAddress2(getRandomName());
			address.setBldgNumber(i + "");
			address.setCity("a" + i);
			address.setState("b" + i);
			address.setCountry("c" + i);
			address.setDescription(getRandomName());
			address.setIsActive(true);
			address.setMdTypeId(getMetadataTypesByGrouping(MetadataTypeGrouping.ADDRESS).get(0).getId());
			address.setName(getRandomName());
			address.setPostalCd("p" + i);
			address.setRequestorUserId(requestor.getId());
			address.setStreetDirection("abc");
			address.setSuite("suite");
			addresses.add(address);
		}
		request.setAddresses(addresses);
		
		final List<EmailAddress> emails = new LinkedList<EmailAddress>();
		for(int i = 0; i < 3; i++) {
			final EmailAddress email = new EmailAddress();
			email.setDescription(getRandomName());
			email.setEmailAddress(String.format("%s@%s.com", getRandomName(), getRandomName(3)));
			email.setIsActive(true);
			email.setMdTypeId(getMetadataTypesByGrouping(MetadataTypeGrouping.EMAIL).get(0).getId());
			email.setName(getRandomName());
			emails.add(email);
		}
		request.setEmails(emails);
		
		final List<Phone> phones = new LinkedList<Phone>();
		for(int i = 0; i < 3; i++) {
			final Phone phone = new Phone();
			phone.setAreaCd(i + "");
			phone.setCountryCd(i + "");
			phone.setDescription(getRandomName());
			phone.setIsActive(true);
			phone.setMdTypeId(getMetadataTypesByGrouping(MetadataTypeGrouping.PHONE).get(0).getId());
			phone.setName(getRandomName());
			phone.setPhoneExt(i + "");
			phone.setPhoneNbr(String.format("%s%s%s %s%s%s", i, i, i, i, i, i));
			phone.setTotpSecret(getRandomName()); /* ensure not propagated to DB here!!!! */
			phones.add(phone);
		}
		request.setPhones(phones);
		
		final List<Login> loginList = new LinkedList<Login>();
		final Login login = new Login();
		login.setLogin(getRandomName());
		login.setManagedSysId(getDefaultManagedSystemId());
		loginList.add(login);
		request.setLoginList(loginList);
		
		//request.setGroupIds(groupIds);
		request.setLanguageId(getDefaultLanguage().getId());
		//request.setOrganizationIds(organizationIds);
		//request.setPageTemplate(pageTemplate);
		//request.setRoleIds(roleIds);
		
		/*
		final List<String> supervisorIds = new LinkedList<String>();
		supervisorIds.add(requestor.getId());
		request.setSupervisorIds(supervisorIds);
		*/
		
		final User user = new User();
		user.setFirstName(getRandomName());
		user.setLastName(getRandomName());
		user.setBirthdate(new Date());
		request.setUser(user);
		
		request.setTestRequest(true);
		request.addCustomApproverId(requestor.getId());
		request.setRequestorUserId(requestor.getId());
		return request;
	}
	
	@Test
	public void testSelfRegistrationWithMultipleApproverLevels() {
		
	}
	
	@Test
	public void testDeleteTask() throws InterruptedException {
		final NewUserProfileRequestModel request = createNewHireRequest(ActivitiRequestType.NEW_HIRE_WITH_APPROVAL);
		
		final SaveTemplateProfileResponse templateResponse = activitiClient.initiateNewHireRequest(request);
		Assert.assertTrue(templateResponse.isSuccess());
		Thread.sleep(5000L);
		
		
		final TaskSearchBean searchBean = new TaskSearchBean();
		searchBean.setOwnerId(requestor.getId());
		
		final List<TaskWrapper> wrappers = activitiClient.findTasks(searchBean, 0, Integer.MAX_VALUE);
		Assert.assertNotNull(wrappers);
		Assert.assertTrue(CollectionUtils.isNotEmpty(wrappers));
		wrappers.forEach(task -> {
			final Response response = activitiClient.deleteTask(task.getId(), requestor.getId());
			Assert.assertTrue(response.isSuccess());
		});
	}
	
	//TODO:  history
}
