package org.openiam.mq;

import org.openiam.base.request.*;
import org.openiam.base.response.data.BatchTaskResponse;
import org.openiam.base.response.data.IntResponse;
import org.openiam.base.response.data.StringResponse;
import org.openiam.base.response.list.BatchTaskListResponse;
import org.openiam.base.response.list.BatchTaskScheduleListResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.BatchTaskScheduleSearchBean;
import org.openiam.idm.searchbeans.BatchTaskSearchBean;
import org.openiam.idm.srvc.batch.service.BatchService;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.api.BatchTaskAPI;
import org.openiam.mq.constants.queue.common.BatchTaskQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 25/10/16.
 */
@Component
@RabbitListener(id="batchTaskListener",
        queues = "#{BatchTaskQueue.name}",
        containerFactory = "commonRabbitListenerContainerFactory")
public class BatchTaskListener extends AbstractListener<BatchTaskAPI> {
    @Autowired
    protected BatchService batchService;

    @Autowired
    public BatchTaskListener(BatchTaskQueue queue) {
        super(queue);
    }

    protected RequestProcessor<BatchTaskAPI, BaseSearchServiceRequest> getSearchRequestProcessor(){
        return new RequestProcessor<BatchTaskAPI, BaseSearchServiceRequest>(){
            @Override
            public Response doProcess(BatchTaskAPI api, BaseSearchServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api) {
                    case GetSchedulesForTask:
                        response = new BatchTaskScheduleListResponse();
                        ((BatchTaskScheduleListResponse) response).setList(batchService.getSchedulesForTask(((BaseSearchServiceRequest<BatchTaskScheduleSearchBean>) request).getSearchBean(), request.getFrom(), request.getSize()));
                        break;
                    case GetNumOfSchedulesForTask:
                        response = new IntResponse();
                        ((IntResponse)response).setValue(batchService.count(((BaseSearchServiceRequest<BatchTaskScheduleSearchBean>) request).getSearchBean()));
                        break;
                    case FindBeans:
                        response = new BatchTaskListResponse();
                        ((BatchTaskListResponse) response).setList(batchService.findBeans(((BaseSearchServiceRequest<BatchTaskSearchBean>) request).getSearchBean(), request.getFrom(), request.getSize()));
                        break;
                    case Count:
                        response = new IntResponse();
                        ((IntResponse)response).setValue(batchService.count(((BaseSearchServiceRequest<BatchTaskSearchBean>) request).getSearchBean()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }
    protected RequestProcessor<BatchTaskAPI, IdServiceRequest> getGetRequestProcessor(){
        return new RequestProcessor<BatchTaskAPI, IdServiceRequest>(){
            @Override
            public Response doProcess(BatchTaskAPI api, IdServiceRequest request) throws BasicDataServiceException {
                BatchTaskResponse response = new BatchTaskResponse();
                response.setValue(batchService.findDto(request.getId()));
                return response;
            }
        };
    }
    protected RequestProcessor<BatchTaskAPI, BaseCrudServiceRequest> getCrudRequestProcessor(){
        return new RequestProcessor<BatchTaskAPI, BaseCrudServiceRequest>(){
            @Override
            public Response doProcess(BatchTaskAPI api, BaseCrudServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api) {
                    case Save:
                        response = new StringResponse();
                        ((StringResponse)response).setValue(batchService.save(((BatchTaskSaveRequest)request).getObject(), ((BatchTaskSaveRequest)request).isPurgeNonExecutedTasks()));
                        break;
                    case Delete:
                        response = new Response();
                        batchService.delete(request.getObject().getId());
                        break;
                    case DeleteScheduledTask:
                        response = new Response();
                        batchService.deleteScheduledTask(request.getObject().getId());
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }
    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) BatchTaskAPI api, StartBatchTaskRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<BatchTaskAPI, StartBatchTaskRequest>(){
            @Override
            public Response doProcess(BatchTaskAPI api, StartBatchTaskRequest request) throws BasicDataServiceException {
                switch (api){
                    case Run:
                        batchService.run(request.getId(), request.isSynchronous());
                        break;
                    case Schedule:
                        batchService.schedule(request.getId(), request.getWhen());
                        break;

                }
                return new Response();
            }
        });
    }
}
