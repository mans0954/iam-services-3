package org.openiam.srvc.activiti;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.activiti.model.dto.TaskSearchBean;
import org.openiam.base.response.TaskHistoryWrapper;
import org.openiam.base.response.TaskListWrapper;
import org.openiam.base.response.TaskWrapper;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.ActivitiDataService;
import org.openiam.bpm.dto.BasicWorkflowResponse;
import org.openiam.base.request.ActivitiClaimRequest;
import org.openiam.base.request.ActivitiRequestDecision;
import org.openiam.base.request.GenericWorkflowRequest;
import org.openiam.activiti.model.dto.HistorySearchBean;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.*;


@Component("activitiBPMService")
@WebService(endpointInterface = "org.openiam.srvc.activiti.ActivitiService",
		targetNamespace = "urn:idm.openiam.org/bpm/request/service",
		serviceName = "ActivitiService")
public class ActivitiServiceImpl extends AbstractBaseService implements ActivitiService {

	private static final Log log = LogFactory.getLog(ActivitiServiceImpl.class);

	@Autowired
	private ActivitiDataService activitiDataService;



	@Override
	@WebMethod
	public String sayHello() {
		return activitiDataService.sayHello();
	}

	@Override
	@WebMethod
	public SaveTemplateProfileResponse initiateNewHireRequest(final NewUserProfileRequestModel request) {
		return activitiDataService.initiateNewHireRequest(request);
	}

	@Override
	@WebMethod
	public Response claimRequest(final ActivitiClaimRequest request) {
		return activitiDataService.claimRequest(request);
	}

	@Override
	public SaveTemplateProfileResponse initiateEditUserWorkflow(final UserProfileRequestModel request) {
		return activitiDataService.initiateEditUserWorkflow(request);
	}


	@Override
	public BasicWorkflowResponse initiateWorkflow(final GenericWorkflowRequest request) {
		return activitiDataService.initiateWorkflow(request);
	}

	@Override
	public String getProcessInstanceIdByExecutionId(String executionId) {
		return activitiDataService.getProcessInstanceIdByExecutionId(executionId);
	}



	@Override
	@WebMethod
	public Response makeDecision(final ActivitiRequestDecision request) {
		return activitiDataService.makeDecision(request);
	}



	@Override
	@WebMethod
	@Deprecated
	public int getNumOfAssignedTasks(String userId) {
		return activitiDataService.getNumOfAssignedTasks(userId);
	}

	@Override
	@WebMethod
	@Deprecated
	public int getNumOfCandidateTasks(String userId) {
		return activitiDataService.getNumOfCandidateTasks(userId);
	}

	@Override
	@WebMethod
	public TaskWrapper getTask(String taskId) {
		return activitiDataService.getTask(taskId);
	}


	@Override
	public TaskWrapper getTaskFromHistory(final String executionId, final String taskId) {
		return activitiDataService.getTaskFromHistory(executionId, taskId);
	}

	@Override
	public int getNumOfAssignedTasksWithFilter(String userId, String description, String requesterId, Date fromDate, Date toDate) {
		return activitiDataService.getNumOfAssignedTasksWithFilter(userId, description, requesterId, fromDate, toDate);
	}

	@Override
	public int getNumOfCandidateTasksWithFilter(String userId, String description, Date fromDate, Date toDate) {
		return activitiDataService.getNumOfCandidateTasksWithFilter(userId, description, fromDate, toDate);
	}

	@Override
	public TaskListWrapper getTasksForCandidateUserWithFilter(String userId, int from, int size, String description, Date fromDate, Date toDate) {
		return activitiDataService.getTasksForCandidateUserWithFilter(userId, from, size, description, fromDate, toDate);
	}

	@Override
	public TaskListWrapper getTasksForAssignedUserWithFilter(String userId, int from, int size, String description, String requesterId, Date fromDate, Date toDate) {
		return activitiDataService.getTasksForAssignedUserWithFilter(userId, from, size, description, requesterId, fromDate, toDate);
	}


	@Override
	public List<TaskHistoryWrapper> getHistoryForInstance(final String executionId) {
		return activitiDataService.getHistoryForInstance(executionId);
	}

	@Override
	public List<TaskWrapper> getHistory(final HistorySearchBean searchBean, final int from, final int size) {
		return activitiDataService.getHistory(searchBean, from, size);
	}

	@Override
	public int count(final HistorySearchBean searchBean) {
		return activitiDataService.count(searchBean);
	}


	@Override
	public Response deleteTask(String taskId, final String userId) {
		return activitiDataService.deleteTask(taskId, userId);
	}

	@Override
	public Response unclaimTask(String taskId, String userId) {
		return activitiDataService.unclaimTask(taskId, userId);
	}

	@Override
	@WebMethod
	@Deprecated
	public TaskListWrapper getTasksForUser(final String userId, final int from, final int size) {
		return activitiDataService.getTasksForUser(userId, from, size);
	}

	@Override
	public Response deleteTasksForUser(final String userId) {
		return activitiDataService.deleteTasksForUser(userId);
	}

	@Override
	@WebMethod
	@Deprecated
	public List<TaskWrapper> getTasksForMemberAssociation(String memberAssociationId) {
		return activitiDataService.getTasksForMemberAssociation(memberAssociationId);
	}

	@Override
	public List<TaskWrapper> findTasks(TaskSearchBean searchBean, int from, int size) {
		return activitiDataService.findTasks(searchBean, from, size);
	}

	@Override
	public int countTasks(TaskSearchBean searchBean) {
		return activitiDataService.countTasks(searchBean);
	}

	@Override
	public List<String> getApproverUserIds(List<String> associationIds, final String targetUserId) {
		return activitiDataService.getApproverUserIds(associationIds, targetUserId);
	}
}

