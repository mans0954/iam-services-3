package org.openiam.bpm.activiti;

import org.openiam.activiti.model.dto.TaskSearchBean;
import org.openiam.base.ws.Response;
import org.openiam.bpm.dto.BasicWorkflowResponse;
import org.openiam.bpm.request.ActivitiClaimRequest;
import org.openiam.bpm.request.ActivitiRequestDecision;
import org.openiam.bpm.request.GenericWorkflowRequest;
import org.openiam.bpm.request.HistorySearchBean;
import org.openiam.base.response.TaskHistoryWrapper;
import org.openiam.base.response.TaskListWrapper;
import org.openiam.base.response.TaskWrapper;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;

import java.util.Date;
import java.util.List;

public interface ActivitiDataService {

	String sayHello();
	
	BasicWorkflowResponse initiateWorkflow(final GenericWorkflowRequest request);
	
	SaveTemplateProfileResponse initiateEditUserWorkflow(final UserProfileRequestModel request);
	
	SaveTemplateProfileResponse initiateNewHireRequest(final NewUserProfileRequestModel newHireRequest);
	
	Response claimRequest(final ActivitiClaimRequest newHireRequest);
	
	Response makeDecision(final ActivitiRequestDecision newHireRequest);
	
    /* use findTasks */
    @Deprecated
	TaskListWrapper getTasksForUser(final String userId, final int from, final int size);
    
    public TaskListWrapper getTasksForCandidateUserWithFilter(final String userId, final int from, final int size, String description, Date fromDate, Date toDate);

    public TaskListWrapper getTasksForAssignedUserWithFilter(final String userId, final int from, final int size, String description, String requesterId, Date fromDate, Date toDate);

    /* use findTasks */
    @Deprecated
	List<TaskWrapper> getTasksForMemberAssociation(String memberAssociationId);
	
    /* use countTasks */
    @Deprecated
	int getNumOfAssignedTasks(final String userId);
	
    /* use countTasks */
    @Deprecated
	int getNumOfCandidateTasks(final String userId);

    public int getNumOfAssignedTasksWithFilter(final String userId, String description, String requesterId, Date fromDate, Date toDate);

    public int getNumOfCandidateTasksWithFilter(final String userId, String description, Date fromDate, Date toDate);
	
	TaskWrapper getTask(final String taskId);
	
	TaskWrapper getTaskFromHistory(final String executionId, final String taskId);
	
	List<TaskWrapper> getHistory(final HistorySearchBean searchBean, final int from, final int size);
	
	List<TaskHistoryWrapper> getHistoryForInstance(final String instanceId);
	
	int count(final HistorySearchBean searchBean);
	
	Response deleteTask(final String taskId, final String userId);
	
	Response unclaimTask(final String taskId, final String userId);
	
	Response deleteTasksForUser(final String userId);
	
	List<TaskWrapper> findTasks(final TaskSearchBean searchBean, final int from, final int size);
	
	int countTasks(final TaskSearchBean searchBean);
	
	String getProcessInstanceIdByExecutionId(final String executionId);

	public List<String> getApproverUserIds(List<String> associationIds, final String targetUserId);

}
