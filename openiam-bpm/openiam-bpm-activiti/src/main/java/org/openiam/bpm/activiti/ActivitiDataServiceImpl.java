package org.openiam.bpm.activiti;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.activiti.model.dto.TaskSearchBean;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.ResponseCode;
import org.openiam.bpm.activiti.delegate.core.ActivitiHelper;
import org.openiam.bpm.activiti.groovy.DefaultEditUserApproverAssociationIdentifier;
import org.openiam.bpm.activiti.groovy.DefaultGenericWorkflowRequestApproverAssociationIdentifier;
import org.openiam.bpm.activiti.groovy.DefaultNewHireRequestApproverAssociationIdentifier;
import org.openiam.bpm.activiti.model.ActivitiJSONStringWrapper;
import org.openiam.bpm.dto.AbstractWorkflowResponse;
import org.openiam.bpm.dto.BasicWorkflowResponse;
import org.openiam.base.request.ActivitiClaimRequest;
import org.openiam.base.request.ActivitiRequestDecision;
import org.openiam.base.request.GenericWorkflowRequest;
import org.openiam.activiti.model.dto.HistorySearchBean;
import org.openiam.base.response.ActivitiHistoricDetail;
import org.openiam.base.response.ActivitiJSONField;
import org.openiam.base.response.ActivitiUserField;
import org.openiam.base.response.TaskHistoryWrapper;
import org.openiam.base.response.TaskListWrapper;
import org.openiam.base.response.TaskWrapper;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.bpm.util.ActivitiRequestType;
import org.openiam.bpm.utils.ActivitiUtils;
import org.openiam.concurrent.AuditLogHolder;
import org.openiam.dozer.converter.AddressDozerConverter;
import org.openiam.dozer.converter.EmailAddressDozerConverter;
import org.openiam.dozer.converter.PhoneDozerConverter;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.access.service.AccessRightDAO;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.constant.AuditSource;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;
import org.openiam.idm.srvc.meta.service.MetadataElementTemplateService;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.service.UserProfileService;
import org.openiam.idm.util.CustomJacksonMapper;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.AuditLogHelper;
import org.openiam.util.SpringSecurityHelper;
import org.openiam.validator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;


@Component("activitiDataService")
public class ActivitiDataServiceImpl extends AbstractBaseService implements ActivitiDataService {

	private static final Log log = LogFactory.getLog(ActivitiDataServiceImpl.class);

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
	private LoginDataService loginService;

	@Autowired
	@Qualifier("activitiHistoryService")
	private HistoryService historyService;

	@Autowired
	private CustomJacksonMapper jacksonMapper;

	@Autowired
	private UserProfileService userProfileService;
	@Autowired
	private UserDataService userDataService;

	@Autowired
	private AuthorizationManagerService authManagerService;

	@Value("${org.openiam.activiti.membership.approver.association.groovy.script}")
	private String membershipApproverAssociationGroovyScript;

	@Value("${org.openiam.activiti.edit.user.approver.association.groovy.script}")
	private String editUserApproverAssociationGroovyScript;

	@Value("${org.openiam.activiti.new.user.approver.association.groovy.script}")
	private String newUserApproverAssociationGroovyScript;

	@Value("${org.openiam.idm.activiti.merge.custom.approver.with.approver.associations}")
	protected Boolean mergeCustomApproverIdsWithApproverAssociations;

	@Autowired
	@Qualifier("configurableGroovyScriptEngine")
	protected ScriptIntegration scriptRunner;

	@Value("${org.openiam.ui.admin.right.id}")
	private String adminRightId;

	@Autowired
	private MetadataElementTemplateService pageTemplateService;

	@Autowired
	private SysConfiguration sysConfiguration;

	@Autowired
	private ActivitiHelper activitiHelper;

	private static final Comparator<Task> taskCreatedTimeComparator = new TaskCreateDateSorter();

	@Autowired
	@Qualifier("entityValidator")
	private EntityValidator entityValidator;

	@Autowired
	private UserDozerConverter userDozerConverter;

	@Autowired
	private EmailAddressDozerConverter emailDozerConverter;

	@Autowired
	private AddressDozerConverter addressDozerConverter;

	@Autowired
	private PhoneDozerConverter phoneDozerConverter;

	@Autowired
	private ResourceTypeDAO resourceTypeDAO;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private AccessRightDAO accessRightDAO;

	@Value("${org.openiam.workflow.resource.type}")
	private String workflowResourceType;

	@Autowired
	private AuditLogHelper auditLogHelper;

	@Autowired
	private AuditLogService auditLogService;

	@Override
	public String sayHello() {
		return "Hello";
	}

	private ResourceEntity createAndSaveWorkflowResource(final String name) {
		final UserEntity user = userDataService.getUser(SpringSecurityHelper.getRequestorUserId());
		final ResourceEntity workflowMasterResource = resourceService.findResourceById(propertyValueSweeper.getString("org.openiam.workflow.master.resource"));

		final ResourceEntity resource = new ResourceEntity();
		resource.setResourceType(resourceTypeDAO.findById(workflowResourceType));
		resource.setName(String.format("%s_%s", name, System.currentTimeMillis()));
		resource.setCoorelatedName(String.format("Resource protecting workflow '%s'", name));
		resource.addUser(user, accessRightDAO.findAll(), null, null);
		resource.addChildResource(workflowMasterResource, null, null, null);

		resourceService.save(resource);
		return resource;
	}

	@Override
	@Transactional
	public SaveTemplateProfileResponse initiateNewHireRequest(final NewUserProfileRequestModel request) throws BasicDataServiceException {
		log.info("Initializing workflow");
		final SaveTemplateProfileResponse response = new SaveTemplateProfileResponse();
		IdmAuditLogEntity idmAuditLog = AuditLogHolder.getInstance().getEvent();

		idmAuditLog.setAction(AuditAction.NEW_USER_WORKFLOW.value());
		idmAuditLog.setBaseObject(request);
		idmAuditLog.setRequestorUserId(SpringSecurityHelper.getRequestorUserId());
		idmAuditLog.setSource(AuditSource.WORKFLOW.value());
		idmAuditLog.addAttributeAsJson(AuditAttributeName.REQUEST, request, jacksonMapper);

		if(request == null || request.getActivitiRequestType() == null) {
			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
		}

		/* throws exception if invalid - caught in try/catch */
		userProfileService.validate(request);

		validateUserRequest(request);

		final Map<String, Object> bindingMap = new HashMap<String, Object>();
		bindingMap.put("REQUEST", request);
		bindingMap.put("BUILDER", idmAuditLog);

		DefaultNewHireRequestApproverAssociationIdentifier identifier = null;
		try {
			identifier = (DefaultNewHireRequestApproverAssociationIdentifier)
					scriptRunner.instantiateClass(bindingMap, newUserApproverAssociationGroovyScript);
			if(identifier == null) {
				throw new Exception("Did not instantiate script - was null");
			}
		} catch(Throwable e) {
			log.error(String.format("Can't instantiate '%s' - using default", newUserApproverAssociationGroovyScript), e);
			identifier = new DefaultNewHireRequestApproverAssociationIdentifier();
		}

		identifier.init(bindingMap);

		List<String> approverAssociationIds = null;
		List<String> approverUserIds = null;
		if(CollectionUtils.isNotEmpty(request.getCustomApproverIds())) {
			approverUserIds = request.getCustomApproverIds();
		} else {
			approverAssociationIds = identifier.getApproverAssociationIds();
			approverUserIds = identifier.getApproverIds();
		}

		idmAuditLog.addAttributeAsJson(AuditAttributeName.APPROVER_ASSOCIATIONS, approverAssociationIds, jacksonMapper);
		idmAuditLog.addAttributeAsJson(AuditAttributeName.REQUEST_APPROVER_IDS, approverUserIds, jacksonMapper);

		//if(CollectionUtils.isEmpty(approverAssociationIds) && CollectionUtils.isEmpty(approverUserIds)) {
		//	throw new BasicDataServiceException(ResponseCode.NO_REQUEST_APPROVERS);
		//}

		/* populate the provision request with required values */

		final ActivitiRequestType requestType = request.getActivitiRequestType();
		final String taskName = String.format("%s Request for %s", requestType.getDescription(), request.getUser().getDisplayName());
		final String taskDescription = taskName;

		/* pass required variables to Activiti, and start the process */
		final List<Object> approverCardinatlity = new LinkedList<Object>();
		if(CollectionUtils.isNotEmpty(approverAssociationIds)) {
			approverCardinatlity.addAll(approverAssociationIds);
		} else {
			approverCardinatlity.add(approverUserIds);
		}

		final ResourceEntity resource = createAndSaveWorkflowResource(taskName);

		final Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(ActivitiConstants.WORKFLOW_RESOURCE_ID.getName(), resource.getId());
		variables.put(ActivitiConstants.OPENIAM_VERSION.getName(), sysConfiguration.getProjectVersion());
		variables.put(ActivitiConstants.APPROVER_CARDINALTITY.getName(), approverCardinatlity);
		variables.put(ActivitiConstants.APPROVER_ASSOCIATION_IDS.getName(), approverAssociationIds);
		try {
			variables.put(ActivitiConstants.REQUEST.getName(), new ActivitiJSONStringWrapper(jacksonMapper.writeValueAsString(request)));
		} catch (JsonProcessingException e) {
			throw new BasicDataServiceException(ResponseCode.INTERNAL_ERROR, "Cannot serialize request to JSON");
		}
		variables.put(ActivitiConstants.TASK_NAME.getName(), taskName);
		variables.put(ActivitiConstants.TASK_DESCRIPTION.getName(), taskDescription);
		variables.put(ActivitiConstants.REQUESTOR.getName(), SpringSecurityHelper.getRequestorUserId());
		variables.put(ActivitiConstants.WORKFLOW_NAME.getName(), requestType.getKey());
		variables.put(ActivitiConstants.REQUESTOR_NAME.getName(), SpringSecurityHelper.getRequestorUserId());
		if(identifier.getCustomActivitiAttributes() != null) {
			variables.putAll(identifier.getCustomActivitiAttributes());
		}

		for(Map.Entry<String,Object> varEntry : variables.entrySet()) {
			idmAuditLog.put(varEntry.getKey(), (varEntry.getValue() != null) ? varEntry.getValue().toString() : null);
		}

		//idmAuditLog = auditLogService.save(idmAuditLog);
		idmAuditLog = auditLogHelper.save(idmAuditLog);
		AuditLogHolder.getInstance().setEvent(idmAuditLog);
		variables.put(ActivitiConstants.AUDIT_LOG_ID.getName(), idmAuditLog.getId());

		final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(requestType.getKey(), variables);
		resource.setReferenceId(processInstance.getId());
		resourceService.save(resource);
		populate(response, processInstance, resource, approverAssociationIds, approverUserIds, SpringSecurityHelper.getRequestorUserId());

		idmAuditLog = auditLogService.findById(idmAuditLog.getId());
		idmAuditLog.setTargetTask(processInstance.getId(), taskName);
		return response;
	}

	private void populate(final AbstractWorkflowResponse response,
						  final ProcessInstance processInstance,
						  final ResourceEntity resource,
						  final List<String> approverAssociationIds,
						  final List<String> approverUserIds,
						  final String taskOwner) {
		response.setActivityId(processInstance.getActivityId());
		response.setBusinessKey(processInstance.getBusinessKey());
		response.setDeploymentId(processInstance.getDeploymentId());
		response.setId(processInstance.getId());
		response.setName(processInstance.getName());
		response.setParentId(processInstance.getParentId());
		response.setProcessDefinitionId(processInstance.getProcessDefinitionId());
		response.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
		response.setProcessDefinitionName(processInstance.getProcessDefinitionName());
		response.setProcessDefinitionVersion(processInstance.getProcessDefinitionVersion());
		response.setProcessInstanceId(processInstance.getProcessInstanceId());
		response.setTenantId(processInstance.getTenantId());
		response.setProtectingResourceId(resource.getId());
		response.setApproverAssociationIds(approverAssociationIds);
		response.setApproverUserIds(approverUserIds);
		response.addProcessOwner(taskOwner);
	}

	@Override
	@Transactional
	public void claimRequest(final ActivitiClaimRequest request) throws BasicDataServiceException {
		final IdmAuditLogEntity idmAuditLog = AuditLogHolder.getInstance().getEvent();
		idmAuditLog.setAction(AuditAction.CLAIM_REQUEST.value());
		idmAuditLog.setBaseRequest(request);
		idmAuditLog.setRequestorUserId(request.getRequesterId());
		idmAuditLog.setSource(AuditSource.WORKFLOW.value());

//		final Response response = new Response();
			String parentAuditLogId = null;
			idmAuditLog.addAttributeAsJson(AuditAttributeName.REQUEST, request, jacksonMapper);
		try {
			final List<Task> taskList = taskService.createTaskQuery().taskCandidateUser(request.getRequesterId()).list();
			if (CollectionUtils.isEmpty(taskList)) {
				throw new BasicDataServiceException(ResponseCode.USER_STATUS, "No Candidate Task available");
			}

			if (StringUtils.isBlank(request.getTaskId())) {
				throw new BasicDataServiceException(ResponseCode.USER_STATUS, "No Task specified");
			}

			Task potentialTaskToClaim = null;
			for (final Task task : taskList) {
				if (task.getId().equals(request.getTaskId())) {
					potentialTaskToClaim = task;
					break;
				}
			}

			if (potentialTaskToClaim == null) {
				throw new BasicDataServiceException(ResponseCode.USER_STATUS, String.format("Task with ID: '%s' not assigned to user", request.getTaskId()));
			}

			idmAuditLog.setTargetTask(potentialTaskToClaim.getId(), potentialTaskToClaim.getName());
		/* claim the process, and set the assignee */
			taskService.claim(potentialTaskToClaim.getId(), request.getRequesterId());
			taskService.setAssignee(potentialTaskToClaim.getId(), request.getRequesterId());

			taskService.setVariableLocal(potentialTaskToClaim.getId(), ActivitiConstants.ASSIGNEE_ID.getName(), request.getRequesterId());
			final Object auditLogId = taskService.getVariable(potentialTaskToClaim.getId(), ActivitiConstants.AUDIT_LOG_ID.getName());
			if (auditLogId != null && auditLogId instanceof String) {
				parentAuditLogId = (String) auditLogId;
			}
		} finally {
			updateParentAuditLog(parentAuditLogId, idmAuditLog);
		}
	}

	@Override
	@Transactional
	public SaveTemplateProfileResponse initiateEditUserWorkflow(final UserProfileRequestModel request) throws BasicDataServiceException{
		final SaveTemplateProfileResponse response = new SaveTemplateProfileResponse();

		IdmAuditLogEntity idmAuditLog = AuditLogHolder.getInstance().getEvent();
		idmAuditLog.setAction(AuditAction.EDIT_USER_WORKFLOW.value());
		idmAuditLog.setBaseObject(request);
		idmAuditLog.setRequestorUserId(SpringSecurityHelper.getRequestorUserId());
		idmAuditLog.setSource(AuditSource.WORKFLOW.value());
		idmAuditLog.addAttributeAsJson(AuditAttributeName.REQUEST, request, jacksonMapper);

		pageTemplateService.validate(request);
		validateUserRequest(request);

		final String description = String.format("Edit User %s", request.getUser().getDisplayName());

		final Map<String, Object> bindingMap = new HashMap<String, Object>();
		bindingMap.put("REQUEST", request);
		bindingMap.put("BUILDER", idmAuditLog);

		DefaultEditUserApproverAssociationIdentifier identifier = null;
		try {
			identifier = (DefaultEditUserApproverAssociationIdentifier)
					scriptRunner.instantiateClass(bindingMap, editUserApproverAssociationGroovyScript);
			if(identifier == null) {
				throw new Exception("Did not instantiate script - was null");
			}
		} catch(Throwable e) {
			log.error(String.format("Can't instantiate '%s' - using default", editUserApproverAssociationGroovyScript), e);
			identifier = new DefaultEditUserApproverAssociationIdentifier();
		}

		identifier.init(bindingMap);

		final List<String> approverAssociationIds = identifier.getApproverAssociationIds();
		final List<String> approverUserIds = identifier.getApproverIds();

		idmAuditLog.addAttributeAsJson(AuditAttributeName.REQUEST_APPROVER_IDS, approverUserIds, jacksonMapper);

		final List<Object> approverCardinatlity = new LinkedList<Object>();
		if(CollectionUtils.isNotEmpty(approverAssociationIds)) {
			approverCardinatlity.addAll(approverAssociationIds);
		} else {
			approverCardinatlity.add(approverUserIds);
		}

		final ResourceEntity resource = createAndSaveWorkflowResource(description);

		final Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(ActivitiConstants.WORKFLOW_RESOURCE_ID.getName(), resource.getId());
		variables.put(ActivitiConstants.OPENIAM_VERSION.getName(), sysConfiguration.getProjectVersion());
		variables.put(ActivitiConstants.APPROVER_CARDINALTITY.getName(), approverCardinatlity);
		variables.put(ActivitiConstants.APPROVER_ASSOCIATION_IDS.getName(), approverAssociationIds);
		try {
			variables.put(ActivitiConstants.REQUEST.getName(), new ActivitiJSONStringWrapper(jacksonMapper.writeValueAsString(request)));
		} catch (JsonProcessingException e) {
			throw new BasicDataServiceException(ResponseCode.INTERNAL_ERROR, "Cannot serialize request to JSON");
		}
		variables.put(ActivitiConstants.TASK_NAME.getName(), description);
		variables.put(ActivitiConstants.TASK_DESCRIPTION.getName(), description);
		variables.put(ActivitiConstants.REQUESTOR.getName(), SpringSecurityHelper.getRequestorUserId());
		variables.put(ActivitiConstants.ASSOCIATION_ID.getName(), request.getUser().getId());
		variables.put(ActivitiConstants.WORKFLOW_NAME.getName(), ActivitiRequestType.EDIT_USER.getKey());
		if(identifier.getCustomActivitiAttributes() != null) {
			variables.putAll(identifier.getCustomActivitiAttributes());
		}

		//idmAuditLog = auditLogService.save(idmAuditLog);
		idmAuditLog = auditLogHelper.save(idmAuditLog);
		AuditLogHolder.getInstance().setEvent(idmAuditLog);
		variables.put(ActivitiConstants.AUDIT_LOG_ID.getName(), idmAuditLog.getId());

		final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(ActivitiRequestType.EDIT_USER.getKey(), variables);
		resource.setReferenceId(processInstance.getId());
		resourceService.save(resource);
		populate(response, processInstance, resource, approverAssociationIds, approverUserIds, SpringSecurityHelper.getRequestorUserId());

		idmAuditLog = auditLogService.findById(idmAuditLog.getId());
		for(Map.Entry<String,Object> varEntry : variables.entrySet()) {
			idmAuditLog.put(varEntry.getKey(), (varEntry.getValue() != null) ? varEntry.getValue().toString() : null);
		}

		idmAuditLog.setTargetTask(processInstance.getId(), description);
		return response;
	}

	private void validateUserRequest(final UserProfileRequestModel request) throws BasicDataServiceException {
		final UserEntity provisionUserValidationObject = userDozerConverter.convertToEntity(request.getUser(), true);
		entityValidator.isValid(provisionUserValidationObject);
		if(CollectionUtils.isNotEmpty(request.getEmails())) {
			for(final EmailAddress bean : request.getEmails()) {
				final EmailAddressEntity entity = emailDozerConverter.convertToEntity(bean, true);
				entityValidator.isValid(entity);
			}
		}
		if(CollectionUtils.isNotEmpty(request.getPhones())) {
			for(final Phone bean : request.getPhones()) {
				final PhoneEntity entity = phoneDozerConverter.convertToEntity(bean, true);
				entityValidator.isValid(entity);
			}
		}
		if(CollectionUtils.isNotEmpty(request.getAddresses())) {
			for(final Address bean : request.getAddresses()) {
				final AddressEntity entity = addressDozerConverter.convertToEntity(bean, true);
				entityValidator.isValid(entity);
			}
		}
	}

	@Override
	@Transactional
	public BasicWorkflowResponse initiateWorkflow(final GenericWorkflowRequest request) throws BasicDataServiceException {
		final String requestorId = request.getRequesterId();
		IdmAuditLogEntity idmAuditLog = AuditLogHolder.getInstance().getEvent();
		idmAuditLog.setRequestorUserId(requestorId);
		//idmAuditLog.setAction(AuditAction.INITIATE_WORKFLOW.value());
		idmAuditLog.setAction(request.getActivitiRequestType());
		idmAuditLog.setBaseRequest(request);
		idmAuditLog.setSource(AuditSource.WORKFLOW.value());
		final BasicWorkflowResponse response = new BasicWorkflowResponse();
		idmAuditLog.addAttributeAsJson(AuditAttributeName.REQUEST, request, jacksonMapper);

		if (request == null || request.isEmpty()) {
			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
		}

		final Map<String, Object> bindingMap = new HashMap<String, Object>();
		bindingMap.put("REQUEST", request);
		bindingMap.put("BUILDER", idmAuditLog);

		DefaultGenericWorkflowRequestApproverAssociationIdentifier identifier = null;
		try {
			identifier = (DefaultGenericWorkflowRequestApproverAssociationIdentifier)
					scriptRunner.instantiateClass(bindingMap, membershipApproverAssociationGroovyScript);

			if (identifier == null) {
				throw new Exception("Did not instantiate script - was null");
			}
		} catch (Throwable e) {
			log.error(String.format("Can't instantiate '%s' - using default", membershipApproverAssociationGroovyScript), e);
			identifier = new DefaultGenericWorkflowRequestApproverAssociationIdentifier();
		}

		identifier.init(bindingMap);

		final List<String> approverAssociationIds = identifier.getApproverAssociationIds();
		final List<String> approverUserIds = identifier.getApproverIds();

		List<Object> approverCardinatlity = new LinkedList<Object>();
		if(propertyValueSweeper.getBoolean("org.openiam.idm.activiti.merge.custom.approver.with.approver.associations")){
			final List<String> mergedIds = new LinkedList<String>();

			if (CollectionUtils.isNotEmpty(approverUserIds)) {
				mergedIds.addAll(approverUserIds);
			}
			if (CollectionUtils.isNotEmpty(approverAssociationIds)) {
				mergedIds.addAll(getCandidateUserIdsFromApproverAssociations(request,approverAssociationIds));
			}
			approverCardinatlity = buildApproverCardinatlity(request, mergedIds);
		} else {
			if (CollectionUtils.isNotEmpty(approverAssociationIds)) {
				approverCardinatlity.addAll(approverAssociationIds);
			} else {
				approverCardinatlity = buildApproverCardinatlity(request, approverUserIds);
			}
		}

		final ResourceEntity resource = createAndSaveWorkflowResource(request.getName());

		idmAuditLog.addAttributeAsJson(AuditAttributeName.REQUEST_APPROVER_IDS, approverUserIds, jacksonMapper);
		final Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(ActivitiConstants.WORKFLOW_RESOURCE_ID.getName(), resource.getId());
		variables.put(ActivitiConstants.OPENIAM_VERSION.getName(), sysConfiguration.getProjectVersion());
		variables.put(ActivitiConstants.WORKFLOW_NAME.getName(), request.getActivitiRequestType());
		variables.put(ActivitiConstants.APPROVER_CARDINALTITY.getName(), approverCardinatlity);
		variables.put(ActivitiConstants.APPROVER_ASSOCIATION_IDS.getName(), approverAssociationIds);
		variables.put(ActivitiConstants.TASK_NAME.getName(), request.getName());
		variables.put(ActivitiConstants.TASK_DESCRIPTION.getName(), request.getDescription());
		variables.put(ActivitiConstants.REQUESTOR.getName(), request.getRequesterId());
		variables.put(ActivitiConstants.DELETABLE.getName(), Boolean.valueOf(request.isDeletable()));
		variables.put(ActivitiConstants.ACCESS_RIGHTS.getName(), request.getAccessRights());

		if(request.getStartDate() != null) {
			variables.put(ActivitiConstants.START_DATE.getName(), request.getStartDate());
		}
		if(request.getEndDate() != null) {
			variables.put(ActivitiConstants.END_DATE.getName(), request.getEndDate());
		}
		if(request.getUserNotes() != null) {
			variables.put(ActivitiConstants.USER_NOTE.getName(), request.getUserNotes());
		}

		if(request.getAssociationId() != null) {
			variables.put(ActivitiConstants.ASSOCIATION_ID.getName(), request.getAssociationId());
		}
		if(request.getAssociationType() != null) {
			variables.put(ActivitiConstants.ASSOCIATION_TYPE.getName(), request.getAssociationType().getValue());
		}
		if(request.getMemberAssociationId() != null) {
			variables.put(ActivitiConstants.MEMBER_ASSOCIATION_ID.getName(), request.getMemberAssociationId());
		}
		if(request.getMemberAssociationType() != null) {
			variables.put(ActivitiConstants.MEMBER_ASSOCIATION_TYPE.getName(), request.getMemberAssociationType().getValue());
		}
		if(request.getParameters() != null) {
			variables.putAll(request.getParameters());
		}
		if(request.getJsonSerializedParams() != null) {
			for(final String key : request.getJsonSerializedParams().keySet()) {
				final String value = request.getJsonSerializedParams().get(key);
				variables.put(key, new ActivitiJSONStringWrapper(value));
			}
		}

		if(identifier.getCustomActivitiAttributes() != null) {
			variables.putAll(identifier.getCustomActivitiAttributes());
		}

		boolean isAdmin = false;
		if((request.getAssociationId() != null) && (request.getAssociationType() != null)) {
			switch(request.getAssociationType()) {
				case GROUP:
					isAdmin = authManagerService.isMemberOfGroup(requestorId, request.getAssociationId(), adminRightId);
					break;
				case ORGANIZATION:
					isAdmin = authManagerService.isMemberOfOrganization(requestorId, request.getAssociationId(), adminRightId);
					break;
				case RESOURCE:
					isAdmin = authManagerService.isEntitled(requestorId, request.getAssociationId(), adminRightId);
					break;
				case ROLE:
					isAdmin = authManagerService.isMemberOfRole(requestorId, request.getAssociationId(), adminRightId);
					break;
				default:
					break;
			}
		}

		if((request.getMemberAssociationId() != null) && (request.getMemberAssociationType() != null)) {
			switch(request.getMemberAssociationType()) {
				case GROUP:
					isAdmin = authManagerService.isMemberOfGroup(requestorId, request.getMemberAssociationId(), adminRightId);
					break;
				case ORGANIZATION:
					isAdmin = authManagerService.isMemberOfOrganization(requestorId, request.getMemberAssociationId(), adminRightId);
					break;
				case RESOURCE:
					isAdmin = authManagerService.isEntitled(requestorId, request.getMemberAssociationId(), adminRightId);
					break;
				case ROLE:
					isAdmin = authManagerService.isMemberOfRole(requestorId, request.getMemberAssociationId(), adminRightId);
					break;
				default:
					break;
			}
		}
		variables.put(ActivitiConstants.IS_ADMIN.getName(), isAdmin);

		//idmAuditLog = auditLogService.save(idmAuditLog);
		idmAuditLog = auditLogHelper.save(idmAuditLog);
		AuditLogHolder.getInstance().setEvent(idmAuditLog);
		variables.put(ActivitiConstants.AUDIT_LOG_ID.getName(), idmAuditLog.getId());

		final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(request.getActivitiRequestType(), variables);
		resource.setReferenceId(processInstance.getId());
		resourceService.save(resource);
		populate(response, processInstance, resource, approverAssociationIds, approverUserIds, request.getRequesterId());

		idmAuditLog = auditLogService.findById(idmAuditLog.getId());
		idmAuditLog.setTargetTask(processInstance.getId(), request.getName());
		for (Map.Entry<String, Object> varEntry : variables.entrySet()) {
			idmAuditLog.put(varEntry.getKey(), (varEntry.getValue() != null) ? varEntry.getValue().toString() : null);
		}
		return response;
	}

	@Override
	public String getProcessInstanceIdByExecutionId(String executionId) throws BasicDataServiceException {
		if(StringUtils.isBlank(executionId)){
			throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "ExcecutionID is not set");
		}
		final List<HistoricActivityInstance> results = historyService.createHistoricActivityInstanceQuery().executionId(executionId).list();
		return (CollectionUtils.isNotEmpty(results)) ? results.get(0).getProcessInstanceId() : null;
	}

	private List<String> getCandidateUserIdsFromApproverAssociations(final GenericWorkflowRequest request, final List<String> approverAssociationIds){
		List<String> candidateIds = new LinkedList<>();
		String targetUserId = null;
		if(request.getMemberAssociationType() != null && request.getMemberAssociationType() == AssociationType.USER
				&& request.getMemberAssociationId()!=null){
			targetUserId = request.getMemberAssociationId();
		}
		candidateIds =  activitiHelper.getCandidateUserIds(approverAssociationIds,targetUserId, null);

		return candidateIds;
	}
	private List<Object> buildApproverCardinatlity(final GenericWorkflowRequest request, List<String> sourceList){
		final List<Object> approverCardinatlity = new LinkedList<Object>();
		//TODO fix here AM flag
		if (request.isCustomApproversSequential()) {
			for (final String id : sourceList) {
				approverCardinatlity.add(id);
			}
		} else {
			approverCardinatlity.add(sourceList);
		}
		return approverCardinatlity;
	}

	@Override
	public void makeDecision(final ActivitiRequestDecision request)  throws BasicDataServiceException {
		final IdmAuditLogEntity idmAuditLog = AuditLogHolder.getInstance().getEvent();
		idmAuditLog.setRequestorUserId(request.getRequesterId());
		idmAuditLog.setAction(AuditAction.COMPLETE_WORKFLOW.value());
		idmAuditLog.setBaseRequest(request);
		idmAuditLog.setSource(AuditSource.WORKFLOW.value());
		String parentAuditLogId = null;
		try {
			idmAuditLog.addAttributeAsJson(AuditAttributeName.REQUEST, request, jacksonMapper);
			final Task assignedTask = getTaskAssignee(request);

        	/* complete the Task in Activiti, passing required parameters */
			final Map<String, Object> variables = new HashMap<String, Object>();
			variables.put(ActivitiConstants.COMMENT.getName(), request.getComment());
			variables.put(ActivitiConstants.IS_TASK_APPROVED.getName(), request.isAccepted());
			variables.put(ActivitiConstants.EXECUTOR_ID.getName(), request.getRequesterId());
			if(request.getCustomVariables() != null) {
				variables.putAll(request.getCustomVariables());
			}

			idmAuditLog.setTargetTask(assignedTask.getId(), assignedTask.getName());
			for(Map.Entry<String,Object> varEntry : variables.entrySet()) {
				idmAuditLog.put(varEntry.getKey(), (varEntry.getValue() != null) ? varEntry.getValue().toString() : null);
			}


			final Object auditLogId = taskService.getVariable(assignedTask.getId(), ActivitiConstants.AUDIT_LOG_ID.getName());
			if(auditLogId != null && auditLogId instanceof String) {
				parentAuditLogId = (String)auditLogId;
			}

			final Task task = taskService.createTaskQuery().taskId(assignedTask.getId()).list().get(0);

			taskService.setVariablesLocal(assignedTask.getId(), variables);
			if(org.apache.commons.lang3.StringUtils.isNotBlank(request.getComment())) {
				taskService.addComment(assignedTask.getId(), task.getProcessInstanceId(), request.getComment());
			}
			taskService.complete(assignedTask.getId(), variables);
		} finally {
			updateParentAuditLog(parentAuditLogId, idmAuditLog);
		}
	}

	private Task getTaskAssignee(final String taskId, final String userId) {
		final List<Task> taskList = taskService.createTaskQuery().taskId(taskId).taskAssignee(userId).list();
		if(CollectionUtils.isEmpty(taskList)) {
			throw  new ActivitiException("No tasks for user..");
		}

		return taskList.get(0);
	}

	private Task getTaskAssignee(final ActivitiRequestDecision newHireRequest) throws ActivitiException {
		return getTaskAssignee(newHireRequest.getTaskId(), newHireRequest.getRequesterId());
	}


	@Override
	@Transactional
	@Deprecated
	public int getNumOfAssignedTasks(String userId) {
		return countTasks(new TaskSearchBean().setAssigneeId(userId));
	}

	@Override
	@Transactional
	@Deprecated
	public int getNumOfCandidateTasks(String userId) {
		return countTasks(new TaskSearchBean().setCandidateId(userId));
	}

	@Override
	@Transactional
	public TaskWrapper getTask(String taskId) throws BasicDataServiceException {
		if(StringUtils.isBlank(taskId)){
			throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Task ID is not set");
		}
		TaskWrapper retVal = null;
		final List<Task> taskList = taskService.createTaskQuery().taskId(taskId).list();
		if(CollectionUtils.isNotEmpty(taskList)) {
			retVal = ActivitiUtils.getTaskWrapper(taskList.get(0), runtimeService);
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
	public TaskWrapper getTaskFromHistory(final String executionId, final String taskId) {
		TaskWrapper retVal = null;
		final HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery();
		if(StringUtils.isNotBlank(executionId)) {
			query.executionId(executionId);
		} else if(StringUtils.isNotBlank(taskId)) {
			query.taskId(taskId);
		} else {
			throw new IllegalArgumentException("Execution ID and Task ID are null");
		}
		final List<HistoricTaskInstance> instances = query.list();
		if(CollectionUtils.isNotEmpty(instances)) {
			retVal = ActivitiUtils.getTaskWrapper(instances.get(0));
		}
		return retVal;
	}

	private void setValue(final Field field, final ActivitiHistoricDetail entity, final Object obj) {
		try {
			final PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), entity.getClass());
			final Method method = PropertyUtils.getWriteMethod(descriptor);
			if(method != null) {
				ReflectionUtils.invokeMethod(method, entity, obj);
			}
		} catch(Throwable e) {
			log.error("Can't call method", e);
		}
	}

	private void pouplateField(final ActivitiConstants constant, final ActivitiHistoricDetail detail, final Object value) {
		Field field = null;
		try {
			field = detail.getClass().getDeclaredField(constant.getFieldName());
		} catch (Exception e1) {
		}
		if(field != null) {
			field.setAccessible(true);
			if(StringUtils.equals(field.getName(), constant.getFieldName())) {
				/* it's a match.  now try to set it */
				if(String.class.equals(field.getType())) {
					setValue(field, detail, value);
				} else if(field.getType().isInstance(Number.class)) {
					setValue(field, detail, value);
				} else if(field.getType().equals(Boolean.class)) {
					setValue(field, detail, value);
				} else if(field.getType().equals(List.class)) {
					setValue(field, detail, value);
				} else if(field.isAnnotationPresent(ActivitiJSONField.class)) {
					if(value instanceof String) {
						try {
							final Object deserializedValue = jacksonMapper.readValue((String)value, field.getType());
							setValue(field, detail, deserializedValue);
						} catch (Throwable e) {
							log.error(String.format("Can't use jackson to deserialize '%s', which should be of type '%s'.", value, field.getType()), e);
						}
					} else {
						log.error(String.format("Can't use jackson to deserialize '%s', which should be of type '%s'.", value, field.getType()));
					}
				} else {
					log.warn(String.format("Unknown field '%s' with type '%s' and value '%s'", field.getName(), field.getType(), value));
				}

				if(field.isAnnotationPresent(ActivitiUserField.class)) {
					final ActivitiUserField userFieldAnnotation = field.getAnnotation(ActivitiUserField.class);
					if(userFieldAnnotation != null) {
						final Field userField = ReflectionUtils.findField(ActivitiHistoricDetail.class, userFieldAnnotation.value());
						if(userField != null) {
							userField.setAccessible(true);
							if(field.getType().equals(List.class)) {
								final List<User> userList = new LinkedList<>();
								((List<String>)value).forEach(id -> {
									final User user = userDataService.getUserDto(id);
									if(user != null) {
										userList.add(getUser(user, userFieldAnnotation));
									}
								});
								setValue(userField, detail, userList);
							} else { /* assume String */
								final User user = userDataService.getUserDto((String)value);
								if(user != null) {
									setValue(userField, detail, getUser(user, userFieldAnnotation));
								}
							}
						}
					}
				}
			} else {
				log.error(String.format("Field '%s' has a type '%s'", field.getName(), field.getType()));
			}
		}
	}

	private void populateLocalVariables(final ActivitiHistoricDetail detail, final String taskId) {
		final HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId).includeProcessVariables().includeTaskLocalVariables().singleResult();
		final Map<String, Object> localVariables = task.getTaskLocalVariables();
		if(localVariables != null) {
			localVariables.forEach((variableName, value) -> {
				final ActivitiConstants constant = ActivitiConstants.getByName(variableName);
				if(constant != null && constant.getFieldName() != null && value != null && constant.isLocal()) {
					pouplateField(constant, detail, value);
				}
			});
		}
	}

	private void populateGlobalVariables(final ActivitiHistoricDetail detail, final String activityInstanceId) {
		final HistoricActivityInstance instance = historyService.createHistoricActivityInstanceQuery().activityInstanceId(activityInstanceId).singleResult();
		final List<HistoricVariableInstance> queryInstances = historyService.createHistoricVariableInstanceQuery().processInstanceId(instance.getProcessInstanceId()).list();
		final List<HistoricProcessInstance> processInstances = historyService.createHistoricProcessInstanceQuery().processInstanceId(instance.getProcessInstanceId()).includeProcessVariables().list();

		if(queryInstances != null) {
			queryInstances.forEach(variable -> {
				final ActivitiConstants constant = ActivitiConstants.getByName(variable.getVariableName());
				final Object value = variable.getValue();
				if(constant != null && constant.getFieldName() != null && value != null && !constant.isLocal()) {
					pouplateField(constant, detail, value);
				}
			});
		}

		if(CollectionUtils.isNotEmpty(processInstances)) {
			processInstances.forEach(processInstance -> {
				if(processInstance.getProcessVariables() != null) {
					processInstance.getProcessVariables().forEach((key, value) -> {

					});
				}
			});
		}
	}

	@Override
	public int getNumOfAssignedTasksWithFilter(String userId, String description, String requesterId, Date fromDate, Date toDate) {
		TaskQuery query = taskService.createTaskQuery();
		if(fromDate != null) {
			query.taskCreatedAfter(fromDate);
		}
		if(toDate != null) {
			query.taskCreatedBefore(toDate);
		}
		if(description != null ) {
			description = description.toLowerCase();
			List<Task> assignedTasks = query.taskAssignee(userId).list();
			if(assignedTasks != null && assignedTasks.size() > 0) {
				TaskListWrapper taskListWrapper = new TaskListWrapper();
				taskListWrapper.addAssignedTasks(ActivitiUtils.wrapTaskList(assignedTasks, runtimeService, loginService));
				List<TaskWrapper> taskWrappers = new ArrayList<TaskWrapper>();
				for (TaskWrapper wrapper : taskListWrapper.getAssignedTasks()) {
					if (wrapper.getDescription().toLowerCase().contains(description)) {
						taskWrappers.add(wrapper);
					}
				}
				return taskWrappers.size();
			}
			return 0;
		} else if(requesterId != null ) {
			List<Task> assignedTasks = query.taskAssignee(userId).list();
			if(assignedTasks != null && assignedTasks.size() > 0) {
				TaskListWrapper taskListWrapper = new TaskListWrapper();
				taskListWrapper.addAssignedTasks(ActivitiUtils.wrapTaskList(assignedTasks, runtimeService, loginService));
				List<TaskWrapper> taskWrappers = new ArrayList<TaskWrapper>();
				for (TaskWrapper wrapper : taskListWrapper.getAssignedTasks()) {
					if (wrapper.getOwner() != null) {
						if (wrapper.getOwner().equals(requesterId)) {
							taskWrappers.add(wrapper);
						}
					}
				}
				return taskWrappers.size();
			}
			return 0;
		}
		return (int)query.taskAssignee(userId).count();
	}

	@Override
	public int getNumOfCandidateTasksWithFilter(String userId, String description, Date fromDate, Date toDate) {
		TaskQuery query = taskService.createTaskQuery();
		if(fromDate != null) {
			query.taskCreatedAfter(fromDate);
		}
		if(toDate != null) {
			query.taskCreatedBefore(toDate);
		}
		if(description != null) {
			description = description.toLowerCase();
			List<Task> candidateTasks = query.taskCandidateUser(userId).list();
			if(candidateTasks != null && candidateTasks.size() > 0) {
				TaskListWrapper taskListWrapper = new TaskListWrapper();
				taskListWrapper.addCandidateTasks(ActivitiUtils.wrapTaskList(candidateTasks, runtimeService, loginService));
				List<TaskWrapper> taskWrappers = new ArrayList<TaskWrapper>();
				for (TaskWrapper wrapper : taskListWrapper.getCandidateTasks()) {
					if (wrapper.getDescription().toLowerCase().contains(description)) {
						taskWrappers.add(wrapper);
					}
				}
				return taskWrappers.size();
			}
			return 0;
		}
		return (int)query.taskCandidateUser(userId).count();
	}

	@Override
	public TaskListWrapper getTasksForCandidateUserWithFilter(String userId, int from, int size, String description, Date fromDate, Date toDate) {
		final TaskListWrapper taskListWrapper = new TaskListWrapper();
		TaskQuery query = taskService.createTaskQuery();
		if(fromDate != null) {
			query.taskCreatedAfter(fromDate);
		}
		if(toDate != null) {
			query.taskCreatedBefore(toDate);
		}
		final List<Task> candidateTasks = query.taskCandidateUser(userId).list();
		Collections.sort(candidateTasks, taskCreatedTimeComparator);
		taskListWrapper.addCandidateTasks(ActivitiUtils.wrapTaskList(candidateTasks, runtimeService, loginService));
		if(description != null && taskListWrapper.getCandidateTasks() != null){
			List<TaskWrapper> results = new ArrayList<TaskWrapper>();
			for(TaskWrapper wrapper : taskListWrapper.getCandidateTasks()) {
				if(wrapper.getDescription().toLowerCase().contains(description.toLowerCase())) {
					results.add(wrapper);
				}
			}
			if(from+size < results.size()) {
				taskListWrapper.setCandidateTasks(results.subList(from, from + size));
			} else {
				taskListWrapper.setCandidateTasks(results.subList(from, results.size()));
			}
		}
		return taskListWrapper;
	}

	@Override
	public TaskListWrapper getTasksForAssignedUserWithFilter(String userId, int from, int size, String description, String requesterId, Date fromDate, Date toDate) {
		final TaskListWrapper taskListWrapper = new TaskListWrapper();
		TaskQuery query = taskService.createTaskQuery();
		if(fromDate != null) {
			query.taskCreatedAfter(fromDate);
		}
		if(toDate != null) {
			query.taskCreatedBefore(toDate);
		}
		final List<Task> assignedTasks = query.taskAssignee(userId).list();
		Collections.sort(assignedTasks, taskCreatedTimeComparator);
		taskListWrapper.addAssignedTasks(ActivitiUtils.wrapTaskList(assignedTasks, runtimeService, loginService));
		if(description != null && taskListWrapper.getAssignedTasks() != null){
			List<TaskWrapper> results = new ArrayList<TaskWrapper>();
			for(TaskWrapper wrapper : taskListWrapper.getAssignedTasks()) {
				if(wrapper.getDescription().toLowerCase().contains(description.toLowerCase())) {
					results.add(wrapper);
				}
			}
			if(from+size < results.size()) {
				taskListWrapper.setAssignedTasks(results.subList(from, from + size));
			} else {
				taskListWrapper.setAssignedTasks(results.subList(from, results.size()));
			}
		} else if(requesterId != null && taskListWrapper.getAssignedTasks() != null){
			List<TaskWrapper> results = new ArrayList<TaskWrapper>();
			for(TaskWrapper wrapper : taskListWrapper.getAssignedTasks()) {
				if (wrapper.getOwner() != null) { // owner id null in self registration case
					if (wrapper.getOwner().equals(requesterId)) {
						results.add(wrapper);
					}
				}
			}
			if(from+size < results.size()) {
				taskListWrapper.setAssignedTasks(results.subList(from, from + size));
			} else {
				taskListWrapper.setAssignedTasks(results.subList(from, results.size()));
			}
		}

		return taskListWrapper;
	}


	private User getUser(final User user, final ActivitiUserField userFieldAnnotation) {
		if(userFieldAnnotation.exposeDetails()) {
			if(user.getPrincipalList() != null) {
				user.getPrincipalList().forEach(e -> {
					e.setPassword(null);
				});
			}
			return user;
		} else {
			final User retVal = new User();
			retVal.setId(user.getId());
			retVal.setFirstName(user.getFirstName());
			retVal.setLastName(user.getLastName());
			return retVal;
		}
	}

	@Override
	@Transactional
	public List<TaskHistoryWrapper> getHistoryForInstance(final String executionId) {
		final List<TaskHistoryWrapper> retVal = new LinkedList<TaskHistoryWrapper>();

		if(StringUtils.isNotBlank(executionId)) {
			final List<HistoricTaskInstance> instances = historyService.createHistoricTaskInstanceQuery().executionId(executionId).list();
			final Map<String, HistoricTaskInstance> taskDefinitionMap = new HashMap<String, HistoricTaskInstance>();
			if(CollectionUtils.isNotEmpty(instances)) {
				for(HistoricTaskInstance instance : instances) {
					taskDefinitionMap.put(instance.getTaskDefinitionKey(), instance);
				}
			}

			final HistoricActivityInstanceQuery query = historyService.createHistoricActivityInstanceQuery().executionId(executionId);
			final List<HistoricActivityInstance> activityList = query.list();
			if(CollectionUtils.isNotEmpty(activityList)) {
				for(int i = 0; i < activityList.size(); i++) {
					final HistoricActivityInstance instance = activityList.get(i);
					if(StringUtils.isNotBlank(instance.getActivityName())) {
						final TaskHistoryWrapper wrapper = ActivitiUtils.getTaskHistoryWrapper(instance);

						final ActivitiHistoricDetail details = new ActivitiHistoricDetail();
						wrapper.setVariableDetails(details);
						populateGlobalVariables(details, instance.getId());

						if(taskDefinitionMap.containsKey(wrapper.getActivityId())) {
							wrapper.setTask(ActivitiUtils.getTaskWrapper((taskDefinitionMap.get(wrapper.getActivityId()))));
						}
						if(StringUtils.isNotBlank(wrapper.getAssigneeId())) {
							final UserEntity user =  userDataService.getUser(wrapper.getAssigneeId());
							wrapper.setUserInfo(user);
						}
						if(StringUtils.isNotBlank(wrapper.getTaskId())) {
							populateLocalVariables(details, wrapper.getTaskId());
						}
						//wrapper.
						retVal.add(wrapper);

						/*
						if(i < activityList.size() - 1) {
							wrapper.addNextTask(activityList.get(i + 1).getId());
						}
						*/
					}

					/*
					final HistoricDetailQuery detailQuery = historyService.createHistoricDetailQuery()
																		  .activityInstanceId(instance.getId())
																		  .variableUpdates();
					final List<HistoricDetail> details = detailQuery.list();
					if(CollectionUtils.isNotEmpty(details)) {
						for(final HistoricDetail detail : details) {
							final HistoricVariableUpdate variableUpdate = (HistoricVariableUpdate)detail;
							log.info(String.format("Variable update: %s", variableUpdate));
						}
					}
					*/
				}
			}

			for(int i = 0; i < retVal.size(); i++) {
				final TaskHistoryWrapper wrapper = retVal.get(i);
				if(i < retVal.size() - 1) {
					wrapper.addNextTask(retVal.get(i + 1).getId());
				}
			}
		}
		return retVal;
	}

	@Override
	@Transactional
	public List<TaskWrapper> getHistory(final HistorySearchBean searchBean, final int from, final int size) {
		final HistoricTaskInstanceQuery query = getHistoryQuery(searchBean);

		final List<HistoricTaskInstance> historicTaskInstances = query.listPage(from, size);
		final List<TaskWrapper> retVal = new LinkedList<TaskWrapper>();
		if (CollectionUtils.isNotEmpty(historicTaskInstances)) {
			for (final HistoricTaskInstance historyInstance : historicTaskInstances) {
				retVal.add(ActivitiUtils.getTaskWrapper(historyInstance));
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
		if (StringUtils.isNotBlank(searchBean.getAssigneeId())) {
			query.taskAssignee(searchBean.getAssigneeId());
		}
		if (searchBean.isCompleted() != null) {
			if (Boolean.TRUE.equals(searchBean.isCompleted())) {
				query.finished();
			} else {
				query.unfinished();
			}
		}

		if(StringUtils.isNotBlank(searchBean.getInvolvedUserId())) {
			query.taskInvolvedUser(searchBean.getInvolvedUserId());
		}

		if (StringUtils.isNotBlank(searchBean.getProcessInstanceId())) {
			query.processInstanceId(searchBean.getProcessInstanceId());
		}

		if (StringUtils.isNotBlank(searchBean.getExecutionId())) {
			query.executionId(searchBean.getExecutionId());
		}

		if (StringUtils.isNotBlank(searchBean.getParentTaskId())) {
			query.taskParentTaskId(searchBean.getParentTaskId());
		}

		if (StringUtils.isNotBlank(searchBean.getProcessDefinitionId())) {
			query.processDefinitionId(searchBean.getProcessDefinitionId());
		}

		if (searchBean.getDueAfter() != null) {
			query.taskDueAfter(searchBean.getDueAfter());
		}

		if (searchBean.getDueBefore() != null) {
			query.taskDueBefore(searchBean.getDueBefore());
		}

		if (StringUtils.isNotBlank(searchBean.getTaskName())) {
			query.taskNameLike(searchBean.getTaskName());
		}

		if (StringUtils.isNotBlank(searchBean.getTaskDescription())) {
			query.taskOwner(searchBean.getTaskDescription());
		}

		if (StringUtils.isNotBlank(searchBean.getTaskOwnerId())) {
			query.taskOwner(searchBean.getTaskOwnerId());
		}
		query.orderByHistoricTaskInstanceEndTime();
		query.desc();
		return query;
	}

	@Override
	@Transactional
	public void deleteTask(String taskId, final String userId)throws BasicDataServiceException {
		final IdmAuditLogEntity idmAuditLog = AuditLogHolder.getInstance().getEvent();
		idmAuditLog.setRequestorUserId(userId);
		idmAuditLog.setAction(AuditAction.TERMINATED_WORKFLOW.value());
		idmAuditLog.setSource(AuditSource.WORKFLOW.value());
		String parentAuditLogId = null;
		try {
			final Task task = taskService.createTaskQuery().taskOwner(userId).taskId(taskId).singleResult();
			if(task == null) {
				throw new BasicDataServiceException(ResponseCode.USER_STATUS, String.format("Task ID '%s' does not exist, or is not owned by '%s'", taskId, userId));
			}
			idmAuditLog.setTargetTask(task.getId(), task.getName());
			final Object auditLogId = taskService.getVariable(task.getId(), ActivitiConstants.AUDIT_LOG_ID.getName());
			if(auditLogId != null && auditLogId instanceof String) {
				parentAuditLogId = (String)auditLogId;
			}

			taskService.setVariableLocal(task.getId(), ActivitiConstants.TERMINATED_BY_OWNER.getName(), true);

			runtimeService.deleteProcessInstance(task.getProcessInstanceId(), "Terminated by owner");
			/* as of activiti 5.17.0, you can't delete a task that's part of a running process, so we just unclaim it */
			//taskService.unclaim(taskId);

			taskService.deleteTask(task.getId());
			//taskService.deleteTask(task.getId(), true);
		} finally {
			updateParentAuditLog(parentAuditLogId, idmAuditLog);
		}
	}

	@Override
	@Transactional
	public void unclaimTask(String taskId, String userId) throws BasicDataServiceException{
		final IdmAuditLogEntity idmAuditLog = AuditLogHolder.getInstance().getEvent();
		idmAuditLog.setRequestorUserId(userId);
		idmAuditLog.setAction(AuditAction.UNCLAIM_TASK.value());
		idmAuditLog.setSource(AuditSource.WORKFLOW.value());
		String parentAuditLogId = null;
		try {
			final Task task = getTaskAssignee(taskId, userId);
			idmAuditLog.setTargetTask(task.getId(), task.getName());
			final Object auditLogId = taskService.getVariable(task.getId(), ActivitiConstants.AUDIT_LOG_ID.getName());
			if(auditLogId != null && auditLogId instanceof String) {
				parentAuditLogId = (String)auditLogId;
			}

			/* as of activiti 5.17.0, you can't delete a task that's part of a running process, so we just unclaim it */
			taskService.unclaim(taskId);
			//taskService.deleteTask(task.getId(), true);
			idmAuditLog.succeed();
		} finally {
			updateParentAuditLog(parentAuditLogId, idmAuditLog);
		}
	}

	@Override
	@Transactional
	@Deprecated
	public TaskListWrapper getTasksForUser(final String userId, final int from, final int size) {
		final TaskListWrapper taskListWrapper = new TaskListWrapper();
		final List<Task> assignedTasks = taskService.createTaskQuery().taskAssignee(userId).listPage(from, size);
		final List<Task> candidateTasks = taskService.createTaskQuery().taskCandidateUser(userId).listPage(from, size);
		Collections.sort(assignedTasks, taskCreatedTimeComparator);
		Collections.sort(candidateTasks, taskCreatedTimeComparator);
		taskListWrapper.addAssignedTasks(ActivitiUtils.wrapTaskList(assignedTasks, runtimeService));
		taskListWrapper.addCandidateTasks(ActivitiUtils.wrapTaskList(candidateTasks, runtimeService));
		return taskListWrapper;
	}

	@Override
	@Transactional
	public void  deleteTasksForUser(final String userId)throws BasicDataServiceException {
		final IdmAuditLogEntity idmAuditLog = AuditLogHolder.getInstance().getEvent();
		idmAuditLog.setRequestorUserId(userId);
		idmAuditLog.setAction(AuditAction.DELETE_ALL_USER_TASKS.value());
		idmAuditLog.setSource(AuditSource.WORKFLOW.value());
		String parentAuditLogId = null;
		try {

			final List<Task> assignedTasks = taskService.createTaskQuery().taskAssignee(userId).list();
			if(CollectionUtils.isNotEmpty(assignedTasks)) {
				for(final Task task : assignedTasks) {
					taskService.deleteTask(task.getId());
				}
			}
			final List<Task> candidateTasks = taskService.createTaskQuery().taskCandidateUser(userId).list();
			if(CollectionUtils.isNotEmpty(candidateTasks)) {
				for(final Task task : candidateTasks) {
					taskService.deleteTask(task.getId());
				}
			}
		} finally {
			if(parentAuditLogId != null) {
				IdmAuditLogEntity parent = auditLogService.findById(parentAuditLogId);
				parent.addChild(idmAuditLog);
				//idmAuditLog.addParent(parent);
				AuditLogHolder.getInstance().setEvent(parent);
			}
		}
	}

	@Override
	@Transactional
	@Deprecated
	public List<TaskWrapper> getTasksForMemberAssociation(String memberAssociationId) {
		final TaskSearchBean searchBean = new TaskSearchBean();
		searchBean.setMemberAssociationId(memberAssociationId);
		return findTasks(searchBean, 0, Integer.MAX_VALUE);
	}

	@Override
	@Transactional
	public List<TaskWrapper> findTasks(TaskSearchBean searchBean, int from, int size) {
		List<TaskWrapper> wrapperList = new LinkedList<TaskWrapper>();
		final List<Task> taskList = get(searchBean).listPage(from, size);
		if(taskList != null) {
			taskList.forEach(task -> {
				wrapperList.add(ActivitiUtils.getTaskWrapper(task, runtimeService));
			});
		}
		return wrapperList;
	}

	@Override
	@Transactional
	public int countTasks(TaskSearchBean searchBean) {
		return (int)get(searchBean).count();
	}

	private TaskQuery get(final TaskSearchBean searchBean) {
		final TaskQuery query = taskService.createTaskQuery();
		if(searchBean != null) {
			if(StringUtils.isNotBlank(searchBean.getAssigneeId())) {
				query.taskAssignee(searchBean.getAssigneeId());
			}
			if(StringUtils.isNotBlank(searchBean.getCandidateId())) {
				query.taskCandidateUser(searchBean.getCandidateId());
			}
			if(StringUtils.isNotBlank(searchBean.getMemberAssociationId())) {
				query.processVariableValueEquals(ActivitiConstants.MEMBER_ASSOCIATION_ID.getName(), searchBean.getMemberAssociationId());
			}
			if(StringUtils.isNotBlank(searchBean.getProcessDefinitionId())) {
				query.processDefinitionId(searchBean.getProcessDefinitionId());
			}
			if(StringUtils.isNotBlank(searchBean.getOwnerId())) {
				query.taskOwner(searchBean.getOwnerId());
			}
		}
		return query;
	}
	@Override
	@Transactional(readOnly=true)
	public List<String> getApproverUserIds(List<String> associationIds, final String targetUserId) {
		return activitiHelper.getCandidateUserIds(associationIds, targetUserId, null);
	}

	private void updateParentAuditLog(String parentAuditLogId, IdmAuditLogEntity child){
		if (parentAuditLogId != null) {
			IdmAuditLogEntity parent = auditLogService.findById(parentAuditLogId);
			if (parent != null) {
				parent.addChild(child);
				AuditLogHolder.getInstance().setEvent(parent);
			}
		}
	}
}

