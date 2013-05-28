package org.openiam.bpm.activiti;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.openiam.base.ws.Response;
import org.openiam.bpm.request.ActivitiClaimRequest;
import org.openiam.bpm.request.ActivitiRequestDecision;
import org.openiam.bpm.response.NewHireResponse;
import org.openiam.bpm.response.TaskListWrapper;
import org.openiam.bpm.response.TaskWrapper;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;
import org.openiam.idm.srvc.prov.request.dto.ProvisionRequest;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.provision.dto.ProvisionUser;

@WebService(targetNamespace = "urn:idm.openiam.org/bpm/request/service", name = "ActivitiService")
public interface ActivitiService {

	@WebMethod
	public String sayHello();
	
	@WebMethod
	public SaveTemplateProfileResponse initiateNewHireRequest(final NewUserProfileRequestModel newHireRequest);
	
	@WebMethod
	public Response claimRequest(final ActivitiClaimRequest newHireRequest);
	
	@WebMethod
	public Response acceptRequest(final ActivitiRequestDecision newHireRequest);
	
	@WebMethod
	public Response rejectRequest(final ActivitiRequestDecision newHireRequest);
	
	@WebMethod
	public TaskListWrapper getTasksForUser(final String userId);
	
	@WebMethod
	public TaskWrapper getTask(final String taskId);
}
