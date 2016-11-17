package org.openiam.mq;

import org.openiam.base.request.*;
import org.openiam.base.response.*;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.bpm.activiti.ActivitiDataService;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.api.ActivitiAPI;
import org.openiam.mq.constants.queue.activiti.ActivitiServiceQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 11/08/16.
 */
@Component
@RabbitListener(id="activitiMessageListener",
        queues = "#{ActivitiServiceQueue.name}",
        containerFactory = "activitiRabbitListenerContainerFactory")
public class ActivitiMessageListener extends AbstractListener<ActivitiAPI> {
    @Autowired
    private ActivitiDataService activitiDataService;

    @Autowired
    public ActivitiMessageListener(ActivitiServiceQueue queue) {
        super(queue);
    }

    protected RequestProcessor<ActivitiAPI, IdServiceRequest> getGetRequestProcessor(){
        return new RequestProcessor<ActivitiAPI, IdServiceRequest>(){
            @Override
            public Response doProcess(ActivitiAPI api, IdServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case ProcessInstanceIdByExecutionId:
                        response = new StringResponse();
                        ((StringResponse)response).setValue(activitiDataService.getProcessInstanceIdByExecutionId(request.getId()));
                        break;
                    case GetTask:
                        response = new TaskWrapperResponse();
                        ((TaskWrapperResponse)response).setValue(activitiDataService.getTask(request.getId()));
                        break;
                    case HistoryForInstance:
                        response = new TaskHistoryListResponse();
                        ((TaskHistoryListResponse)response).setList(activitiDataService.getHistoryForInstance(request.getId()));
                        break;
                    case GetApproverUserIds:
                        response = new StringListResponse();
                        ((StringListResponse)response).setList(activitiDataService.getApproverUserIds(((ApproverUserRequest)request).getAssociationIds(), request.getId()));
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }
    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) ActivitiAPI api, UserProfileServiceRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<ActivitiAPI, UserProfileServiceRequest>(){
            @Override
            public Response doProcess(ActivitiAPI api, UserProfileServiceRequest request) throws BasicDataServiceException {
                switch (api){
                    case InitiateNewHireRequest:
                        return activitiDataService.initiateNewHireRequest(((NewUserProfileRequestModel) request.getModel()));
                    case InitiateEditUserWorkflow:
                        return activitiDataService.initiateEditUserWorkflow(request.getModel());
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
            }
        });
    }
    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) ActivitiAPI api, ActivitiClaimRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<ActivitiAPI, ActivitiClaimRequest>(){
            @Override
            public Response doProcess(ActivitiAPI api, ActivitiClaimRequest request) throws BasicDataServiceException {
                activitiDataService.claimRequest(request);
                return new Response();
            }
        });
    }
    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) ActivitiAPI api, GenericWorkflowRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<ActivitiAPI, GenericWorkflowRequest>(){
            @Override
            public Response doProcess(ActivitiAPI api, GenericWorkflowRequest request) throws BasicDataServiceException {
                return activitiDataService.initiateWorkflow(request);
            }
        });
    }
    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) ActivitiAPI api, ActivitiRequestDecision request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<ActivitiAPI, ActivitiRequestDecision>(){
            @Override
            public Response doProcess(ActivitiAPI api, ActivitiRequestDecision request) throws BasicDataServiceException {
                activitiDataService.makeDecision(request);
                return new Response(ResponseStatus.SUCCESS);
            }
        });
    }
    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) ActivitiAPI api, ActivitiFilterRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<ActivitiAPI, ActivitiFilterRequest>(){
            @Override
            public Response doProcess(ActivitiAPI api, ActivitiFilterRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case TaskFromHistory:
                        response = new TaskWrapperResponse();
                        ((TaskWrapperResponse)response).setValue(activitiDataService.getTaskFromHistory(request.getExecutionId(), request.getTaskId()));
                        break;
                    case NumOfAssignedTasksWithFilter:
                        response = new IntResponse();
                        ((IntResponse)response).setValue(activitiDataService.getNumOfAssignedTasksWithFilter(request.getUserId(),request.getDescription(),request.getRequesterId(),request.getFromDate(), request.getToDate()));
                        break;
                    case NumOfCandidateTasksWithFilter:
                        response = new IntResponse();
                        ((IntResponse)response).setValue(activitiDataService.getNumOfCandidateTasksWithFilter(request.getUserId(),request.getDescription(),request.getFromDate(), request.getToDate()));
                        break;
                    case TasksForCandidateUserWithFilter:
                        response = new TaskListWrapperResponse();
                        ((TaskListWrapperResponse)response).setValue(activitiDataService.getTasksForCandidateUserWithFilter(request.getUserId(),request.getFrom(),request.getSize(),request.getDescription(),request.getFromDate(), request.getToDate()));
                        break;
                    case TasksForAssignedUserWithFilter:
                        response = new TaskListWrapperResponse();
                        ((TaskListWrapperResponse)response).setValue(activitiDataService.getTasksForAssignedUserWithFilter(request.getUserId(),request.getFrom(),request.getSize(),request.getDescription(),request.getRequesterId(),request.getFromDate(), request.getToDate()));
                        break;
                    case DeleteTask:
                        response=new Response();
                        activitiDataService.deleteTask(request.getTaskId(), request.getUserId());
                        break;
                    case UnclaimTask:
                        response=new Response();
                        activitiDataService.unclaimTask(request.getTaskId(), request.getUserId());
                        break;
                    case DeleteTasksForUser:
                        response=new Response();
                        activitiDataService.deleteTasksForUser(request.getUserId());
                        break;
                    case GetTasksForUser:
                        response = new TaskListWrapperResponse();
                        ((TaskListWrapperResponse)response).setValue(activitiDataService.getTasksForUser(request.getUserId(), request.getFrom(), request.getSize()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        });
    }
    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) ActivitiAPI api, HistorySearchRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<ActivitiAPI, HistorySearchRequest>(){
            @Override
            public Response doProcess(ActivitiAPI api, HistorySearchRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case GetHistory:
                        response = new TaskListResponse();
                        ((TaskListResponse)response).setList(activitiDataService.getHistory(request.getSearchBean(), request.getFrom(), request.getSize()));
                        break;
                    case Count:
                        response = new IntResponse();
                        ((IntResponse)response).setValue(activitiDataService.count(request.getSearchBean()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        });
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) ActivitiAPI api, TaskSearchRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<ActivitiAPI, TaskSearchRequest>(){
            @Override
            public Response doProcess(ActivitiAPI api, TaskSearchRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case FindTasks:
                        response = new TaskListResponse();
                        ((TaskListResponse)response).setList(activitiDataService.findTasks(request.getSearchBean(), request.getFrom(), request.getSize()));
                        break;
                    case CountTasks:
                        response = new IntResponse();
                        ((IntResponse)response).setValue(activitiDataService.countTasks(request.getSearchBean()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        });
    }
}
