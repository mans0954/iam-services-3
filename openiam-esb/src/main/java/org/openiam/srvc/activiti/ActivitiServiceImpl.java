package org.openiam.srvc.activiti;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.activiti.model.dto.TaskSearchBean;
import org.openiam.base.request.*;
import org.openiam.base.response.*;
import org.openiam.base.ws.Response;
import org.openiam.bpm.activiti.ActivitiDataService;
import org.openiam.bpm.dto.BasicWorkflowResponse;
import org.openiam.activiti.model.dto.HistorySearchBean;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.mq.constants.ActivitiAPI;
import org.openiam.mq.constants.queue.OpenIAMQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.*;


@Component("activitiBPMService")
@WebService(endpointInterface = "org.openiam.srvc.activiti.ActivitiService",
		targetNamespace = "urn:idm.openiam.org/bpm/request/service",
		serviceName = "ActivitiService")
public class ActivitiServiceImpl extends AbstractApiService implements ActivitiService {

	private static final Log log = LogFactory.getLog(ActivitiServiceImpl.class);

	@Autowired
	private ActivitiDataService activitiDataService;

	public ActivitiServiceImpl() {
		super(OpenIAMQueue.ActivitiQueue);
	}


	@Override
	@WebMethod
	public String sayHello() {
		return "Hello!";
	}

	@Override
	@WebMethod
	public SaveTemplateProfileResponse initiateNewHireRequest(final NewUserProfileRequestModel request) {
		UserProfileServiceRequest wrapper = new UserProfileServiceRequest();
		wrapper.setModel(request);

		return this.manageApiRequest(ActivitiAPI.InitiateNewHireRequest, wrapper, SaveTemplateProfileResponse.class);
	}

	@Override
	@WebMethod
	public Response claimRequest(final ActivitiClaimRequest request) {
		return this.manageApiRequest(ActivitiAPI.ClaimRequest, request, Response.class);
	}

	@Override
	public SaveTemplateProfileResponse initiateEditUserWorkflow(final UserProfileRequestModel request) {
		UserProfileServiceRequest wrapper = new UserProfileServiceRequest();
		wrapper.setModel(request);
		return this.manageApiRequest(ActivitiAPI.InitiateEditUserWorkflow, wrapper, SaveTemplateProfileResponse.class);
	}


	@Override
	public BasicWorkflowResponse initiateWorkflow(final GenericWorkflowRequest request) {
		return this.manageApiRequest(ActivitiAPI.InitiateWorkflow, request, BasicWorkflowResponse.class);
	}

	@Override
	public String getProcessInstanceIdByExecutionId(String executionId) {
		IdServiceRequest request = new IdServiceRequest();
		request.setId(executionId);
		StringResponse resp = this.manageApiRequest(ActivitiAPI.ProcessInstanceIdByExecutionId, request, StringResponse.class);
		return resp.getValue();
	}

	@Override
	@WebMethod
	public Response makeDecision(final ActivitiRequestDecision request) {
		return this.manageApiRequest(ActivitiAPI.MakeDecision, request, Response.class);
	}

	@Override
	@WebMethod
	@Deprecated
	public int getNumOfAssignedTasks(String userId) {
		return countTasks(new TaskSearchBean().setAssigneeId(userId));
	}

	@Override
	@WebMethod
	@Deprecated
	public int getNumOfCandidateTasks(String userId) {
		return countTasks(new TaskSearchBean().setCandidateId(userId));
	}

	@Override
	@WebMethod
	public TaskWrapper getTask(String taskId) {
		IdServiceRequest request = new IdServiceRequest();
		request.setId(taskId);
		TaskWrapperResponse resp = this.manageApiRequest(ActivitiAPI.GetTask, request, TaskWrapperResponse.class);
		if(resp.isFailure()){
			return null;
		}
		return resp.getTask();
	}

	@Override
	public TaskWrapper getTaskFromHistory(final String executionId, final String taskId) {
		ActivitiFilterRequest request = new ActivitiFilterRequest();
		request.setExecutionId(executionId);
		request.setTaskId(taskId);
		TaskWrapperResponse resp = this.manageApiRequest(ActivitiAPI.TaskFromHistory, request, TaskWrapperResponse.class);
		if(resp.isFailure()){
			return null;
		}
		return resp.getTask();
	}

	@Override
	public int getNumOfAssignedTasksWithFilter(String userId, String description, String requesterId, Date fromDate, Date toDate) {
		ActivitiFilterRequest request = new ActivitiFilterRequest();
		request.setUserId(userId);
		request.setDescription(description);
		request.setRequesterId(requesterId);
		request.setFromDate(fromDate);
		request.setToDate(toDate);

		IntResponse resp = this.manageApiRequest(ActivitiAPI.NumOfAssignedTasksWithFilter, request, IntResponse.class);
		return resp.getValue();
	}

	@Override
	public int getNumOfCandidateTasksWithFilter(String userId, String description, Date fromDate, Date toDate) {
		ActivitiFilterRequest request = new ActivitiFilterRequest();
		request.setUserId(userId);
		request.setDescription(description);
		request.setFromDate(fromDate);
		request.setToDate(toDate);

		IntResponse resp = this.manageApiRequest(ActivitiAPI.NumOfCandidateTasksWithFilter, request, IntResponse.class);
		return resp.getValue();
	}

	@Override
	public TaskListWrapper getTasksForCandidateUserWithFilter(String userId, int from, int size, String description, Date fromDate, Date toDate) {
		ActivitiFilterRequest request = new ActivitiFilterRequest();
		request.setUserId(userId);
		request.setDescription(description);
		request.setFromDate(fromDate);
		request.setToDate(toDate);
		request.setFrom(from);
		request.setSize(size);

		TaskListWrapperResponse resp = this.manageApiRequest(ActivitiAPI.TasksForCandidateUserWithFilter, request, TaskListWrapperResponse.class);
		if(resp.isFailure()){
			return null;
		}
		return resp.getTaskListWrapper();
	}

	@Override
	public TaskListWrapper getTasksForAssignedUserWithFilter(String userId, int from, int size, String description, String requesterId, Date fromDate, Date toDate) {
		ActivitiFilterRequest request = new ActivitiFilterRequest();
		request.setUserId(userId);
		request.setDescription(description);
		request.setFromDate(fromDate);
		request.setToDate(toDate);
		request.setFrom(from);
		request.setSize(size);
		request.setRequesterId(requesterId);

		TaskListWrapperResponse resp = this.manageApiRequest(ActivitiAPI.TasksForAssignedUserWithFilter, request, TaskListWrapperResponse.class);
		if(resp.isFailure()){
			return null;
		}
		return resp.getTaskListWrapper();
	}


	@Override
	public List<TaskHistoryWrapper> getHistoryForInstance(final String executionId) {
		IdServiceRequest request = new IdServiceRequest();
		request.setId(executionId);
		TaskHistoryListResponse resp = this.manageApiRequest(ActivitiAPI.HistoryForInstance, request, TaskHistoryListResponse.class);
		if(resp.isFailure()){
			return null;
		}
		return resp.getTaskHistoryList();
	}

	@Override
	public List<TaskWrapper> getHistory(final HistorySearchBean searchBean, final int from, final int size) {
		HistorySearchRequest request = new HistorySearchRequest();
		request.setSearchBean(searchBean);
		request.setFrom(from);
		request.setSize(size);

		TaskListResponse resp = this.manageApiRequest(ActivitiAPI.GetHistory, request, TaskListResponse.class);
		if(resp.isFailure()){
			return null;
		}
		return resp.getTaskList();
	}

	@Override
	public int count(final HistorySearchBean searchBean) {
		HistorySearchRequest request = new HistorySearchRequest();
		request.setSearchBean(searchBean);

		IntResponse resp = this.manageApiRequest(ActivitiAPI.Count, request, IntResponse.class);
		if(resp.isFailure()){
			return 0;
		}
		return resp.getValue();
	}


	@Override
	public Response deleteTask(String taskId, final String userId) {
		ActivitiFilterRequest request = new ActivitiFilterRequest();
		request.setUserId(userId);
		request.setTaskId(taskId);

		return this.manageApiRequest(ActivitiAPI.DeleteTask, request, Response.class);
	}

	@Override
	public Response unclaimTask(String taskId, String userId) {
		ActivitiFilterRequest request = new ActivitiFilterRequest();
		request.setUserId(userId);
		request.setTaskId(taskId);
		return this.manageApiRequest(ActivitiAPI.UnclaimTask, request, Response.class);
	}

	@Override
	@WebMethod
	@Deprecated
	public TaskListWrapper getTasksForUser(final String userId, final int from, final int size) {
		ActivitiFilterRequest request = new ActivitiFilterRequest();
		request.setUserId(userId);
		request.setFrom(from);
		request.setSize(size);

		TaskListWrapperResponse response = this.manageApiRequest(ActivitiAPI.GetTasksForUser, request, TaskListWrapperResponse.class);
		if(response.isFailure()){
			return null;
		}
		return response.getTaskListWrapper();
	}

	@Override
	public Response deleteTasksForUser(final String userId) {
		ActivitiFilterRequest request = new ActivitiFilterRequest();
		request.setUserId(userId);
		return this.manageApiRequest(ActivitiAPI.DeleteTasksForUser, request, Response.class);
	}

	@Override
	@WebMethod
	@Deprecated
	public List<TaskWrapper> getTasksForMemberAssociation(String memberAssociationId) {
		final TaskSearchBean searchBean = new TaskSearchBean();
		searchBean.setMemberAssociationId(memberAssociationId);
		return findTasks(searchBean, 0, Integer.MAX_VALUE);
	}

	@Override
	public List<TaskWrapper> findTasks(TaskSearchBean searchBean, int from, int size) {
		TaskSearchRequest request = new TaskSearchRequest();
		request.setSearchBean(searchBean);
		request.setFrom(from);
		request.setSize(size);

		TaskListResponse response = this.manageApiRequest(ActivitiAPI.FindTasks, request, TaskListResponse.class);
		if(response.isFailure()){
			return null;
		}
		return response.getTaskList();
	}

	@Override
	public int countTasks(TaskSearchBean searchBean) {
		TaskSearchRequest request = new TaskSearchRequest();
		request.setSearchBean(searchBean);
		IntResponse response = this.manageApiRequest(ActivitiAPI.CountTasks, request, IntResponse.class);
		if(response.isFailure()){
			return 0;
		}
		return response.getValue();
	}

	@Override
	public List<String> getApproverUserIds(List<String> associationIds, final String targetUserId) {
		return activitiDataService.getApproverUserIds(associationIds, targetUserId);
	}
}

