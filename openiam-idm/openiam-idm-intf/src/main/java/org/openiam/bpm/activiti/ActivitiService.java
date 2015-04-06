package org.openiam.bpm.activiti;

import java.util.List;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.openiam.activiti.model.dto.TaskSearchBean;
import org.openiam.base.ws.Response;
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
	public String sayHello();
	
	@WebMethod
	public Response initiateWorkflow(final GenericWorkflowRequest request);
	
	@WebMethod
	public SaveTemplateProfileResponse initiateEditUserWorkflow(final UserProfileRequestModel request);
	
	@WebMethod
	public SaveTemplateProfileResponse initiateNewHireRequest(final NewUserProfileRequestModel newHireRequest);
	
	@WebMethod
	public Response claimRequest(final ActivitiClaimRequest newHireRequest);
	
	@WebMethod
	public Response makeDecision(final ActivitiRequestDecision newHireRequest);
	
    /* use findTasks */
    @Deprecated
	@WebMethod
	public TaskListWrapper getTasksForUser(final String userId, final int from, final int size);
    
    /* use findTasks */
    @Deprecated
    @WebMethod
    public List<TaskWrapper> getTasksForMemberAssociation(String memberAssociationId);
	
    /* use countTasks */
    @Deprecated
	@WebMethod
	public int getNumOfAssignedTasks(final String userId);
	
    /* use countTasks */
    @Deprecated
	@WebMethod
	public int getNumOfCandidateTasks(final String userId);
	
	@WebMethod
	public TaskWrapper getTask(final String taskId);
	
	@WebMethod
	public TaskWrapper getTaskFromHistory(final String executionId, final String taskId);
	
	@WebMethod
	public List<TaskWrapper> getHistory(final HistorySearchBean searchBean, final int from, final int size);
	
	@WebMethod
	public List<TaskHistoryWrapper> getHistoryForInstance(final String instanceId);
	
	@WebMethod
	public int count(final HistorySearchBean searchBean);
	
	@WebMethod
	public Response deleteTask(final String taskId, final String userId);
	
	@WebMethod
	public Response unclaimTask(final String taskId, final String userId);
	
	@WebMethod
	public Response deleteTasksForUser(final String userId);
	
	@WebMethod
	public List<TaskWrapper> findTasks(final TaskSearchBean searchBean, final int from, final int size);
	
	@WebMethod
	public int countTasks(final TaskSearchBean searchBean);
}
