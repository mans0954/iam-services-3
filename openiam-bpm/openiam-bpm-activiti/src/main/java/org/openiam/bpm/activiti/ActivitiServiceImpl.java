package org.openiam.bpm.activiti;

import java.util.Collections;
import java.util.Comparator;
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
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.openiam.bpm.request.ActivitiClaimRequest;
import org.openiam.bpm.request.ActivitiRequestDecision;
import org.openiam.bpm.response.NewHireResponse;
import org.openiam.bpm.response.TaskListWrapper;
import org.openiam.bpm.response.TaskWrapper;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;
import org.openiam.idm.srvc.meta.exception.PageTemplateException;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAOImpl;
import org.openiam.idm.srvc.prov.request.dto.ProvisionRequest;
import org.openiam.idm.srvc.prov.request.domain.ProvisionRequestEntity;
import org.openiam.idm.srvc.prov.request.domain.RequestApproverEntity;
import org.openiam.idm.srvc.prov.request.service.ProvisionRequestDAO;
import org.openiam.idm.srvc.prov.request.service.RequestDataService;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.user.dto.DelegationFilterSearch;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.Supervisor;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserProfileService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
	private UserProfileService userProfileService;
	
	private static final Comparator<Task> taskCreatedTimeComparator = new TaskCreateDateSorter();

	@Override
	@WebMethod
	public String sayHello() {
		return "Hello";
	}
	
	//TODO:  This was moved from the SelfService NewHireController to replicate the logic.  Why is it "254"?
	private static final String NEW_HIRE_REQUEST_TYPE = "254";

	@Override
	@WebMethod
	public SaveTemplateProfileResponse initiateNewHireRequest(final NewUserProfileRequestModel request) {
		final SaveTemplateProfileResponse response = new SaveTemplateProfileResponse();

		try {
			if(request == null || request.getActivitiRequestType() == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			/* throws exception if invalid - caught in try/catch */
			userProfileService.validate(request);
			
			final ProvisionRequestEntity provisionRequest = new ProvisionRequestEntity();
			final User provisionUser = request.getUser();
			
			/* get a list of approvers for the new hire request, including information about their organization */
	        String approverRole = null;
	        String userOrg = null;
	        int applyDelegationFilter = 0;
	        
	        /* get a list of approvers for this request type */
			final List<ApproverAssociationEntity> approverAssocationList = approverAssociationDao.findApproversByRequestType(NEW_HIRE_REQUEST_TYPE, 1);
	        if (CollectionUtils.isNotEmpty(approverAssocationList)) {
	            for (final ApproverAssociationEntity approverAssociation : approverAssocationList) {
	                String approverType = null;
	                String approverId = null;
	                if (approverAssociation != null) {
	                    approverType = approverAssociation.getAssociationType();
	
	                    /* if the association type is a Supervisor, the assigned Supervisor to the User is the approver */
	                    if(StringUtils.equalsIgnoreCase(approverAssociation.getAssociationType(), "supervisor")) {
	                    	final Supervisor supVisor = provisionUser.getSupervisor();
	                        approverId = supVisor.getSupervisor().getUserId();
	                        
	                    /* if the association type is a Role, use the approver Role ID */
	                    } else if(StringUtils.equalsIgnoreCase(approverAssociation.getAssociationType(), "role")) {
	                        approverId = approverAssociation.getApproverRoleId();
	
	                        approverRole = approverAssociation.getApproverRoleId();
	                        if (approverAssociation.getApplyDelegationFilter() != null) {
	                            applyDelegationFilter = approverAssociation.getApplyDelegationFilter().intValue();
	                        }
	                        if (StringUtils.isNotBlank(provisionUser.getCompanyId())) {
	                            userOrg = provisionUser.getCompanyId();
	                        }
	
	                    /* otherwise, use the approver id of the association */
	                    } else {
	                        approverId = approverAssociation.getApproverUserId();
	                    }
	
	                    /* add the approver to the list */
	                    final RequestApproverEntity reqApprover = new RequestApproverEntity(approverId, approverAssociation.getApproverLevel(), approverAssociation.getAssociationType(), "PENDING");
	                    reqApprover.setApproverType(approverType);
	                    provisionRequest.addRequestApprover(reqApprover);
	                }
	
	            }
	        }
	        
	        /* based on the roleapprovers, add teh users as candidate approvers */
	        final List<String> requestApproverIds = new LinkedList<String>();
	        if(CollectionUtils.isNotEmpty(provisionRequest.getRequestApprovers())) {
	        	for(final RequestApproverEntity requestApprover : provisionRequest.getRequestApprovers()) {
	        		requestApproverIds.add(requestApprover.getApproverId());
	        	}
	        }
	        
			/* set provision user fields before saving request */
			provisionUser.setUserId(null);
			provisionUser.setCreateDate(new Date());
			provisionUser.setCreatedBy(request.getRequestorUserId());
			provisionUser.setStatus(UserStatusEnum.PENDING_APPROVAL);

			/* populate the provision request with required values */
			final Date currentDate = new Date();
			final String xml = new XStream().toXML(request);
			final ResourceEntity newUserResource = resourceDao.findById(NEW_HIRE_REQUEST_TYPE);
			provisionRequest.setRequestXML(xml);
			provisionRequest.setStatus("PENDING");
			provisionRequest.setStatusDate(currentDate);
			provisionRequest.setRequestDate(currentDate);
			provisionRequest.setRequestType(newUserResource.getResourceId());
			provisionRequest.setRequestType(NEW_HIRE_REQUEST_TYPE);
			provisionRequest.setRequestReason(String.format("%s FOR %s %s", newUserResource.getDescription(), provisionUser.getFirstName(), provisionUser.getLastName()));
			provisionRequest.setRequestorId(request.getRequestorUserId());
			if(StringUtils.isNotBlank(provisionUser.getCompanyId())) {
				provisionRequest.setRequestForOrgId(provisionUser.getCompanyId());
			}
			
			/* save the request */
			provRequestService.addRequest(provisionRequest);
			
			/* set a dsearch filter, in case it is needed by the underlying delegate */
			final DelegationFilterSearch delegationFilterSearch = new DelegationFilterSearch();
			delegationFilterSearch.setRole(approverRole);
			delegationFilterSearch.setDelAdmin(applyDelegationFilter);
			delegationFilterSearch.setOrgFilter("%" + userOrg + "%");
			
			/* pass required variables to Activiti, and start the process */
			final Map<String, Object> variables = new HashMap<String, Object>();
			variables.put(ActivitiConstants.PROVISION_REQUEST_ID, provisionRequest.getId());
			variables.put(ActivitiConstants.DELEGATION_FILTER_SEARCH, delegationFilterSearch);
			variables.put(ActivitiConstants.CANDIDATE_USERS_IDS, requestApproverIds);
			variables.put(ActivitiConstants.TASK_NAME, String.format("New Hire Request for %s %s", provisionUser.getFirstName(), provisionUser.getLastName()));
			variables.put(ActivitiConstants.TASK_OWNER, request.getRequestorUserId());
			final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(request.getActivitiRequestType().getKey(), variables);

			response.setStatus(ResponseStatus.SUCCESS);
		} catch (PageTemplateException e) {
			response.setCurrentValue(e.getCurrentValue());
			response.setElementName(e.getElementName());
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
		} catch(BasicDataServiceException e) {
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
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
	public Response claimRequest(final ActivitiClaimRequest request) {
		final Response response = new Response();

		try {
			final List<Task> taskList = taskService.createTaskQuery().taskCandidateUser(request.getCallerUserId()).list();
			if(CollectionUtils.isEmpty(taskList)) {
				throw new ActivitiException("No Candidate Task available");
			}
			
			if(StringUtils.isBlank(request.getTaskId())) {
				throw new ActivitiException("No Task specified");
			}
			
			Task potentialTaskToClaim = null;
			for(final Task task : taskList) {
				if(task.getId().equals(request.getTaskId())) {
					potentialTaskToClaim = task;
					break;
				}
			}
			
			if(potentialTaskToClaim == null) {
				throw new ActivitiException(String.format("Task with ID: '%s' not assigned to user", request.getTaskId()));
			}
			
			final TaskWrapper taskWrapper = getTask(request.getTaskId());
			final ProvisionRequestEntity provisionRequest = provRequestService.getRequest(taskWrapper.getProvisionRequestId());
			
			/* claim the process, and set the assignee */
			taskService.claim(potentialTaskToClaim.getId(), request.getCallerUserId());
			taskService.setAssignee(potentialTaskToClaim.getId(), request.getCallerUserId());
			
			/* update the provision request */
			final Date currentDate = new Date();
			final String status = "CLAIMED";
			
			provisionRequest.setStatus(status);
			provisionRequest.setStatusDate(currentDate);
	        final Set<RequestApproverEntity> requestApprovers = provisionRequest.getRequestApprovers();
	        for (final RequestApproverEntity requestApprovder  : requestApprovers ) {
	        	if(StringUtils.equalsIgnoreCase(requestApprovder.getApproverId(), request.getCallerUserId())) {
	        		requestApprovder.setAction(status);
	            	requestApprovder.setActionDate(currentDate);
	        	}
	        }
			
			final String xml = provisionRequest.getRequestXML();
			final ProvisionUser provisionUser = (ProvisionUser)new XStream().fromXML(xml);
			provisionUser.setRequestClientIP(request.getRequestClientIP());
			provisionUser.setRequestorLogin(request.getRequestorLogin());
			provisionUser.setRequestorDomain(request.getRequestorDomain());
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
	public Response acceptRequest(final ActivitiRequestDecision request) {
		final Response response = new Response();
		try {
			
			final Task assignedTask = getTaskAssignee(request);
			
			/* update the provision request */
			/*
			final String status = "APPROVED";
			final Date currentDate = new Date();
			final TaskWrapper taskWrapper = getTask(request.getTaskId());
			final ProvisionRequestEntity provisionRequest = provRequestService.getRequest(taskWrapper.getProvisionRequestId());
			provisionRequest.setStatusDate(currentDate);
			provisionRequest.setStatus(status);
	        final Set<RequestApproverEntity> requestApprovers = provisionRequest.getRequestApprovers();
	        for (final RequestApproverEntity requestApprovder  : requestApprovers ) {
	        	if(StringUtils.equalsIgnoreCase(requestApprovder.getApproverId(), request.getCallerUserId())) {
	        		requestApprovder.setAction(status);
	            	requestApprovder.setActionDate(currentDate);
	            	requestApprovder.setComment(request.getComment());
	        	}
	        }
			
			final ProvisionUser provisionUser = (ProvisionUser)new XStream().fromXML(provisionRequest.getRequestXML());
			provisionUser.setUserId(null);
			provisionUser.setStatus(UserStatusEnum.PENDING_INITIAL_LOGIN);
			provisionUser.setRequestClientIP(request.getRequestClientIP());
			provisionUser.setRequestorLogin(request.getRequestorLogin());
			provisionUser.setRequestorDomain(request.getRequestorDomain());
			provisionRequest.setRequestXML(new XStream().toXML(provisionUser));
			provRequestService.updateRequest(provisionRequest);
			*/
		
        	/* complete the Task in Activiti, passing required parameters */
        	final Map<String, Object> variables = new HashMap<String, Object>();
        	variables.put(ActivitiConstants.IS_TASK_APPROVED, Boolean.TRUE);
        	variables.put(ActivitiConstants.NEW_HIRE_EXECUTOR_ID, request.getCallerUserId());
        	taskService.complete(assignedTask.getId(), variables);	
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
	public Response rejectRequest(final ActivitiRequestDecision request) {
		final Response response = new Response();
		try {
			/*
			final String status = "REJECTED";
			final Date currentDate = new Date();
			*/
			final Task assignedTask = getTaskAssignee(request);
			
			/* update the provision request */
			/*
			final TaskWrapper taskWrapper = getTask(request.getTaskId());
			final ProvisionRequestEntity provisionRequest = provRequestService.getRequest(taskWrapper.getProvisionRequestId());
			provisionRequest.setStatusDate(currentDate);
			provisionRequest.setStatus(status);
	        final Set<RequestApproverEntity> requestApprovers = provisionRequest.getRequestApprovers();
	        for (final RequestApproverEntity requestApprovder  : requestApprovers ) {
	        	if(StringUtils.equalsIgnoreCase(requestApprovder.getApproverId(), request.getCallerUserId())) {
	        		requestApprovder.setAction(status);
	            	requestApprovder.setActionDate(currentDate);
	            	requestApprovder.setComment(request.getComment());
	        	}
	        }
	        provRequestService.updateRequest(provisionRequest);
	        */
			
	        /* complete the Task */
			final Map<String, Object> variables = new HashMap<String, Object>();
			variables.put(ActivitiConstants.IS_TASK_APPROVED, Boolean.FALSE);
			variables.put(ActivitiConstants.NEW_HIRE_EXECUTOR_ID, request.getCallerUserId());
			taskService.complete(assignedTask.getId(), variables);
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
	
	private Task getTaskAssignee(final ActivitiRequestDecision newHireRequest) throws ActivitiException {
		final List<Task> taskList = taskService.createTaskQuery().taskAssignee(newHireRequest.getCallerUserId()).list();
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

	@Override
	@WebMethod
	public TaskListWrapper getTasksForUser(String userId) {
		final TaskListWrapper taskListWrapper = new TaskListWrapper();
		final List<Task> assignedTasks = taskService.createTaskQuery().taskAssignee(userId).list();
		final List<Task> candidateTasks = taskService.createTaskQuery().taskCandidateUser(userId).list();
		Collections.sort(assignedTasks, taskCreatedTimeComparator);
		Collections.sort(candidateTasks, taskCreatedTimeComparator);
		taskListWrapper.addAssignedTasks(assignedTasks, runtimeService);
		taskListWrapper.addCandidateTasks(candidateTasks, runtimeService);
		return taskListWrapper;
	}

	@Override
	@WebMethod
	public TaskWrapper getTask(String taskId) {
		TaskWrapper retVal = null;
		final List<Task> taskList = taskService.createTaskQuery().taskId(taskId).list();
		if(CollectionUtils.isNotEmpty(taskList)) {
			retVal = new TaskWrapper(taskList.get(0), runtimeService);
		}
		return retVal;
	}
	
	private static final class TaskCreateDateSorter implements Comparator<Task> {

		@Override
		public int compare(Task o1, Task o2) {
			try {
				return o2.getCreateTime().compareTo(o1.getCreateTime());
			} catch(Throwable e) { /* can't happen, but... */
				log.warn("Sorting problem", e);
				return 0;
			}
		}
		
	}
}
