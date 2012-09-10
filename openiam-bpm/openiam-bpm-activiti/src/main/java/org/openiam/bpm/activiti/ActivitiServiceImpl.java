package org.openiam.bpm.activiti;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
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
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAOImpl;
import org.openiam.idm.srvc.prov.request.dto.ProvisionRequest;
import org.openiam.idm.srvc.prov.request.dto.RequestApprover;
import org.openiam.idm.srvc.prov.request.service.RequestDataService;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.user.dto.DelegationFilterSearch;
import org.openiam.idm.srvc.user.dto.Supervisor;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.thoughtworks.xstream.XStream;

@WebService(endpointInterface = "org.openiam.bpm.activiti.ActivitiService", 
targetNamespace = "urn:idm.openiam.org/bpm/request/service", 
serviceName = "ActivitiService")
public class ActivitiServiceImpl implements ActivitiService {

	private static final Log log = LogFactory.getLog(ActivitiServiceImpl.class);
	
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
	@Qualifier("approverAssociationDAO")
	private ApproverAssociationDAO approverAssociationDao;
	
	@Autowired
	@Qualifier("provRequestService")
	private RequestDataService provRequestService;
	
	@Autowired
	@Qualifier("defaultProvision")
	private ProvisionService provisionService;
	
	@Autowired
	@Qualifier("resourceDAO")
	private ResourceDAO resourceDao;
	
	@Autowired
	@Qualifier("userDAO")
	private UserDAO userDAO;

	@Override
	@WebMethod
	public String sayHello() {
		return "Hello";
	}
	
	//TODO:  This was moved from the SelfService NewHireController to replicate the logic.  Why is it "254"?
	private static final String NEW_HIRE_REQUEST_TYPE = "254";

	@Override
	@WebMethod
	public NewHireResponse initiateNewHireRequest(final NewHireRequest newHireRequest) {
		final NewHireResponse response = new NewHireResponse();

		try {
			final ProvisionRequest provisionRequest = newHireRequest.getProvisionRequest();
			final ProvisionUser provisionUser = newHireRequest.getProvisionUser();
			
			/* get a list of approvers for the new hire request, including information about their organization */
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
	        
	        final List<User> requestApprovers = new LinkedList<User>();
	        if(CollectionUtils.isNotEmpty(newHireRequest.getProvisionRequest().getRequestApprovers())) {
	        	for(final RequestApprover requestApprover : newHireRequest.getProvisionRequest().getRequestApprovers()) {
	        		final User queriedUser = userDAO.findById(requestApprover.getApproverId());
	        		if(queriedUser != null) {
	        			requestApprovers.add(queriedUser);
	        		}
	        	}
	        }
	        
			/* set provision user fields before saving request */
			provisionUser.setUserId(null);
			provisionUser.setCreateDate(new Date());
			provisionUser.setCreatedBy(newHireRequest.getRequestorInformation().getCallerUserId());
			provisionUser.setStatus(UserStatusEnum.PENDING_APPROVAL);

			final Date currentDate = new Date();
			final String xml = new XStream().toXML(provisionUser);
			final Resource newUserResource = resourceDao.findById(NEW_HIRE_REQUEST_TYPE);
			provisionRequest.setRequestXML(xml);
			provisionRequest.setStatus("PENDING");
			provisionRequest.setStatusDate(currentDate);
			provisionRequest.setRequestDate(currentDate);
			provisionRequest.setRequestType(newUserResource.getResourceId());
			provisionRequest.setRequestType(NEW_HIRE_REQUEST_TYPE);
			provisionRequest.setRequestReason(String.format("%s FOR %s %s", newUserResource.getDescription(), provisionUser.getFirstName(), provisionUser.getLastName()));
			
			provRequestService.addRequest(provisionRequest);
			
			/* set a dsearch filter, in case it is needed by the underlying delegate */
			final DelegationFilterSearch delegationFilterSearch = new DelegationFilterSearch();
			delegationFilterSearch.setRole(approverRole);
			delegationFilterSearch.setDelAdmin(applyDelegationFilter);
			delegationFilterSearch.setOrgFilter("%" + userOrg + "%");
			
			final Map<String, Object> variables = new HashMap<String, Object>();
			variables.put(ActivitiConstants.NEW_HIRE_BPM_VAR, newHireRequest);
			variables.put(ActivitiConstants.DELEGATION_FILTER_SEARCH, delegationFilterSearch);
			variables.put(ActivitiConstants.CANDIDATE_USERS, requestApprovers);
			final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("newHireWithApprovalProcess", variables);

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
		return response;
	}

	@Override
	@WebMethod
	public NewHireResponse claimNewHireRequest(final NewHireRequest newHireRequest) {
		final NewHireResponse response = new NewHireResponse();

		try {
			final List<Task> taskList = taskService.createTaskQuery().taskCandidateUser(newHireRequest.getRequestorInformation().getCallerUserId()).list();
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
			
			taskService.claim(potentialTaskToClaim.getId(), newHireRequest.getRequestorInformation().getCallerUserId());
			taskService.setAssignee(potentialTaskToClaim.getId(), newHireRequest.getRequestorInformation().getCallerUserId());
			
			final Date currentDate = new Date();
			final String status = "CLAIMED";
			
			provisionRequest.setStatus(status);
			provisionRequest.setStatusDate(currentDate);
	        Set<RequestApprover> requestApprovers = provisionRequest.getRequestApprovers();
	        for (final RequestApprover requestApprovder  : requestApprovers ) {
	        	if(StringUtils.equalsIgnoreCase(requestApprovder.getApproverId(), newHireRequest.getRequestorInformation().getCallerUserId())) {
	        		requestApprovder.setAction(status);
	            	requestApprovder.setActionDate(currentDate);
	            	requestApprovder.setComment(newHireRequest.getComment());
	        	}
	        }
			
			final String xml = provisionRequest.getRequestXML();
			final ProvisionUser provisionUser = (ProvisionUser)new XStream().fromXML(xml);
			provisionUser.setRequestClientIP(newHireRequest.getRequestorInformation().getIp());
			provisionUser.setRequestorLogin(newHireRequest.getRequestorInformation().getLogin());
			provisionUser.setRequestorDomain(newHireRequest.getRequestorInformation().getDomain());
			provisionRequest.setRequestXML(new XStream().toXML(provisionUser));
			
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

		return response;
	}

	@Override
	@WebMethod
	public NewHireResponse acceptNewHireRequest(final NewHireRequest newHireRequest) {
		final NewHireResponse response = new NewHireResponse();
		try {
			
			final Task assignedTask = getTaskAssignee(newHireRequest);
			
			/* provision the user */
			final ProvisionUser provisionUser = newHireRequest.getProvisionUser();
			provisionUser.setUserId(null);
			provisionUser.setStatus(UserStatusEnum.ACTIVE);
			provisionUser.setRequestClientIP(newHireRequest.getRequestorInformation().getIp());
			provisionUser.setRequestorLogin(newHireRequest.getRequestorInformation().getLogin());
			provisionUser.setRequestorDomain(newHireRequest.getRequestorInformation().getDomain());
	        ProvisionUserResponse resp = provisionService.addUser(provisionUser);
	        final User newUser = resp.getUser();
			
			final Map<String, Object> variables = new HashMap<String, Object>();
			variables.put(ActivitiConstants.IS_NEW_HIRE_APPROVED, Boolean.TRUE);
			variables.put(ActivitiConstants.NEW_HIRE_BPM_VAR, newHireRequest);
			variables.put(ActivitiConstants.NEW_PROVISIONED_USER, newUser);
			taskService.complete(assignedTask.getId(), variables);
				
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
		return response;
	}

	@Override
	@WebMethod
	public NewHireResponse rejectNewHireRequest(final NewHireRequest newHireRequest) {
		final NewHireResponse response = new NewHireResponse();
		try {
			final Task assignedTask = getTaskAssignee(newHireRequest);
			
			final Map<String, Object> variables = new HashMap<String, Object>();
			variables.put(ActivitiConstants.IS_NEW_HIRE_APPROVED, Boolean.FALSE);
			variables.put(ActivitiConstants.NEW_HIRE_BPM_VAR, newHireRequest);
			taskService.complete(assignedTask.getId(), variables);
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
		return response;
	}
	
	private Task getTaskAssignee(final NewHireRequest newHireRequest) throws ActivitiException {
		final List<Task> taskList = taskService.createTaskQuery().taskAssignee(newHireRequest.getRequestorInformation().getCallerUserId()).list();
		if(CollectionUtils.isEmpty(taskList)) {
			throw  new ActivitiException("No tasks for user..");
		}
		
		if(StringUtils.isBlank(newHireRequest.getTaskId())) {
			throw new ActivitiException("No task id specified");
		}
		
		Task assignedTask = null;
		for(final Task task : taskList) {
			if(task.getId().equals(newHireRequest.getTaskId())) {
				assignedTask = task;
				break;
			}
		}
		
		if(assignedTask == null) {
			throw new ActivitiException(String.format("No task '%s' assigned", newHireRequest.getTaskId()));
		}
		return assignedTask;
	}
}
