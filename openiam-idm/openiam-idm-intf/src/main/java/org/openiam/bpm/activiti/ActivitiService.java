package org.openiam.bpm.activiti;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.openiam.activiti.model.dto.TaskSearchBean;
import org.openiam.base.ws.Response;
import org.openiam.bpm.dto.BasicWorkflowResponse;
import org.openiam.bpm.request.ActivitiClaimRequest;
import org.openiam.bpm.request.ActivitiRequestDecision;
import org.openiam.bpm.request.GenericWorkflowRequest;
import org.openiam.bpm.request.HistorySearchBean;
import org.openiam.bpm.response.ActivitiHistoricDetail;
import org.openiam.bpm.response.ActivitiHistoricDetail;
import org.openiam.bpm.response.NewHireResponse;
import org.openiam.bpm.response.ProcessWrapper;
import org.openiam.bpm.response.TaskHistoryWrapper;
import org.openiam.bpm.response.TaskListWrapper;
import org.openiam.bpm.response.TaskWrapper;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.provision.dto.ProvisionUser;

@WebService(targetNamespace = "urn:idm.openiam.org/bpm/request/service", name = "ActivitiService")
public interface ActivitiService {

	@WebMethod
	String sayHello();
	
	@WebMethod
	BasicWorkflowResponse initiateWorkflow(final GenericWorkflowRequest request);
	
	@WebMethod
	SaveTemplateProfileResponse initiateEditUserWorkflow(final UserProfileRequestModel request);
	
	@WebMethod
	SaveTemplateProfileResponse initiateNewHireRequest(final NewUserProfileRequestModel newHireRequest);
	
	@WebMethod
	Response claimRequest(final ActivitiClaimRequest newHireRequest);
	
	@WebMethod
	Response makeDecision(final ActivitiRequestDecision newHireRequest);
	
    /* use findTasks */
    @Deprecated
	@WebMethod
	TaskListWrapper getTasksForUser(final String userId, final int from, final int size);
    
    @WebMethod
    public TaskListWrapper getTasksForCandidateUserWithFilter(final String userId, final int from, final int size, String description, Date fromDate, Date toDate);

    @WebMethod
    public TaskListWrapper getTasksForAssignedUserWithFilter(final String userId, final int from, final int size, String description, String requesterId, Date fromDate, Date toDate);

    /* use findTasks */
    @Deprecated
    @WebMethod
	List<TaskWrapper> getTasksForMemberAssociation(String memberAssociationId);
	
    /* use countTasks */
    @Deprecated
	@WebMethod
	int getNumOfAssignedTasks(final String userId);
	
    /* use countTasks */
    @Deprecated
	@WebMethod
	int getNumOfCandidateTasks(final String userId);

	@WebMethod
    public int getNumOfAssignedTasksWithFilter(final String userId, String description, String requesterId, Date fromDate, Date toDate);

    @WebMethod
    public int getNumOfCandidateTasksWithFilter(final String userId, String description, Date fromDate, Date toDate);
	
	@WebMethod
	TaskWrapper getTask(final String taskId);
	
	@WebMethod
	TaskWrapper getTaskFromHistory(final String executionId, final String taskId);
	
	@WebMethod
	List<TaskWrapper> getHistory(final HistorySearchBean searchBean, final int from, final int size);
	
	@WebMethod
	List<TaskHistoryWrapper> getHistoryForInstance(final String instanceId);
	
	@WebMethod
	int count(final HistorySearchBean searchBean);
	
	@WebMethod
	Response deleteTask(final String taskId, final String userId);
	
	@WebMethod
	Response unclaimTask(final String taskId, final String userId);
	
	@WebMethod
	Response deleteTasksForUser(final String userId);
	
	@WebMethod
	List<TaskWrapper> findTasks(final TaskSearchBean searchBean, final int from, final int size);
	
	@WebMethod
	int countTasks(final TaskSearchBean searchBean);
	
	@WebMethod
	String getProcessInstanceIdByExecutionId(final String executionId);

	@WebMethod
	public List<String> getApproverUserIds(List<String> associationIds, final String targetUserId);

}
