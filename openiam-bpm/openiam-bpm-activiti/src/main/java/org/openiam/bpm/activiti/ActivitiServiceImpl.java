package org.openiam.bpm.activiti;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.bpm.activiti.util.ActivitiConstants;
import org.openiam.bpm.request.NewHireRequest;
import org.openiam.bpm.response.NewHireResponse;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.prov.request.dto.ProvisionRequest;
import org.openiam.idm.srvc.prov.request.dto.RequestApprover;
import org.openiam.idm.srvc.prov.request.service.RequestDataService;
import org.openiam.idm.srvc.user.dto.DelegationFilterSearch;
import org.openiam.idm.srvc.user.dto.Supervisor;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@WebService(endpointInterface = "org.openiam.bpm.activiti.ActivitiService", 
targetNamespace = "urn:idm.openiam.org/bpm/request/service", 
serviceName = "ActivitiService")
public class ActivitiServiceImpl implements ActivitiService {

	private static final Log log = LogFactory.getLog(ActivitiServiceImpl.class);
	
	/*
	@Autowired
	@Qualifier("activitiRuntimeService")
	private RuntimeService runtimeService;
	  
	@Autowired
	@Qualifier("activitiTaskService")
	private TaskService taskService;
	
	@Autowired
	@Qualifier("activitiRepositoryService")
	private RepositoryService repositoryService;
	  
	@Autowired
	@Qualifier("activitiManagementService")
	private ManagementService managementService;
	
	@Autowired
	@Qualifier("activitiHistoryService")
	private HistoryService historyService;
	
	@Autowired
	@Qualifier("approverAssociationDao")
	private ApproverAssociationDAO approverAssociationDao;
	
	@Autowired
	@Qualifier("provRequestService")
	private RequestDataService provRequestService;
	*/
	@Override
	@WebMethod
	public String sayHello() {
		return "Hello";
	}
	
	//TODO:  This was moved from the SelfService NewHireController to replicate the logic.  Why is this here?
	private static final String NEW_HIRE_REQUEST_TYPE = "254";

	@Override
	@WebMethod
	public NewHireResponse initiateNewHireRequest(final NewHireRequest newHireRequest) {
		final NewHireResponse response = new NewHireResponse();
		/*
		try {
			final ProvisionRequest provisionRequest = newHireRequest.getProvisionRequest();
			final ProvisionUser provisionUser = newHireRequest.getProvisionUser();
			
	        String approverRole = null;
	        String userOrg = null;
	        int applyDelegationFilter = 0;
			final List<ApproverAssociation> approverAssocationList = approverAssociationDao.findApproversByRequestType(NEW_HIRE_REQUEST_TYPE, 1);
	        if (CollectionUtils.isNotEmpty(approverAssocationList)) {
	            for (final ApproverAssociation approverAssociation : approverAssocationList) {
	                String approverType = null;
	                String roleDomain = null;
	                String approverId = null;
	                if (approverAssociation != null) {
	                    approverType = approverAssociation.getAssociationType();
	
	                    if(StringUtils.equalsIgnoreCase(approverAssociation.getAssociationType(), "supervisor")) {
	                    	final Supervisor supVisor = provisionUser.getSupervisor();
	                        approverId = supVisor.getSupervisor().getUserId();
	                    } else if(StringUtils.equalsIgnoreCase(approverAssociation.getAssociationType(), "role")) {
	                        approverId = approverAssociation.getApproverRoleId();
	                        roleDomain = approverAssociation.getApproverRoleDomain();
	
	                        approverRole = approverAssociation.getApproverRoleId();
	                        if (approverAssociation.getApplyDelegationFilter() != null) {
	                            applyDelegationFilter = approverAssociation.getApplyDelegationFilter().intValue();
	                        }
	                        if (StringUtils.isNotBlank(provisionUser.getCompanyId())) {
	                            userOrg = provisionUser.getCompanyId();
	                        }
	
	
	                    } else {
	                        approverId = approverAssociation.getApproverUserId();
	                    }
	
	
	                    final RequestApprover reqApprover = new RequestApprover(approverId, approverAssociation.getApproverLevel(), approverAssociation.getAssociationType(), "PENDING");
	                    reqApprover.setApproverType(approverType);
	                    reqApprover.setRoleDomain(roleDomain);
	
	                    newHireRequest.getProvisionRequest().getRequestApprovers().add(reqApprover);
	                }
	
	            }
	        }
			
			
			final DelegationFilterSearch delegationFilterSearch = new DelegationFilterSearch();
			delegationFilterSearch.setRole(approverRole);
			delegationFilterSearch.setDelAdmin(applyDelegationFilter);
			delegationFilterSearch.setOrgFilter("%" + userOrg + "%");
			
			final Map<String, Object> variables = new HashMap<String, Object>();
			variables.put(ActivitiConstants.NEW_HIRE_BPM_VAR, newHireRequest);
			variables.put(ActivitiConstants.DELEGATION_FILTER_SEARCH, delegationFilterSearch);
			final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("newHireWithApproval", variables);
			
			provRequestService.addRequest(provisionRequest);
			response.setStatus(ResponseStatus.SUCCESS);
		} catch(ActivitiException e) {
			log.info("Activiti Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(ResponseCode.USER_STATUS);
			response.setErrorText(e.getMessage());
		} catch(Throwable e) {
			log.error("Error while creating newhire request", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(ResponseCode.USER_STATUS);
			response.setErrorText(e.getMessage());
		}
		*/
		return response;
	}

	@Override
	@WebMethod
	public NewHireResponse claimNewHireRequest(final NewHireRequest newHireRequest) {
		final NewHireResponse response = new NewHireResponse();
		/*
		try {
			final List<Task> taskList = taskService.createTaskQuery().taskCandidateUser(newHireRequest.getCallerUserId()).list();
			if(CollectionUtils.isEmpty(taskList)) {
				throw new ActivitiException("No Candidate Task available");
			}
			
			if(StringUtils.isBlank(newHireRequest.getTaskId())) {
				throw new ActivitiException("No Task specified");
			}
			
			Task potentialTaskToClaim = null;
			for(final Task task : taskList) {
				if(task.getId().equals(newHireRequest.getTaskId())) {
					potentialTaskToClaim = task;
					break;
				}
			}
			
			if(potentialTaskToClaim == null) {
				throw new ActivitiException(String.format("Task with ID: '%s' not assigned to user", newHireRequest.getTaskId()));
			}
			
			final ProvisionRequest provisionRequest = newHireRequest.getProvisionRequest();
			
			taskService.claim(potentialTaskToClaim.getId(), newHireRequest.getCallerUserId());
			
			final Date currentDate = new Date();
			final String status = "CLAIMED";
			
			provisionRequest.setStatus(status);
			provisionRequest.setStatusDate(currentDate);
	        Set<RequestApprover> requestApprovers = provisionRequest.getRequestApprovers();
	        for (final RequestApprover requestApprovder  : requestApprovers ) {
	        	if(StringUtils.equalsIgnoreCase(requestApprovder.getApproverId(), newHireRequest.getCallerUserId())) {
	        		requestApprovder.setAction(status);
	            	requestApprovder.setActionDate(currentDate);
	            	requestApprovder.setComment(newHireRequest.getComment());
	        	}
	        }
			
			
			provRequestService.updateRequest(provisionRequest);
			response.setStatus(ResponseStatus.SUCCESS);
		} catch(ActivitiException e) {
			log.info("Activiti Exception", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(ResponseCode.USER_STATUS);
			response.setErrorText(e.getMessage());
		} catch(Throwable e) {
			log.error("Error while creating newhire request", e);
			response.setStatus(ResponseStatus.FAILURE);
			response.setErrorCode(ResponseCode.USER_STATUS);
			response.setErrorText(e.getMessage());
		}
		*/
		return response;
	}

	@Override
	@WebMethod
	public NewHireResponse acceptNewHireRequest(final NewHireRequest newHireRequest) {
		final NewHireResponse response = new NewHireResponse();
		
		return response;
	}

	@Override
	@WebMethod
	public NewHireResponse rejectNewHireRequest(final NewHireRequest newHireRequest) {
		final NewHireResponse response = new NewHireResponse();
		
		return response;
	}
}
