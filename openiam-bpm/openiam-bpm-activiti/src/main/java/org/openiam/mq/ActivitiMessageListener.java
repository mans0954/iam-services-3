package org.openiam.mq;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.bpm.activiti.dispatcher.*;
import org.openiam.mq.constants.ActivitiAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.listener.AbstractRabbitMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 11/08/16.
 */
@Component
public class ActivitiMessageListener extends AbstractRabbitMQListener<ActivitiAPI> {
    @Autowired
    private InitiateUserProfileRequestDispatcher initiateUserProfileRequestDispatcher;
    @Autowired
    private ClaimRequestDispatcher claimRequestDispatcher;
    @Autowired
    private InitiateWorkflowDispatcher initiateWorkflowDispatcher;
    @Autowired
    private ProcessInstanceIdByExecutionIdDispatcher processInstanceIdByExecutionIdDispatcher;
    @Autowired
    private MakeDecisionDispatcher makeDecisionDispatcher;
    @Autowired
    private GetTaskDispatcher getTaskDispatcher;
    @Autowired
    private TaskFromHistoryDispatcher taskFromHistoryDispatcher;
    @Autowired
    private NumTaskWithFilterDispatcher numTaskWithFilterDispatcher;
    @Autowired
    private GetTasksWithFilterDispatcher getTasksWithFilterDispatcher;
    @Autowired
    private HistoryForInstanceDispatcher historyForInstanceDispatcher;
    @Autowired
    private GetHistoryDispatcher getHistoryDispatcher;
    @Autowired
    private CountHistoryDispatcher countHistoryDispatcher;
    @Autowired
    private DeleteUnclaimTaskDispatcher deleteUnclaimTaskDispatcher;
    @Autowired
    private GetTasksForUserDispatcher getTasksForUserDispatcher;
    @Autowired
    private FindTasksDispatcher findTasksDispatcher;
    @Autowired
    private CountTasksDispatcher countTasksDispatcher;

    public ActivitiMessageListener() {
        super(OpenIAMQueue.ActivitiQueue);
    }

    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, ActivitiAPI> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        ActivitiAPI apiName = message.getRequestApi();
        switch (apiName){
            case InitiateNewHireRequest:
            case InitiateEditUserWorkflow:
                addTask(initiateUserProfileRequestDispatcher, correlationId, message, apiName, isAsync);
                break;
            case ClaimRequest:
                addTask(claimRequestDispatcher, correlationId, message, apiName, isAsync);
                break;
            case InitiateWorkflow:
                addTask(initiateWorkflowDispatcher, correlationId, message, apiName, isAsync);
                break;
            case ProcessInstanceIdByExecutionId:
                addTask(processInstanceIdByExecutionIdDispatcher, correlationId, message, apiName, isAsync);
                break;
            case MakeDecision:
                addTask(makeDecisionDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetTask:
                addTask(getTaskDispatcher, correlationId, message, apiName, isAsync);
                break;
            case TaskFromHistory:
                addTask(taskFromHistoryDispatcher, correlationId, message, apiName, isAsync);
                break;
            case NumOfAssignedTasksWithFilter:
            case NumOfCandidateTasksWithFilter:
                addTask(numTaskWithFilterDispatcher, correlationId, message, apiName, isAsync);
                break;
            case TasksForCandidateUserWithFilter:
            case TasksForAssignedUserWithFilter:
                addTask(getTasksWithFilterDispatcher, correlationId, message, apiName, isAsync);
                break;
            case HistoryForInstance:
                addTask(historyForInstanceDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetHistory:
                addTask(getHistoryDispatcher, correlationId, message, apiName, isAsync);
                break;
            case Count:
                addTask(countHistoryDispatcher, correlationId, message, apiName, isAsync);
                break;
            case DeleteTask:
            case UnclaimTask:
            case DeleteTasksForUser:
                addTask(deleteUnclaimTaskDispatcher, correlationId, message, apiName, isAsync);
                break;
            case GetTasksForUser:
                addTask(getTasksForUserDispatcher, correlationId, message, apiName, isAsync);
                break;
            case FindTasks:
                addTask(findTasksDispatcher, correlationId, message, apiName, isAsync);
                break;
            case CountTasks:
                addTask(countTasksDispatcher, correlationId, message, apiName, isAsync);
                break;
            default:
                break;
        }
    }
}
