package org.openiam.bpm.activiti;

import java.util.*;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.bpm.activiti.delegate.core.ActivitiHelper;
import org.openiam.dozer.converter.AddressDozerConverter;
import org.openiam.dozer.converter.EmailAddressDozerConverter;
import org.openiam.dozer.converter.PhoneDozerConverter;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.exception.CustomActivitiException;
import org.openiam.bpm.activiti.groovy.DefaultEditUserApproverAssociationIdentifier;
import org.openiam.bpm.activiti.groovy.DefaultGenericWorkflowRequestApproverAssociationIdentifier;
import org.openiam.bpm.activiti.groovy.DefaultNewHireRequestApproverAssociationIdentifier;
import org.openiam.bpm.activiti.model.ActivitiJSONStringWrapper;
import org.openiam.bpm.request.ActivitiClaimRequest;
import org.openiam.bpm.request.ActivitiRequestDecision;
import org.openiam.bpm.request.GenericWorkflowRequest;
import org.openiam.bpm.request.HistorySearchBean;
import org.openiam.bpm.response.TaskHistoryWrapper;
import org.openiam.bpm.response.TaskListWrapper;
import org.openiam.bpm.response.TaskWrapper;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.bpm.util.ActivitiRequestType;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.constant.AuditSource;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;
import org.openiam.idm.srvc.meta.exception.PageTemplateException;
import org.openiam.idm.srvc.meta.service.MetadataElementTemplateService;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.synch.service.generic.ObjectAdapterMap;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.service.UserProfileService;
import org.openiam.idm.util.CustomJacksonMapper;
import org.openiam.script.ScriptIntegration;
import org.openiam.validator.EntityValidator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Transactional;

@WebService(endpointInterface = "org.openiam.bpm.activiti.ActivitiService",
        targetNamespace = "urn:idm.openiam.org/bpm/request/service",
        serviceName = "ActivitiService")
public class ActivitiServiceImpl extends AbstractBaseService implements ActivitiService {

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
    private CustomJacksonMapper jacksonMapper;

    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserDataService userDataService;

    @Autowired
    private LoginDataService loginService;

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

    @Autowired
    private MetadataElementTemplateService pageTemplateService;

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

    @Override
    @WebMethod
    public String sayHello() {
        return "Hello";
    }

    @Override
    @WebMethod
    @Transactional
    public SaveTemplateProfileResponse initiateNewHireRequest(final NewUserProfileRequestModel request) {
        log.info("Initializing workflow");
        final SaveTemplateProfileResponse response = new SaveTemplateProfileResponse();
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setAction(AuditAction.NEW_USER_WORKFLOW.value());
        idmAuditLog.setBaseObject(request);
        idmAuditLog.setRequestorUserId(request.getRequestorUserId());
        idmAuditLog.setSource(AuditSource.WORKFLOW.value());
        try {
            idmAuditLog.addAttributeAsJson(AuditAttributeName.REQUEST, request, jacksonMapper);

            if (request == null || request.getActivitiRequestType() == null) {
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
                if (identifier == null) {
                    throw new Exception("Did not instantiate script - was null");
                }
            } catch (Throwable e) {
                log.error(String.format("Can't instantiate '%s' - using default", newUserApproverAssociationGroovyScript), e);
                identifier = new DefaultNewHireRequestApproverAssociationIdentifier();
            }

            identifier.init(bindingMap);

            final List<String> approverAssociationIds = identifier.getApproverAssociationIds();
            final List<String> approverUserIds = identifier.getApproverIds();

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
            if (CollectionUtils.isNotEmpty(approverAssociationIds)) {
                approverCardinatlity.addAll(approverAssociationIds);
            } else {
                approverCardinatlity.add(approverUserIds);
            }

            final Map<String, Object> variables = new HashMap<String, Object>();
            variables.put(ActivitiConstants.APPROVER_CARDINALTITY.getName(), approverCardinatlity);
            variables.put(ActivitiConstants.APPROVER_ASSOCIATION_IDS.getName(), approverAssociationIds);
            variables.put(ActivitiConstants.REQUEST.getName(), new ActivitiJSONStringWrapper(jacksonMapper.writeValueAsString(request)));
            variables.put(ActivitiConstants.TASK_NAME.getName(), taskName);
            variables.put(ActivitiConstants.TASK_DESCRIPTION.getName(), taskDescription);
            variables.put(ActivitiConstants.REQUESTOR.getName(), request.getRequestorUserId());
            variables.put(ActivitiConstants.WORKFLOW_NAME.getName(), requestType.getKey());
            variables.put(ActivitiConstants.REQUESTOR_NAME.getName(), request.getRequestorUserId());
            if (identifier.getCustomActivitiAttributes() != null) {
                variables.putAll(identifier.getCustomActivitiAttributes());
            }

            for (Map.Entry<String, Object> varEntry : variables.entrySet()) {
                idmAuditLog.addCustomRecord(varEntry.getKey(), (varEntry.getValue() != null) ? varEntry.getValue().toString() : null);
            }

            idmAuditLog = auditLogService.save(idmAuditLog);
            variables.put(ActivitiConstants.AUDIT_LOG_ID.getName(), idmAuditLog.getId());

            final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(requestType.getKey(), variables);
            idmAuditLog = auditLogService.findById(idmAuditLog.getId());
            idmAuditLog.setTargetTask(processInstance.getId(), taskName);
            response.succeed();
            idmAuditLog.succeed();
        } catch (PageTemplateException e) {
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
            response.setCurrentValue(e.getCurrentValue());
            response.setElementName(e.getElementName());
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
        } catch (BasicDataServiceException e) {
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
            response.setErrorTokenList(e.getErrorTokenList());
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
        } catch (ActivitiException e) {
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getMessage());
            idmAuditLog.setException(e);
            log.info("Activiti Exception", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_STATUS);
            response.setErrorText(e.getMessage());
        } catch (Throwable e) {
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getMessage());
            idmAuditLog.setException(e);
            log.error("Error while creating newhire request", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_STATUS);
            response.setErrorText(e.getMessage());
        } finally {
            log.info("Persisting activiti log..");
            idmAuditLog = auditLogService.save(idmAuditLog);
        }
        return response;
    }

    @Override
    @WebMethod
    @Transactional
    public Response claimRequest(final ActivitiClaimRequest request) {
        final IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setAction(AuditAction.CLAIM_REQUEST.value());
        idmAuditLog.setBaseObject(request);
        idmAuditLog.setRequestorUserId(request.getRequestorUserId());
        idmAuditLog.setSource(AuditSource.WORKFLOW.value());

        final Response response = new Response();
        String parentAuditLogId = null;
        try {
            idmAuditLog.addAttributeAsJson(AuditAttributeName.REQUEST, request, jacksonMapper);

            final List<Task> taskList = taskService.createTaskQuery().taskCandidateUser(request.getRequestorUserId()).list();
            if (CollectionUtils.isEmpty(taskList)) {
                throw new ActivitiException("No Candidate Task available");
            }

            if (StringUtils.isBlank(request.getTaskId())) {
                throw new ActivitiException("No Task specified");
            }

            Task potentialTaskToClaim = null;
            for (final Task task : taskList) {
                if (task.getId().equals(request.getTaskId())) {
                    potentialTaskToClaim = task;
                    break;
                }
            }

            if (potentialTaskToClaim == null) {
                throw new ActivitiException(String.format("Task with ID: '%s' not assigned to user", request.getTaskId()));
            }

            idmAuditLog.setTargetTask(potentialTaskToClaim.getId(), potentialTaskToClaim.getName());
			/* claim the process, and set the assignee */
            taskService.claim(potentialTaskToClaim.getId(), request.getRequestorUserId());
            taskService.setAssignee(potentialTaskToClaim.getId(), request.getRequestorUserId());

            final Object auditLogId = taskService.getVariable(potentialTaskToClaim.getId(), ActivitiConstants.AUDIT_LOG_ID.getName());
            if (auditLogId != null && auditLogId instanceof String) {
                parentAuditLogId = (String) auditLogId;
            }

            response.succeed();
            idmAuditLog.succeed();
        } catch (ActivitiException e) {
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getMessage());
            idmAuditLog.setException(e);
            log.info("Activiti Exception", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_STATUS);
            response.setErrorText(e.getMessage());
        } catch (Throwable e) {
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getMessage());
            idmAuditLog.setException(e);
            log.error("Error while creating newhire request", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_STATUS);
            response.setErrorText(e.getMessage());
        } finally {
            if (parentAuditLogId != null) {
                IdmAuditLog parent = auditLogService.findById(parentAuditLogId);
                if (parent != null) {
                    parent.addChild(idmAuditLog);
                    idmAuditLog.addParent(parent);
                    parent = auditLogService.save(parent);
                }
            }
        }

        return response;
    }

    @Override
    @Transactional
    public SaveTemplateProfileResponse initiateEditUserWorkflow(final UserProfileRequestModel request) {
        final SaveTemplateProfileResponse response = new SaveTemplateProfileResponse();

        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setAction(AuditAction.EDIT_USER_WORKFLOW.value());
        idmAuditLog.setBaseObject(request);
        idmAuditLog.setRequestorUserId(request.getRequestorUserId());
        idmAuditLog.setSource(AuditSource.WORKFLOW.value());
        try {
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
                if (identifier == null) {
                    throw new Exception("Did not instantiate script - was null");
                }
            } catch (Throwable e) {
                log.error(String.format("Can't instantiate '%s' - using default", editUserApproverAssociationGroovyScript), e);
                identifier = new DefaultEditUserApproverAssociationIdentifier();
            }

            identifier.init(bindingMap);

            final List<String> approverAssociationIds = identifier.getApproverAssociationIds();
            final List<String> approverUserIds = identifier.getApproverIds();

            idmAuditLog.addAttributeAsJson(AuditAttributeName.REQUEST_APPROVER_IDS, approverUserIds, jacksonMapper);

            final List<Object> approverCardinatlity = new LinkedList<Object>();
            if (CollectionUtils.isNotEmpty(approverAssociationIds)) {
                approverCardinatlity.addAll(approverAssociationIds);
            } else {
                approverCardinatlity.add(approverUserIds);
            }

            final Map<String, Object> variables = new HashMap<String, Object>();
            variables.put(ActivitiConstants.APPROVER_CARDINALTITY.getName(), approverCardinatlity);
            variables.put(ActivitiConstants.APPROVER_ASSOCIATION_IDS.getName(), approverAssociationIds);
            variables.put(ActivitiConstants.REQUEST.getName(), new ActivitiJSONStringWrapper(jacksonMapper.writeValueAsString(request)));
            variables.put(ActivitiConstants.TASK_NAME.getName(), description);
            variables.put(ActivitiConstants.TASK_DESCRIPTION.getName(), description);
            variables.put(ActivitiConstants.REQUESTOR.getName(), request.getRequestorUserId());
            variables.put(ActivitiConstants.ASSOCIATION_ID.getName(), request.getUser().getId());
            variables.put(ActivitiConstants.WORKFLOW_NAME.getName(), ActivitiRequestType.EDIT_USER.getKey());
            if (identifier.getCustomActivitiAttributes() != null) {
                variables.putAll(identifier.getCustomActivitiAttributes());
            }

            idmAuditLog = auditLogService.save(idmAuditLog);
            variables.put(ActivitiConstants.AUDIT_LOG_ID.getName(), idmAuditLog.getId());

            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(ActivitiRequestType.EDIT_USER.getKey(), variables);
            idmAuditLog = auditLogService.findById(idmAuditLog.getId());
            for (Map.Entry<String, Object> varEntry : variables.entrySet()) {
                idmAuditLog.addCustomRecord(varEntry.getKey(), (varEntry.getValue() != null) ? varEntry.getValue().toString() : null);
            }

            idmAuditLog.setTargetTask(processInstance.getId(), description);
			/* throws exception if invalid - caught in try/catch */
            //userProfileService.validate(request);

            response.succeed();
            idmAuditLog.succeed();
        } catch (CustomActivitiException e) {
            log.warn("Can't perform task", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            response.setErrorText(e.getMessage());
        } catch (PageTemplateException e) {
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
            response.setCurrentValue(e.getCurrentValue());
            response.setElementName(e.getElementName());
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
        } catch (BasicDataServiceException e) {
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorTokenList(e.getErrorTokenList());
        } catch (ActivitiException e) {
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getMessage());
            idmAuditLog.setException(e);
            log.info("Activiti Exception", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_STATUS);
            response.setErrorText(e.getMessage());
        } catch (Throwable e) {
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getMessage());
            idmAuditLog.setException(e);
            log.error("Error while creating newhire request", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_STATUS);
            response.setErrorText(e.getMessage());
        } finally {
            idmAuditLog = auditLogService.save(idmAuditLog);
        }
        return response;
    }

    private void validateUserRequest(final UserProfileRequestModel request) throws BasicDataServiceException {
        final UserEntity provisionUserValidationObject = userDozerConverter.convertToEntity(request.getUser(), true);
        entityValidator.isValid(provisionUserValidationObject);
        if (CollectionUtils.isNotEmpty(request.getEmails())) {
            for (final EmailAddress bean : request.getEmails()) {
                final EmailAddressEntity entity = emailDozerConverter.convertToEntity(bean, true);
                entityValidator.isValid(entity);
            }
        }
        if (CollectionUtils.isNotEmpty(request.getPhones())) {
            for (final Phone bean : request.getPhones()) {
                final PhoneEntity entity = phoneDozerConverter.convertToEntity(bean, true);
                entityValidator.isValid(entity);
            }
        }
        if (CollectionUtils.isNotEmpty(request.getAddresses())) {
            for (final Address bean : request.getAddresses()) {
                final AddressEntity entity = addressDozerConverter.convertToEntity(bean, true);
                entityValidator.isValid(entity);
            }
        }
    }

    @Override
    @Transactional
    public Response initiateWorkflow(final GenericWorkflowRequest request) {
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(request.getRequestorUserId());
        //idmAuditLog.setAction(AuditAction.INITIATE_WORKFLOW.value());
        idmAuditLog.setAction(request.getActivitiRequestType());
        idmAuditLog.setBaseObject(request);
        idmAuditLog.setSource(AuditSource.WORKFLOW.value());
        final Response response = new Response();
        try {
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
            if(mergeCustomApproverIdsWithApproverAssociations){

                if (CollectionUtils.isNotEmpty(approverUserIds)) {
                    approverCardinatlity = buildApproverCardinatlity(request, approverUserIds);
                }

                if (CollectionUtils.isNotEmpty(approverAssociationIds)) {
                    approverCardinatlity.addAll(approverAssociationIds);
                }
            } else {
                if (CollectionUtils.isNotEmpty(approverAssociationIds)) {
                    approverCardinatlity.addAll(approverAssociationIds);
                } else {
                    approverCardinatlity = buildApproverCardinatlity(request, approverUserIds);
                }
            }



            idmAuditLog.addAttributeAsJson(AuditAttributeName.REQUEST_APPROVER_IDS, approverUserIds, jacksonMapper);
            final Map<String, Object> variables = new HashMap<String, Object>();
            variables.put(ActivitiConstants.WORKFLOW_NAME.getName(), request.getActivitiRequestType());
            variables.put(ActivitiConstants.APPROVER_CARDINALTITY.getName(), approverCardinatlity);
            variables.put(ActivitiConstants.APPROVER_ASSOCIATION_IDS.getName(), approverAssociationIds);
            variables.put(ActivitiConstants.TASK_NAME.getName(), request.getName());
            variables.put(ActivitiConstants.TASK_DESCRIPTION.getName(), request.getDescription());
            variables.put(ActivitiConstants.REQUESTOR.getName(), request.getRequestorUserId());
            variables.put(ActivitiConstants.DELETABLE.getName(), Boolean.valueOf(request.isDeletable()));
            if (request.getAssociationId() != null) {
                variables.put(ActivitiConstants.ASSOCIATION_ID.getName(), request.getAssociationId());
            }
            if (request.getAssociationType() != null) {
                variables.put(ActivitiConstants.ASSOCIATION_TYPE.getName(), request.getAssociationType().getValue());
            }
            if (request.getMemberAssociationId() != null) {
                variables.put(ActivitiConstants.MEMBER_ASSOCIATION_ID.getName(), request.getMemberAssociationId());
            }
            if (request.getMemberAssociationType() != null) {
                variables.put(ActivitiConstants.MEMBER_ASSOCIATION_TYPE.getName(), request.getMemberAssociationType().getValue());
            }
            if (request.getParameters() != null) {
                variables.putAll(request.getParameters());
            }
            if (request.getJsonSerializedParams() != null) {
                for (final String key : request.getJsonSerializedParams().keySet()) {
                    final String value = request.getJsonSerializedParams().get(key);
                    variables.put(key, new ActivitiJSONStringWrapper(value));
                }
            }

            if (identifier.getCustomActivitiAttributes() != null) {
                variables.putAll(identifier.getCustomActivitiAttributes());
            }

            idmAuditLog = auditLogService.save(idmAuditLog);
            variables.put(ActivitiConstants.AUDIT_LOG_ID.getName(), idmAuditLog.getId());

            final ProcessInstance instance = runtimeService.startProcessInstanceByKey(request.getActivitiRequestType(), variables);
            idmAuditLog = auditLogService.findById(idmAuditLog.getId());
            idmAuditLog.setTargetTask(instance.getId(), request.getName());
            for (Map.Entry<String, Object> varEntry : variables.entrySet()) {
                idmAuditLog.addCustomRecord(varEntry.getKey(), (varEntry.getValue() != null) ? varEntry.getValue().toString() : null);
            }

            response.succeed();
            idmAuditLog.succeed();
        } catch (CustomActivitiException e) {
            log.warn("Can't perform task", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            response.setErrorText(e.getMessage());
        } catch (BasicDataServiceException e) {
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
            log.info("Could not initialize task", e);
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
        } catch (ActivitiException e) {
            idmAuditLog.setFailureReason(e.getMessage());
            idmAuditLog.setException(e);
            log.info("Activiti Exception", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_STATUS);
            response.setErrorText(e.getMessage());
        } catch (Throwable e) {
            idmAuditLog.setFailureReason(e.getMessage());
            idmAuditLog.setException(e);
            log.error("Error while creating newhire request", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_STATUS);
            response.setErrorText(e.getMessage());
        } finally {
            idmAuditLog = auditLogService.save(idmAuditLog);
        }
        return response;
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
    @WebMethod
    public Response makeDecision(final ActivitiRequestDecision request) {
        final Response response = new Response();
        final IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(request.getRequestorUserId());
        idmAuditLog.setAction(AuditAction.COMPLETE_WORKFLOW.value());
        idmAuditLog.setBaseObject(request);
        idmAuditLog.setSource(AuditSource.WORKFLOW.value());
        String parentAuditLogId = null;
        try {
            idmAuditLog.addAttributeAsJson(AuditAttributeName.REQUEST, request, jacksonMapper);
            final Task assignedTask = getTaskAssignee(request);
		
        	/* complete the Task in Activiti, passing required parameters */
            final Map<String, Object> variables = new HashMap<String, Object>();
            variables.put(ActivitiConstants.COMMENT.getName(), request.getComment());
            variables.put(ActivitiConstants.IS_TASK_APPROVED.getName(), request.isAccepted());
            variables.put(ActivitiConstants.EXECUTOR_ID.getName(), request.getRequestorUserId());
            if (request.getCustomVariables() != null) {
                variables.putAll(request.getCustomVariables());
            }

            idmAuditLog.setTargetTask(assignedTask.getId(), assignedTask.getName());
            for (Map.Entry<String, Object> varEntry : variables.entrySet()) {
                idmAuditLog.addCustomRecord(varEntry.getKey(), (varEntry.getValue() != null) ? varEntry.getValue().toString() : null);
            }


            final Object auditLogId = taskService.getVariable(assignedTask.getId(), ActivitiConstants.AUDIT_LOG_ID.getName());
            if (auditLogId != null && auditLogId instanceof String) {
                parentAuditLogId = (String) auditLogId;
            }

            taskService.complete(assignedTask.getId(), variables);
            response.succeed();
            idmAuditLog.succeed();

        } catch (CustomActivitiException e) {
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
            idmAuditLog.fail();
            log.warn("Can't perform task", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            response.setErrorText(e.getMessage());
        } catch (ActivitiException e) {
            idmAuditLog.setFailureReason(e.getMessage());
            idmAuditLog.setException(e);
            idmAuditLog.fail();
            log.info("Activiti Exception", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.INTERNAL_ERROR);
            response.setErrorText(e.getMessage());
        } catch (Throwable e) {
            idmAuditLog.setFailureReason(e.getMessage());
            idmAuditLog.setException(e);
            idmAuditLog.fail();
            log.error("Error while creating newhire request", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.INTERNAL_ERROR);
            response.setErrorText(e.getMessage());
        } finally {
            if (parentAuditLogId != null) {
                IdmAuditLog parent = auditLogService.findById(parentAuditLogId);
                if (parent != null) {
                    parent.addChild(idmAuditLog);
                    idmAuditLog.addParent(parent);
                    parent = auditLogService.save(parent);
                }
            }
        }
        return response;
    }

    private Task getTaskAssignee(final String taskId, final String userId) {
        final List<Task> taskList = taskService.createTaskQuery().taskId(taskId).taskAssignee(userId).list();
        if (CollectionUtils.isEmpty(taskList)) {
            throw new ActivitiException("No tasks for user..");
        }

        return taskList.get(0);
    }

    private Task getTaskAssignee(final ActivitiRequestDecision newHireRequest) throws ActivitiException {
        return getTaskAssignee(newHireRequest.getTaskId(), newHireRequest.getRequestorUserId());
    }


    @Override
    @WebMethod
    @Transactional
    public int getNumOfAssignedTasks(String userId) {
        return (int) taskService.createTaskQuery().taskAssignee(userId).count();
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
                taskListWrapper.addAssignedTasks(assignedTasks, runtimeService, loginService);
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
                taskListWrapper.addAssignedTasks(assignedTasks, runtimeService, loginService);
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
    @WebMethod
    @Transactional
    public int getNumOfCandidateTasks(String userId) {
        return (int) taskService.createTaskQuery().taskCandidateUser(userId).count();
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
                taskListWrapper.addCandidateTasks(candidateTasks, runtimeService, loginService);
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
    @WebMethod
    @Transactional
    public TaskListWrapper getTasksForUser(final String userId, final int from, final int size) {
        final TaskListWrapper taskListWrapper = new TaskListWrapper();
        final List<Task> assignedTasks = taskService.createTaskQuery().taskAssignee(userId).listPage(from, size);
        final List<Task> candidateTasks = taskService.createTaskQuery().taskCandidateUser(userId).listPage(from, size);
        Collections.sort(assignedTasks, taskCreatedTimeComparator);
        Collections.sort(candidateTasks, taskCreatedTimeComparator);
        taskListWrapper.addAssignedTasks(assignedTasks, runtimeService, loginService);
        taskListWrapper.addCandidateTasks(candidateTasks, runtimeService, loginService);
        return taskListWrapper;
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
        final List<Task> candidateTasks = query.taskCandidateUser(userId).listPage(from, size);
        Collections.sort(candidateTasks, taskCreatedTimeComparator);
        taskListWrapper.addCandidateTasks(candidateTasks, runtimeService, loginService);
        if(description != null && taskListWrapper.getCandidateTasks() != null){
            List<TaskWrapper> results = new ArrayList<TaskWrapper>();
            for(TaskWrapper wrapper : taskListWrapper.getCandidateTasks()) {
                if(wrapper.getDescription().toLowerCase().contains(description.toLowerCase())) {
                    results.add(wrapper);
                }
            }
            taskListWrapper.setCandidateTasks(results);

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
        final List<Task> assignedTasks = query.taskAssignee(userId).listPage(from, size);
        Collections.sort(assignedTasks, taskCreatedTimeComparator);
        taskListWrapper.addAssignedTasks(assignedTasks, runtimeService, loginService);
        if(description != null && taskListWrapper.getAssignedTasks() != null){
            List<TaskWrapper> results = new ArrayList<TaskWrapper>();
            for(TaskWrapper wrapper : taskListWrapper.getAssignedTasks()) {
                if(wrapper.getDescription().toLowerCase().contains(description.toLowerCase())) {
                    results.add(wrapper);
                }
            }

            taskListWrapper.setAssignedTasks(results);

        } else if(requesterId != null && taskListWrapper.getAssignedTasks() != null){
            List<TaskWrapper> results = new ArrayList<TaskWrapper>();
            for(TaskWrapper wrapper : taskListWrapper.getAssignedTasks()) {
                if (wrapper.getOwner() != null) { // owner id null in self registration case
                    if (wrapper.getOwner().equals(requesterId)) {
                        results.add(wrapper);
                    }
                }
            }

            taskListWrapper.setAssignedTasks(results);

        }

        return taskListWrapper;
    }

    @Override
    @WebMethod
    @Transactional
    public List<TaskWrapper> getTasksForMemberAssociation(String memberAssociationId) {
        List<TaskWrapper> memberAssociationTaskList = new LinkedList<TaskWrapper>();
        final List<Task> taskList = taskService.createTaskQuery().processVariableValueEquals(ActivitiConstants.MEMBER_ASSOCIATION_ID.getName(), memberAssociationId).list();
        if(CollectionUtils.isNotEmpty(taskList)) {
            for(final Task task : taskList) {
                memberAssociationTaskList.add(new TaskWrapper(task, runtimeService,loginService));
            }
        }
        return memberAssociationTaskList;
    }

    @Override
    @WebMethod
    @Transactional
    public TaskWrapper getTask(String taskId) {
        TaskWrapper retVal = null;
        final List<Task> taskList = taskService.createTaskQuery().taskId(taskId).list();
        if (CollectionUtils.isNotEmpty(taskList)) {
            retVal = new TaskWrapper(taskList.get(0), runtimeService,loginService);
        }
        return retVal;
    }

    private static final class TaskCreateDateSorter implements Comparator<Task> {

        @Override
        public int compare(Task o1, Task o2) {
            try {
                return o2.getCreateTime().compareTo(o1.getCreateTime());
            } catch (Throwable e) { /* can't happen, but... */
                log.warn("Sorting problem", e);
                return 0;
            }
        }

    }

    @Override
    public TaskWrapper getTaskFromHistory(final String executionId, final String taskId) {
        TaskWrapper retVal = null;
        final HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery();
        if (StringUtils.isNotBlank(executionId)) {
            query.executionId(executionId);
        } else if (StringUtils.isNotBlank(taskId)) {
            query.taskId(taskId);
        } else {
            throw new IllegalArgumentException("Execution ID and Task ID are null");
        }
        final List<HistoricTaskInstance> instances = query.list();
        if (CollectionUtils.isNotEmpty(instances)) {
            retVal = new TaskWrapper(instances.get(0));
        }
        return retVal;
    }

    @Override
    @Transactional
    public List<TaskHistoryWrapper> getHistoryForInstance(final String executionId) {
        final List<TaskHistoryWrapper> retVal = new LinkedList<TaskHistoryWrapper>();
        if (StringUtils.isNotBlank(executionId)) {
            final List<HistoricTaskInstance> instances = historyService.createHistoricTaskInstanceQuery().executionId(executionId).list();
            final Map<String, HistoricTaskInstance> taskDefinitionMap = new HashMap<String, HistoricTaskInstance>();
            if (CollectionUtils.isNotEmpty(instances)) {
                for (HistoricTaskInstance instance : instances) {
                    taskDefinitionMap.put(instance.getTaskDefinitionKey(), instance);
                }
            }

            final HistoricActivityInstanceQuery query = historyService.createHistoricActivityInstanceQuery().executionId(executionId);
            final List<HistoricActivityInstance> activityList = query.list();
            if (CollectionUtils.isNotEmpty(activityList)) {
                for (int i = 0; i < activityList.size(); i++) {
                    final HistoricActivityInstance instance = activityList.get(i);
                    final TaskHistoryWrapper wrapper = new TaskHistoryWrapper(instance);
                    if (taskDefinitionMap.containsKey(wrapper.getActivityId())) {
                        wrapper.setTask(new TaskWrapper(taskDefinitionMap.get(wrapper.getActivityId())));
                    }
                    if (StringUtils.isNotBlank(wrapper.getAssigneeId())) {
                        final UserEntity user = userDataService.getUser(wrapper.getAssigneeId());
                        wrapper.setUserInfo(user);
                    }
                    retVal.add(wrapper);

                    if (i < activityList.size() - 1) {
                        wrapper.addNextTask(activityList.get(i + 1).getId());
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
                retVal.add(new TaskWrapper(historyInstance));
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
    public Response deleteTask(String taskId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            taskService.deleteTask(taskId, true);
        } catch (ActivitiException e) {
            log.info("Activiti Exception", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_STATUS);
            response.setErrorText(e.getMessage());
        } catch (Throwable e) {
            log.error("Error while deleting task", e);
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
        final IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(userId);
        idmAuditLog.setAction(AuditAction.TERMINATED_WORKFLOW.value());
        idmAuditLog.setSource(AuditSource.WORKFLOW.value());
        String parentAuditLogId = null;
        try {
            final Task task = getTaskAssignee(taskId, userId);
            idmAuditLog.setTargetTask(task.getId(), task.getName());
            final Object auditLogId = taskService.getVariable(task.getId(), ActivitiConstants.AUDIT_LOG_ID.getName());
            if (auditLogId != null && auditLogId instanceof String) {
                parentAuditLogId = (String) auditLogId;
            }

            taskService.deleteTask(task.getId(), true);
            idmAuditLog.succeed();
        } catch (ActivitiException e) {
            log.info("Activiti Exception", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_STATUS);
            response.setErrorText(e.getMessage());
            idmAuditLog.setFailureReason(e.getMessage());
            idmAuditLog.setException(e);
            idmAuditLog.fail();
        } catch (Throwable e) {
            log.error("Error while creating newhire request", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.USER_STATUS);
            response.setErrorText(e.getMessage());
            idmAuditLog.setFailureReason(e.getMessage());
            idmAuditLog.setException(e);
            idmAuditLog.fail();
        } finally {
            if (parentAuditLogId != null) {
                IdmAuditLog parent = auditLogService.findById(parentAuditLogId);
                if (parent != null) {
                    parent.addChild(idmAuditLog);
                    idmAuditLog.addParent(parent);
                    parent = auditLogService.save(parent);
                }
            }
        }
        return response;
    }

    @Override
    @Transactional(readOnly=true)
    public List<String> getApproverUserIds(List<String> associationIds, final String targetUserId) {
        return activitiHelper.getCandidateUserIds(associationIds, targetUserId, null);
    }
}
