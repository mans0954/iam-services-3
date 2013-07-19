package org.openiam.bpm.activiti;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.openiam.authmanager.common.model.AuthorizationUser;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.openiam.bpm.activiti.groovy.UserCentricApproverAssociationIdentifier;
import org.openiam.bpm.request.ActivitiClaimRequest;
import org.openiam.bpm.request.ActivitiRequestDecision;
import org.openiam.bpm.request.GenericWorkflowRequest;
import org.openiam.bpm.request.HistorySearchBean;
import org.openiam.bpm.response.NewHireResponse;
import org.openiam.bpm.response.TaskListWrapper;
import org.openiam.bpm.response.TaskWrapper;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.bpm.util.ActivitiRequestType;
import org.openiam.idm.srvc.grp.service.UserGroupDAO;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;
import org.openiam.idm.srvc.meta.exception.PageTemplateException;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
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
import org.openiam.idm.srvc.role.domain.UserRoleEntity;
import org.openiam.idm.srvc.role.service.UserRoleDAO;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.DelegationFilterSearch;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.Supervisor;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.SupervisorDAO;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserProfileService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.resp.ProvisionUserResponse;
import org.openiam.provision.service.ProvisionService;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.thoughtworks.xstream.XStream;

@WebService(endpointInterface = "org.openiam.bpm.activiti.ActivitiService", 
targetNamespace = "urn:idm.openiam.org/bpm/request/service", 
serviceName = "ActivitiService")
public class ActivitiServiceImpl implements ActivitiService, ApplicationContextAware {

	private ApplicationContext ctx;
	
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
	@Qualifier("resourceDAO")
	private ResourceDAO resourceDao;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private SupervisorDAO supervisorDAO;
	
	@Autowired
	private UserProfileService userProfileService;
	
	@Autowired
	private AuthorizationManagerService authManagerService;
	
	@Autowired
	private UserRoleDAO userRoleDAO;
	
	@Autowired
	private UserGroupDAO userGroupDAO;
	
	@Value("${org.openiam.idm.activiti.default.approver.association.resource.name}")
	private String defaultApproverAssociationResourceId;
	
	@Value("${org.openiam.idm.activiti.default.approver.user}")
	private String defaultApproverUserId;
	
	private static final Comparator<Task> taskCreatedTimeComparator = new TaskCreateDateSorter();
	
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;
    
    @Value("${org.openiam.bpm.user.approver.association.script}")
    private String approverAssociationScript;

	@Override
	@WebMethod
	public String sayHello() {
		return "Hello";
	}
	
	@Value("${org.openiam.idm.activiti.new.user.resource.name}")
	private String newUserResourceProtectingName;
	
	@Override
	@WebMethod
	@Transactional
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
	        
	        /* get a list of approvers for this request type */
	        final ResourceEntity protectingResource = resourceDao.findByName(newUserResourceProtectingName);
	        if(protectingResource == null) {
	        	throw new ActivitiException(String.format("Resoruce with name '%s' not found - can't continue", newUserResourceProtectingName));
	        }
	        
	        final List<String> approverAssociationIds = new LinkedList<String>();
        	List<ApproverAssociationEntity> approverAssocationList = approverAssociationDao.getByAssociation(protectingResource.getResourceId(), AssociationType.RESOURCE);
        	if(CollectionUtils.isEmpty(approverAssocationList)) {
        		log.warn(String.format("Can't find approver association for %s %s, using default approver association", AssociationType.RESOURCE, protectingResource.getResourceId()));
				approverAssocationList = getDefaultApproverAssociations();
			}
        	
        	
        	final Set<String> requestApproverIds = new HashSet<String>();
        	if (CollectionUtils.isNotEmpty(approverAssocationList)) {
        		for (final ApproverAssociationEntity approverAssociation : approverAssocationList) {
        			approverAssociationIds.add(approverAssociation.getId());
        			if (approverAssociation != null) {
        				final AssociationType approverType = approverAssociation.getApproverEntityType();
        				final String approverId = approverAssociation.getApproverEntityId();
        				
        				if(approverType != null) {
        					switch(approverType) {
        						case SUPERVISOR:
                    				final Supervisor supVisor = provisionUser.getSupervisor();
                    				if(supVisor != null) {
                    					final String userId = supVisor.getSupervisor().getUserId();
                    					if(StringUtils.isNotBlank(userId)) {
                    						requestApproverIds.add(userId);
                    					}
                    				}
                    				break;
        						case ROLE:
        							if(StringUtils.isNotBlank(approverId)) {
        								final List<String> authUsers = userRoleDAO.getUserIdsInRole(approverId);
        								if(CollectionUtils.isNotEmpty(authUsers)) {
        									requestApproverIds.addAll(authUsers);
        								}
        							}
        							break;
        						case GROUP:
        							if(StringUtils.isNotBlank(approverId)) {
        								final List<String> authUsers = userGroupDAO.getUserIdsInGroup(approverId);
        								if(CollectionUtils.isNotEmpty(authUsers)) {
        									requestApproverIds.addAll(authUsers);
                						}
        							}
        							break;
        						case USER:
        							if(StringUtils.isNotBlank(approverId)) {
        								requestApproverIds.add(approverId);
        							}
        							break;
        						default:
        							break;
        					}
        				}
        			}
        		}
        	}
        	
        	if(CollectionUtils.isEmpty(requestApproverIds)) {
        		log.warn("Could not found any approvers - using default user");
        		requestApproverIds.add(defaultApproverUserId);
        	}
	        
	        if(CollectionUtils.isEmpty(requestApproverIds)) {
	        	throw new BasicDataServiceException(ResponseCode.NO_REQUEST_APPROVERS);
	        }
	        
			/* set provision user fields before saving request */
			provisionUser.setUserId(null);
			provisionUser.setCreateDate(new Date());
			provisionUser.setCreatedBy(request.getRequestorUserId());
			provisionUser.setStatus(UserStatusEnum.PENDING_APPROVAL);

			/* populate the provision request with required values */
			final Date currentDate = new Date();
			final String xml = new XStream().toXML(request);
			provisionRequest.setRequestXML(xml);
			provisionRequest.setStatus("PENDING");
			provisionRequest.setStatusDate(currentDate);
			provisionRequest.setRequestDate(currentDate);
			provisionRequest.setRequestReason(String.format("%s FOR %s %s", protectingResource.getDescription(), provisionUser.getFirstName(), provisionUser.getLastName()));
			provisionRequest.setRequestorId(request.getRequestorUserId());
			
			/* save the request */
			provRequestService.addRequest(provisionRequest);
			
			/* set a dsearch filter, in case it is needed by the underlying delegate */
			
			final ActivitiRequestType requestType = request.getActivitiRequestType();
			final String taskName = String.format("%s Request for %s %s", requestType.getDescription(), provisionUser.getFirstName(), provisionUser.getLastName());
			final String taskDescription = taskName;
			
			/* pass required variables to Activiti, and start the process */
			final Map<String, Object> variables = new HashMap<String, Object>();
			variables.put(ActivitiConstants.APPROVER_ASSOCIATION_IDS, approverAssociationIds);
			variables.put(ActivitiConstants.PROVISION_REQUEST_ID, provisionRequest.getId());
			variables.put(ActivitiConstants.CANDIDATE_USERS_IDS, requestApproverIds);
			variables.put(ActivitiConstants.TASK_NAME, taskName);
			variables.put(ActivitiConstants.TASK_DESCRIPTION, taskDescription);
			variables.put(ActivitiConstants.TASK_OWNER, request.getRequestorUserId());
			final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(requestType.getKey(), variables);

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
	@Transactional
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
			
			/* claim the process, and set the assignee */
			taskService.claim(potentialTaskToClaim.getId(), request.getCallerUserId());
			taskService.setAssignee(potentialTaskToClaim.getId(), request.getCallerUserId());
			
			/* update the provision request */
			/*
			final TaskWrapper taskWrapper = getTask(request.getTaskId());
			final ProvisionRequestEntity provisionRequest = provRequestService.getRequest(taskWrapper.getProvisionRequestId());
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
			*/
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
	@Transactional
	public Response initiateWorkflow(final GenericWorkflowRequest request) {
		final Response response = new Response();
		try {
			if(request == null || request.isEmpty()) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			final Set<String> approverAssociationIds = new HashSet<String>();
			final Set<String> approverUserIds = new HashSet<String>();
			
			if(CollectionUtils.isNotEmpty(request.getCustomApproverIds())) {
				approverUserIds.addAll(request.getCustomApproverIds());
			} else {
				List<ApproverAssociationEntity> approverAssocationList = null;
				if(CollectionUtils.isNotEmpty(request.getCustomApproverAssociationIds())) {
					approverAssocationList = approverAssociationDao.findByIds(request.getCustomApproverAssociationIds());
				} else {
					/* for user target objects, use the groovy script */
					if(AssociationType.USER.equals(request.getAssociationId())) {
						try {
							final UserCentricApproverAssociationIdentifier identifier = 
									(UserCentricApproverAssociationIdentifier)scriptRunner.instantiateClass(null, approverAssociationScript);
							final Map<String, Object> bindingMap = new HashMap<String, Object>();
							bindingMap.put("USER", userDAO.findById(request.getAssociationId()));
							identifier.init(bindingMap);
							approverAssocationList = identifier.getApproverAssociations();
						} catch(Throwable e) {
							log.warn("Can't instantiate groovy class", e);
						}
					} else {
						approverAssocationList = approverAssociationDao.getByAssociation(request.getAssociationId(), request.getAssociationType());
					}
				}
				
				if(CollectionUtils.isEmpty(approverAssocationList)) {
					log.warn(String.format("Can't find approver association for %s %s, using default approver association", request.getAssociationType(), request.getAssociationId()));
					approverAssocationList = getDefaultApproverAssociations();
				}
				if(CollectionUtils.isNotEmpty(approverAssocationList)) {
					for(final ApproverAssociationEntity entity : approverAssocationList) {
						approverAssociationIds.add(entity.getId());
						if(entity.getApproverEntityType() != null && StringUtils.isNotBlank(entity.getApproverEntityId())) {
							final String approverId = entity.getApproverEntityId();
							switch(entity.getApproverEntityType()) {
								case GROUP:
									final List<String> groupUsers = userGroupDAO.getUserIdsInGroup(approverId);
									if(CollectionUtils.isNotEmpty(groupUsers)) {
		    							approverUserIds.addAll(groupUsers);
		    						}
									break;
								case ROLE:
									final List<String> roleUsers = userRoleDAO.getUserIdsInRole(approverId);
									if(CollectionUtils.isNotEmpty(roleUsers)) {
										approverUserIds.addAll(roleUsers);
									}
									break;
								case USER:
									approverUserIds.add(approverId);
									break;
								case SUPERVISOR: /* assume association ID is a user */
									final List<SupervisorEntity> supervisors = supervisorDAO.findSupervisors(request.getAssociationId());
									if(CollectionUtils.isNotEmpty(supervisors)) {
										for(final SupervisorEntity supervisor : supervisors) {
											if(supervisor != null && supervisor.getEmployee() != null) {
												approverUserIds.add(supervisor.getEmployee().getUserId());
											}
										}
									}
									break;
								default:
									break;
							}
						}
					}
				}
				
				if(CollectionUtils.isEmpty(approverUserIds)) {
					log.warn("Could not found any approvers - using default user");
	        		approverUserIds.add(defaultApproverUserId);
	        	}
			}
			
			final Map<String, Object> variables = new HashMap<String, Object>();
			variables.put(ActivitiConstants.APPROVER_ASSOCIATION_IDS, approverAssociationIds);
			variables.put(ActivitiConstants.CANDIDATE_USERS_IDS, approverUserIds);
			variables.put(ActivitiConstants.TASK_NAME, request.getName());
			variables.put(ActivitiConstants.TASK_DESCRIPTION, request.getDescription());
			variables.put(ActivitiConstants.TASK_OWNER, request.getCallerUserId());
			variables.put(ActivitiConstants.ASSOCIATION_ID, request.getAssociationId());
			if(request.getParameters() != null) {
				variables.putAll(request.getParameters());
			}
			runtimeService.startProcessInstanceByKey(request.getActivitiRequestType(), variables);

			response.setStatus(ResponseStatus.SUCCESS);
		} catch(BasicDataServiceException e) {
			log.info("Could not initialize task", e);
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
	@Transactional
	public Response makeDecision(final ActivitiRequestDecision request) {
		final Response response = new Response();
		try {
			
			final Task assignedTask = getTaskAssignee(request);
		
        	/* complete the Task in Activiti, passing required parameters */
        	final Map<String, Object> variables = new HashMap<String, Object>();
        	variables.put(ActivitiConstants.COMMENT, request.getComment());
        	variables.put(ActivitiConstants.IS_TASK_APPROVED, request.isAccepted());
        	variables.put(ActivitiConstants.EXECUTOR_ID, request.getCallerUserId());
        	if(request.getCustomVariables() != null) {
        		variables.putAll(request.getCustomVariables());
        	}
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
	
	private Task getTaskAssignee(final String taskId, final String userId) {
		final List<Task> taskList = taskService.createTaskQuery().taskId(taskId).taskAssignee(userId).list();
		if(CollectionUtils.isEmpty(taskList)) {
			throw  new ActivitiException("No tasks for user..");
		}
		
		return taskList.get(0);
	}
	
	private Task getTaskAssignee(final ActivitiRequestDecision newHireRequest) throws ActivitiException {
		return getTaskAssignee(newHireRequest.getTaskId(), newHireRequest.getCallerUserId());
	}

	@Override
	@WebMethod
	@Transactional
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
	@Transactional
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

	@Override
	@Transactional
	public List<TaskWrapper> getHistory(final HistorySearchBean searchBean, final int from, final int size) {
		final HistoricTaskInstanceQuery query = getHistoryQuery(searchBean);
		
		final List<HistoricTaskInstance> historicTaskInstances = query.listPage(from * size, size);
		final List<TaskWrapper> retVal = new LinkedList<TaskWrapper>();
		if(CollectionUtils.isNotEmpty(historicTaskInstances)) {
			for(final HistoricTaskInstance historyInstance : historicTaskInstances) {
				retVal.add(new TaskWrapper(historyInstance, runtimeService));
			}
		}
		return retVal;
	}
	
	@Override
	@Transactional
	public int count(final HistorySearchBean searchBean) {
		final HistoricTaskInstanceQuery query = getHistoryQuery(searchBean);
		return Long.valueOf(query.count()).intValue();
	}
	
	private HistoricTaskInstanceQuery getHistoryQuery(final HistorySearchBean searchBean) {
		final HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery();
		if(StringUtils.isNotBlank(searchBean.getAssigneeId())) {
			query.taskAssignee(searchBean.getAssigneeId());
		}
		if(searchBean.isCompleted() != null) {
			if(Boolean.TRUE.equals(searchBean.isCompleted())) {
				query.finished();
			} else {
				query.unfinished();
			}
		}
		
		if(searchBean.getDueAfter() != null) {
			query.taskDueAfter(searchBean.getDueAfter());
		}
		
		if(searchBean.getDueBefore() != null) {
			query.taskDueBefore(searchBean.getDueBefore());
		}
		
		if(StringUtils.isNotBlank(searchBean.getTaskName())) {
			query.taskNameLike(searchBean.getTaskName());
		}
		
		if(StringUtils.isNotBlank(searchBean.getTaskDescription())) {
			query.taskOwner(searchBean.getTaskDescription());
		}
		
		if(StringUtils.isNotBlank(searchBean.getTaskOwnerId())) {
			query.taskOwner(searchBean.getTaskOwnerId());
		}
		query.orderByHistoricTaskInstanceEndTime();
		query.desc();
		return query;
	}

	@Override
	@Transactional
	public Response deleteTask(String taskId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			taskService.deleteTask(taskId, true);
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
	@Transactional
	public Response deleteTaskForUser(String taskId, String userId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			final Task task = getTaskAssignee(taskId, userId);
			taskService.deleteTask(task.getId(), true);
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
	
	private List<ApproverAssociationEntity> getDefaultApproverAssociations() {
		return approverAssociationDao.getByAssociation(defaultApproverAssociationResourceId, AssociationType.RESOURCE);
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;
	}
}
