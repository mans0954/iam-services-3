package org.openiam.bpm.utils;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.response.TaskHistoryWrapper;
import org.openiam.base.response.TaskWrapper;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;

import java.util.*;

/**
 * Created by alexander on 17/08/16.
 */
public class ActivitiUtils {
    private static final Log LOG = LogFactory.getLog(ActivitiUtils.class);


    public static TaskWrapper getTaskWrapper(final Task task, final RuntimeService runtimeService, LoginDataService login){
        TaskWrapper wrapper = new TaskWrapper();
        wrapper.setId(task.getId());
        wrapper.setName(task.getName());
        wrapper.setOwner(task.getOwner());
        wrapper.setPriority(task.getPriority());
        wrapper.setProcessDefinitionId(task.getProcessDefinitionId());
        wrapper.setProcessInstanceId(task.getProcessInstanceId());
        wrapper.setTaskDefinitionKey(task.getTaskDefinitionKey());
        wrapper.setParentTaskId(task.getParentTaskId());
        wrapper.setAssignee(task.getAssignee());
        wrapper.setCreatedTime(task.getCreateTime());
        wrapper.setDescription(task.getDescription());
        wrapper.setDueDate(task.getDueDate());
        wrapper.setExecutionId(task.getExecutionId());

        setCustomVariables(wrapper, runtimeService, login);
        return wrapper;
    }
    public static TaskWrapper getTaskWrapper(final Task task, final RuntimeService runtimeService){
        return getTaskWrapper(task, runtimeService, null);
    }

    public static TaskWrapper getTaskWrapper(final HistoricTaskInstance historyInstance){
        TaskWrapper wrapper = new TaskWrapper();
        wrapper.setId(historyInstance.getId());
        wrapper.setName(historyInstance.getName());
        wrapper.setOwner(historyInstance.getOwner());
        wrapper.setPriority(historyInstance.getPriority());
        wrapper.setProcessDefinitionId(historyInstance.getProcessDefinitionId());
        wrapper.setProcessInstanceId(historyInstance.getProcessInstanceId());
        wrapper.setTaskDefinitionKey(historyInstance.getTaskDefinitionKey());
        wrapper.setParentTaskId(historyInstance.getParentTaskId());
        wrapper.setAssignee(historyInstance.getAssignee());
        wrapper.setCreatedTime(historyInstance.getCreateTime());
        wrapper.setDescription(historyInstance.getDescription());
        wrapper.setDueDate(historyInstance.getDueDate());
        wrapper.setExecutionId(historyInstance.getExecutionId());

        return wrapper;
    }
    public static TaskHistoryWrapper getTaskHistoryWrapper(final HistoricActivityInstance instance){
        TaskHistoryWrapper wrapper = new TaskHistoryWrapper();

        wrapper.setId(instance.getId());
        wrapper.setActivityId(instance.getActivityId());
        wrapper.setActivityName(instance.getActivityName());
        wrapper.setActivityType(instance.getActivityType());
        wrapper.setAssigneeId(instance.getAssignee());
        wrapper.setDuration(instance.getDurationInMillis());
        wrapper.setEndTime(instance.getEndTime());
        wrapper.setExecutionId(instance.getExecutionId());
        wrapper.setProcessDefinitionId(instance.getProcessDefinitionId());
        wrapper.setProcessInstanceId(instance.getProcessInstanceId());
        wrapper.setStartTime(instance.getStartTime());
        wrapper.setTaskId(instance.getTaskId());
        wrapper.setCalledProcessInstanceId(instance.getCalledProcessInstanceId());
        wrapper.setTenantId(instance.getTenantId());

        return wrapper;
    }

    public static List<TaskWrapper> wrapTaskList(final List<Task> taskList, final RuntimeService runtimeService){
        return wrapTaskList(taskList, runtimeService, null);
    }

    public static List<TaskWrapper> wrapTaskList(final List<Task> taskList, final RuntimeService runtimeService, LoginDataService loginService){
        List<TaskWrapper> wrapperList = new LinkedList<TaskWrapper>();
        if(CollectionUtils.isNotEmpty(taskList)) {
            for(final Task task : taskList) {
                wrapperList.add(getTaskWrapper(task, runtimeService,loginService));
            }
        }
        return wrapperList;
    }

    private static void setCustomVariables(TaskWrapper wrapper, final RuntimeService runtimeService, LoginDataService loginService) {
        if(StringUtils.isNotEmpty(wrapper.getExecutionId())) {
            try {
                final Map<String, Object> customVariables = runtimeService.getVariables(wrapper.getExecutionId());
                if(customVariables != null) {
                    if(customVariables.containsKey(ActivitiConstants.REQUEST_METADATA_MAP.getName())) {
                        wrapper.setRequestMetadataMap((LinkedHashMap<String, String>)customVariables.get(ActivitiConstants.REQUEST_METADATA_MAP.getName()));
                    }

                    if (customVariables.containsKey(ActivitiConstants.REQUESTOR_NAME.getName())) {
                        wrapper.setName((String) customVariables.get(ActivitiConstants.REQUESTOR_NAME.getName()));
                    } else {
                        if (customVariables.containsKey(ActivitiConstants.REQUESTOR.getName()) && loginService != null) {
                            LoginEntity loginEntity = loginService.getPrimaryIdentity((String) customVariables.get(ActivitiConstants.REQUESTOR.getName()));
                            wrapper.setName(loginEntity != null ? loginEntity.getLogin() : "");
                        }
                    }

                    if(customVariables.containsKey(ActivitiConstants.EMPLOYEE_ID.getName())) {
                        wrapper.setEmployeeId((String)customVariables.get(ActivitiConstants.EMPLOYEE_ID.getName()));
                    }
                    
                    if(customVariables.containsKey(ActivitiConstants.WORKFLOW_RESOURCE_ID.getName())) {
                    	wrapper.setResourceId((String)customVariables.get(ActivitiConstants.WORKFLOW_RESOURCE_ID.getName()));
                    }

                    if(customVariables.containsKey(ActivitiConstants.ATTESTATION_URL.getName())) {
                        String customObjectURI = (String)customVariables.get(ActivitiConstants.ATTESTATION_URL.getName());
                        customObjectURI = new StringBuilder(customObjectURI).append(String.format("?id=%s&taskId=%s", wrapper.getEmployeeId(), wrapper.getId())).toString();
                        wrapper.setCustomObjectURI(customObjectURI);
                    }

                    if(customVariables.containsKey(ActivitiConstants.DELETABLE.getName())) {
                        wrapper.setDeletable(((Boolean)customVariables.get(ActivitiConstants.DELETABLE.getName())).booleanValue());
                    }

                    if(customVariables.containsKey(ActivitiConstants.WORKFLOW_NAME.getName())) {
                        wrapper.setWorkflowName((String)customVariables.get(ActivitiConstants.WORKFLOW_NAME.getName()));
                    }

                    if(customVariables.containsKey(ActivitiConstants.ASSOCIATION_TYPE.getName())) {
                        wrapper.setAssociationType((String)customVariables.get(ActivitiConstants.ASSOCIATION_TYPE.getName()));
                    }

                    if(customVariables.containsKey(ActivitiConstants.ASSOCIATION_ID.getName())) {
                        wrapper.setAssociationId((String)customVariables.get(ActivitiConstants.ASSOCIATION_ID.getName()));
                    }

                    if(customVariables.containsKey(ActivitiConstants.MEMBER_ASSOCIATION_TYPE.getName())) {
                        wrapper.setMemberAssociationType((String)customVariables.get(ActivitiConstants.MEMBER_ASSOCIATION_TYPE.getName()));
                    }

                    if(customVariables.containsKey(ActivitiConstants.MEMBER_ASSOCIATION_ID.getName())) {
                        wrapper.setMemberAssociationId((String)customVariables.get(ActivitiConstants.MEMBER_ASSOCIATION_ID.getName()));
                    }

                    if(customVariables.containsKey(ActivitiConstants.START_DATE.getName())) {
                        wrapper.setStartMembershipDate((Date)customVariables.get(ActivitiConstants.START_DATE.getName()));
                    }
                    if(customVariables.containsKey(ActivitiConstants.END_DATE.getName())) {
                        wrapper.setEndMembershipDate((Date)customVariables.get(ActivitiConstants.END_DATE.getName()));
                    }
                    if(customVariables.containsKey(ActivitiConstants.USER_NOTE.getName())) {
                        wrapper.setUserNotes((String)customVariables.get(ActivitiConstants.USER_NOTE.getName()));
                    }
                    if(customVariables.containsKey(ActivitiConstants.ATTESTATION_MANAGED_SYS_RESOURCES.getName())) {
                        wrapper.setAttestationManagedSysFilter((List<String>)customVariables.get(ActivitiConstants.ATTESTATION_MANAGED_SYS_RESOURCES.getName()));
                    }
                }
            } catch(ActivitiException e) {
                LOG.warn(String.format("Could not fetch variables for Execution ID: %s.  Changes are that the task is completed.", wrapper.getExecutionId()));
            } catch(Throwable e) {
            }
        }
    }
}
