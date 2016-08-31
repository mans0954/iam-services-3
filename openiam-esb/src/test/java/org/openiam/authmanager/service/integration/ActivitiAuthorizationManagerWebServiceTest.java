package org.openiam.authmanager.service.integration;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.openiam.activiti.model.dto.TaskSearchBean;
import org.openiam.base.KeyDTO;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.srvc.activiti.ActivitiService;
import org.openiam.bpm.dto.BasicWorkflowResponse;
import org.openiam.base.request.ActivitiClaimRequest;
import org.openiam.base.request.ActivitiRequestDecision;
import org.openiam.base.request.GenericWorkflowRequest;
import org.openiam.base.response.TaskWrapper;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.bpm.util.ActivitiRequestType;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.srvc.idm.ManagedSystemWebService;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.util.CustomJacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ActivitiAuthorizationManagerWebServiceTest extends AbstractAuthorizationManagerValidator {

	private static final Log log = LogFactory.getLog(ActivitiAuthorizationManagerWebServiceTest.class);
	
	@Autowired
	@Qualifier("activitiClient")
	private ActivitiService activitiClient;
	
    @Autowired
    @Qualifier("managedSysServiceClient")
    private ManagedSystemWebService managedSysServiceClient;
    
    private static final CustomJacksonMapper mapper = new CustomJacksonMapper();
    
    private static final Map<Class, AssociationType> associationTypes = new HashMap<Class, AssociationType>();
    static {
    	associationTypes.put(Group.class, AssociationType.GROUP);
    	associationTypes.put(Organization.class, AssociationType.ORGANIZATION);
    	associationTypes.put(Role.class, AssociationType.ROLE);
    	associationTypes.put(Resource.class, AssociationType.RESOURCE);
    }

	@BeforeClass
	public void _init() {
		super._init();
		/*
		for(final KeyDTO dto : new KeyDTO[] {role, group, organization, resource}) {
			final ApproverAssociation association = new ApproverAssociation();
			association.setApproverEntityId(user.getId());
			association.setApproverEntityType(AssociationType.USER);
			association.setAssociationEntityId(dto.getId());
			association.setAssociationType(associationTypes.get(dto.getClass()));
			association.setTestRequest(true);
			final Response wsResponse = managedSysServiceClient.saveApproverAssociation(association);
			Assert.assertNotNull(wsResponse);
			Assert.assertTrue(wsResponse.isSuccess());
		}
		*/
		refreshAuthorizationManager();
	}
	
	@Override
	protected Set<String> rightsToIgnore() {
		final Set<String> rights = new HashSet<String>();
		rights.add("ADMIN");
		return rights;
	}

	@Override
	protected void checkUserURLEntitlements(String userId, String url) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected boolean loginAfterUserCreation() {
		return true;
	}
	
	@Override
	protected String getRequestorId() {
		return user.getId();
	}
	
	private static final Map<AssociationType, ActivitiConstants> constantMap = new HashMap<AssociationType, ActivitiConstants>();
	static  {
		constantMap.put(AssociationType.ROLE, ActivitiConstants.ROLE);
		constantMap.put(AssociationType.GROUP, ActivitiConstants.GROUP);
		constantMap.put(AssociationType.ORGANIZATION, ActivitiConstants.ORGANIZATION);
		constantMap.put(AssociationType.RESOURCE, ActivitiConstants.RESOURCE);
	}
	
	private BasicWorkflowResponse makeRequest(final ActivitiRequestType requestType,
												final String requestorId, 
												final String associationId,
												final AssociationType associationType,
												final String memberAssociationId,
												final AssociationType memberAssociationType,
												final Set<String> rightIds,
												final Date startDate, 
												final Date endDate,
												final KeyDTO entity) {
		final GenericWorkflowRequest request = new GenericWorkflowRequest();
		request.setDescription(getRandomName());
		request.setAccessRights(rightIds);
		request.setStartDate(startDate);
		request.setEndDate(endDate);
		request.setRequesterId(requestorId);
		request.setAssociationId(associationId);
		request.setAssociationType(associationType);
		request.setMemberAssociationId(memberAssociationId);
		request.setMemberAssociationType(memberAssociationType);
		request.setActivitiRequestType(requestType.getKey());
		request.setAccessRights(rightIds);
		request.setTestRequest(true);
		try {
			request.addJSONParameter(constantMap.get(associationType).getName(), entity, mapper);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		final BasicWorkflowResponse workflowResponse = activitiClient.initiateWorkflow(request);
		Assert.assertNotNull(workflowResponse);
		Assert.assertTrue(workflowResponse.isSuccess());
		return workflowResponse;
	}
	
	private boolean isAdminOfResource(final String userId, final String resourceId) {
		return authorizationManagerServiceClient.isUserEntitledToResourceWithRight(userId, resourceId, "ADMIN");
	}
	
	private boolean isAdminOfRole(final String userId, final String roleId) {
		return authorizationManagerServiceClient.isMemberOfRoleWithRight(userId, roleId, "ADMIN");
	}
	
	private boolean isAdminOfGroup(final String userId, final String groupId) {
		return authorizationManagerServiceClient.isMemberOfGroupWithRight(userId, groupId, "ADMIN");
	}
	
	private boolean isAdminOfOrganization(final String userId, final String organizationId) {
		return authorizationManagerServiceClient.isMemberOfOrganizationWithRight(userId, organizationId, "ADMIN");
	}
	
	private Response success() {
		return new Response(ResponseStatus.SUCCESS);
	}
	
	private void claimAllTasks(final String requestorId) {
		final TaskSearchBean sb = new TaskSearchBean();
		sb.setCandidateId(requestorId);
		final List<TaskWrapper> tasks = activitiClient.findTasks(sb, 0, Integer.MAX_VALUE);
		if(CollectionUtils.isNotEmpty(tasks)) {
			tasks.forEach(task -> {
				final ActivitiClaimRequest claimRequest = new ActivitiClaimRequest();
				claimRequest.setRequesterId(requestorId);
				claimRequest.setTestRequest(true);
				claimRequest.setTaskId(task.getId());
				final Response wsResponse = activitiClient.claimRequest(claimRequest);
				Assert.assertNotNull(wsResponse);
				Assert.assertTrue(wsResponse.isSuccess());
			});
		}
	}
	
	private void acceptAllTasks(final String requestorId) {
		final List<TaskWrapper> wrapper = getAssignedTasks(requestorId);
		Assert.assertTrue(CollectionUtils.isNotEmpty(wrapper));
		wrapper.forEach(task -> {
			final ActivitiRequestDecision decision = new ActivitiRequestDecision();
			decision.setAccepted(true);
			decision.setTaskId(task.getId());
			decision.setTestRequest(true);
			decision.setRequesterId(requestorId);
			final Response wsResponse = activitiClient.makeDecision(decision);
			Assert.assertNotNull(wsResponse);
			Assert.assertTrue(wsResponse.isSuccess());
		});
	}
	
	private void claimAndAcceptAllTasks(final String requestorId) {
		claimAllTasks(requestorId);
		acceptAllTasks(requestorId);
	}
	
	private List<TaskWrapper> getAssignedTasks(final String requestorId) {
		final TaskSearchBean sb = new TaskSearchBean();
		sb.setAssigneeId(requestorId);
		final List<TaskWrapper> tasks = activitiClient.findTasks(sb, 0, Integer.MAX_VALUE);
		return tasks;
	}

	@Override
	protected Response doAddUserToResource(final String resourceId, 
										   final String userId,
										   final String requestorId, 
										   final Set<String> rightIds, 
										   final Date startDate, 
										   final Date endDate) {
		final BasicWorkflowResponse workflowResponse = makeRequest(ActivitiRequestType.ENTITLE_USER_TO_RESOURCE, requestorId, resourceId, AssociationType.RESOURCE, userId, AssociationType.USER, rightIds, startDate, endDate, null);
		if(!isAdminOfResource(requestorId, resourceId)) {
			claimAndAcceptAllTasks(requestorId);
		}
		return success();
	}

	@Override
	protected Response doRemoveUserFromResource(String resourceId,
			String userId, String requestorId) {
		final BasicWorkflowResponse workflowResponse = makeRequest(ActivitiRequestType.DISENTITLE_USR_FROM_RESOURCE, requestorId, resourceId, AssociationType.RESOURCE, userId, AssociationType.USER, null, null, null, null);
		if(!isAdminOfResource(requestorId, resourceId)) {
			claimAndAcceptAllTasks(requestorId);
		}
		return success();
	}

	@Override
	protected Response doDeleteResource(final Resource resource, String requestorId) {
		final BasicWorkflowResponse workflowResponse = makeRequest(ActivitiRequestType.DELETE_RESOURCE, requestorId, resource.getId(), AssociationType.RESOURCE, null, null, null, null, null, resource);
		if(!isAdminOfResource(requestorId, resource.getId())) {
			claimAndAcceptAllTasks(requestorId);
		}
		return success();
	}

	@Override
	protected Response doAddGroupToResource(String resourceId, String groupId,
			String requestorId, Set<String> rightIds, Date startDate,
			Date endDate) {
		final BasicWorkflowResponse workflowResponse = makeRequest(ActivitiRequestType.ENTITLE_RESOURCE_TO_GROUP, requestorId, resourceId, AssociationType.RESOURCE, groupId, AssociationType.GROUP, rightIds, startDate, endDate, null);
		if(!isAdminOfResource(requestorId, resourceId)) {
			claimAndAcceptAllTasks(requestorId);
		}
		return success();
	}

	@Override
	protected Response doAddRoleToResource(String resourceId, String roleId,
			String requestorId, Set<String> rightIds, Date startDate,
			Date endDate) {
		final BasicWorkflowResponse workflowResponse = makeRequest(ActivitiRequestType.ENTITLE_RESOURCE_TO_ROLE, requestorId, resourceId, AssociationType.RESOURCE, roleId, AssociationType.ROLE, rightIds, startDate, endDate, null);
		if(!isAdminOfResource(requestorId, resourceId)) {
			claimAndAcceptAllTasks(requestorId);
		}
		return success();
	}

	@Override
	protected Response doAddUserToRole(String roleId, String userId,
			String requestorId, Set<String> rightIds, Date startDate,
			Date endDate) {
		final BasicWorkflowResponse workflowResponse = makeRequest(ActivitiRequestType.ADD_USER_TO_ROLE, requestorId, roleId, AssociationType.ROLE, userId, AssociationType.USER, rightIds, startDate, endDate, null);
		if(!isAdminOfRole(requestorId, roleId)) {
			claimAndAcceptAllTasks(requestorId);
		}
		return success();
	}

	@Override
	protected Response doAddChildRole(String roleId, String childRoleId,
			String requestorId, Set<String> rightIds, Date startDate,
			Date endDate) {
		final BasicWorkflowResponse workflowResponse = makeRequest(ActivitiRequestType.ADD_ROLE_TO_ROLE, requestorId, roleId, AssociationType.ROLE, childRoleId, AssociationType.ROLE, rightIds, startDate, endDate, null);
		if(!isAdminOfRole(requestorId, roleId)) {
			claimAndAcceptAllTasks(requestorId);
		}
		return success();
	}

	@Override
	protected Response doRemoveUserFromRole(String roleId, String userId,
			String requestorId) {
		final BasicWorkflowResponse workflowResponse = makeRequest(ActivitiRequestType.REMOVE_USER_FROM_ROLE, requestorId, roleId, AssociationType.ROLE, userId, AssociationType.USER, null, null, null, null);
		if(!isAdminOfRole(requestorId, roleId)) {
			claimAndAcceptAllTasks(requestorId);
		}
		return success();
	}

	@Override
	protected Response doAddUserToGroup(String groupId, String userId,
			String requestorId, Set<String> rightIds, Date startDate,
			Date endDate) {
		final BasicWorkflowResponse workflowResponse = makeRequest(ActivitiRequestType.ADD_USER_TO_GROUP, requestorId, groupId, AssociationType.GROUP, userId, AssociationType.USER, rightIds, startDate, endDate, null);
		if(!isAdminOfGroup(requestorId, groupId)) {
			claimAndAcceptAllTasks(requestorId);
		}
		return success();
	}

	@Override
	protected Response doRemoveUserFromGroup(String groupId, String userId,
			String requestorId) {
		final BasicWorkflowResponse workflowResponse = makeRequest(ActivitiRequestType.REMOVE_USER_FROM_GROUP, requestorId, groupId, AssociationType.GROUP, userId, AssociationType.USER, null, null, null, null);
		if(!isAdminOfGroup(requestorId, groupId)) {
			claimAndAcceptAllTasks(requestorId);
		}
		return success();
	}

	@Override
	protected Response doAddChildGroup(String groupId, String childGroupId,
			String requestorId, Set<String> rightIds, Date startDate,
			Date endDate) {
		final BasicWorkflowResponse workflowResponse = makeRequest(ActivitiRequestType.ADD_GROUP_TO_GROUP, requestorId, groupId, AssociationType.GROUP, childGroupId, AssociationType.GROUP, rightIds, startDate, endDate, null);
		if(!isAdminOfGroup(requestorId, groupId)) {
			claimAndAcceptAllTasks(requestorId);
		}
		return success();
	}


	@Override
	protected Response doAddChildResource(String resourceId,
			String childResourceId, String requestorId, Set<String> rightIds,
			Date startDate, Date endDate) {
		final BasicWorkflowResponse workflowResponse = makeRequest(ActivitiRequestType.ADD_RESOURCE_TO_RESOURCE, requestorId, resourceId, AssociationType.RESOURCE, childResourceId, AssociationType.RESOURCE, rightIds, startDate, endDate, null);
		if(!isAdminOfResource(requestorId, resourceId)) {
			claimAndAcceptAllTasks(requestorId);
		}
		return success();
	}

	@Override
	protected Response doDeleteGroup(final Group group, String requestorId) {
		final BasicWorkflowResponse workflowResponse = makeRequest(ActivitiRequestType.DELETE_GROUP, requestorId, group.getId(), AssociationType.GROUP, null, null, null, null, null, group);
		if(!isAdminOfGroup(requestorId, group.getId())) {
			claimAndAcceptAllTasks(requestorId);
		}
		return success();
	}

	@Override
	protected Response doAddGroupToRole(String roleId, String groupId,
			String requestorId, Set<String> rightIds, Date startDate,
			Date endDate) {
		final BasicWorkflowResponse workflowResponse = makeRequest(ActivitiRequestType.ADD_ROLE_TO_GROUP, requestorId, roleId, AssociationType.ROLE, groupId, AssociationType.GROUP, rightIds, startDate, endDate, null);
		if(!isAdminOfRole(requestorId, roleId)) {
			claimAndAcceptAllTasks(requestorId);
		}
		return success();
	}

	@Override
	protected Response doRemoveGroupFromRole(String roleId, String groupId,
			String requestorId) {
		final BasicWorkflowResponse workflowResponse = makeRequest(ActivitiRequestType.REMOVE_ROLE_FROM_GROUP, requestorId, roleId, AssociationType.ROLE, groupId, AssociationType.GROUP, null, null, null, null);
		if(!isAdminOfRole(requestorId, roleId)) {
			claimAndAcceptAllTasks(requestorId);
		}
		return success();
	}

	@Override
	protected Response doAddUserToOrg(String organizationId, String userId,
			String requestorId, Set<String> rightIds, Date startDate,
			Date endDate) {
		final BasicWorkflowResponse workflowResponse = makeRequest(ActivitiRequestType.ADD_USER_TO_ORG, requestorId, organizationId, AssociationType.ORGANIZATION, userId, AssociationType.USER, rightIds, startDate, endDate, null);
		if(!isAdminOfOrganization(requestorId, organizationId)) {
			claimAndAcceptAllTasks(requestorId);
		}
		return success();
	}

	@Override
	protected Response doRemoveUserFromOrg(String organizationId,
			String userId, String requestorId) {
		final BasicWorkflowResponse workflowResponse = makeRequest(ActivitiRequestType.REMOVE_USER_FROM_ORG, requestorId, organizationId, AssociationType.ORGANIZATION, userId, AssociationType.USER, null, null, null, null);
		if(!isAdminOfOrganization(requestorId, organizationId)) {
			claimAndAcceptAllTasks(requestorId);
		}
		return success();
	}

	@Override
	protected Response doRemoveRoleToResource(String resourceId, String roleId,
			String requestorId) {
		final BasicWorkflowResponse workflowResponse = makeRequest(ActivitiRequestType.DISENTITLE_RESOURCE_FROM_ROLE, requestorId, resourceId, AssociationType.RESOURCE, roleId, AssociationType.ROLE, null, null, null, null);
		if(!isAdminOfResource(requestorId, resourceId)) {
			claimAndAcceptAllTasks(requestorId);
		}
		return success();
	}

	@Override
	protected Response doRemoveGroupToResource(String resourceId,
			String groupId, String requestorId) {
		final BasicWorkflowResponse workflowResponse = makeRequest(ActivitiRequestType.DISENTITLE_RESOURCE_FROM_GROUP, requestorId, resourceId, AssociationType.RESOURCE, groupId, AssociationType.GROUP, null, null, null, null);
		if(!isAdminOfResource(requestorId, resourceId)) {
			claimAndAcceptAllTasks(requestorId);
		}
		return success();
	}

	@Override
	protected Response doRemoveRole(final Role role, String requestorId) {
		final BasicWorkflowResponse workflowResponse = makeRequest(ActivitiRequestType.DELETE_ROLE, requestorId, role.getId(), AssociationType.ROLE, null, null, null, null, null, role);
		if(!isAdminOfRole(requestorId, role.getId())) {
			claimAndAcceptAllTasks(requestorId);
		}
		return success();
	}

	@Override
	protected Response doRemoveOrganization(final Organization organization,
			String requestorId) {
		final BasicWorkflowResponse workflowResponse = makeRequest(ActivitiRequestType.DELETE_ORGANIZATION, requestorId, organization.getId(), AssociationType.ORGANIZATION, null, null, null, null, null, organization);
		if(!isAdminOfOrganization(requestorId, organization.getId())) {
			claimAndAcceptAllTasks(requestorId);
		}
		return success();
	}

	@Override
	@Test
	public void testGetOrgsForUserStartDate() {
		super.testGetOrgsForUserStartDate();
	}
}
