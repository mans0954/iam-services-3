package org.openiam.mq;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.request.*;
import org.openiam.base.request.model.AuditLogBatchContainer;
import org.openiam.base.response.*;
import org.openiam.base.response.data.IdmAuditLogResponse;
import org.openiam.base.response.data.IntResponse;
import org.openiam.base.response.list.IdmAuditLogListResponse;
import org.openiam.base.response.list.StringListResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.domain.AuditLogTargetEntity;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogCustomEntity;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.api.AuditLogAPI;
import org.openiam.mq.constants.queue.audit.AuditLogQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * Created by alexander on 01/08/16.
 */
@Component
@RabbitListener(id="auditLogListener",
        queues = "#{AuditLogQueue.name}",
        containerFactory = "auditRabbitListenerContainerFactory")
public class AuditLogListener extends AbstractListener<AuditLogAPI> {
    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    public AuditLogListener(AuditLogQueue queue) {
        super(queue);
    }

    private AuditLogBatchContainer batchContainer;

    @Autowired
    public ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    @Qualifier("transactionTemplate")
    private TransactionTemplate transactionTemplate;


    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) AuditLogAPI api, IdmAuditLogRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new RequestProcessor<AuditLogAPI, IdmAuditLogRequest>(){
            @Override
            public Response doProcess(AuditLogAPI api, IdmAuditLogRequest request) throws BasicDataServiceException {
                AuditLogResponse response = new AuditLogResponse();
                IdmAuditLogEntity event = process(request);
                response.setEvent(event);
                return response;
            }
        });
    }

    private IdmAuditLogEntity process(final IdmAuditLogRequest request) {
        if(request.isAsych()){
            if(batchContainer==null){
                batchContainer = new AuditLogBatchContainer();
            }
            if(batchContainer.isFull()){
                final List<IdmAuditLogEntity> eventList = batchContainer.getEventList();
                taskExecutor.execute(() ->
                        transactionTemplate.execute(new TransactionCallback<Void>() {
                        @Override
                        public Void doInTransaction(TransactionStatus status) {
                            for (IdmAuditLogEntity event : eventList) {
                                doInProcess(event);
                            }
                            return null;
                        }
                    }));
                // create a new container for the next batch;
                batchContainer = new AuditLogBatchContainer();
            } else {
                batchContainer.addEvent(request.getLogEntity());
            }
            return null;
        } else {
           return doInProcess(request.getLogEntity());
        }
    }

    private IdmAuditLogEntity doInProcess(IdmAuditLogEntity event){
    	if(event != null) {
	        if (StringUtils.isNotEmpty(event.getId())) {
	            final IdmAuditLogEntity srcLog = auditLogService.findById(event.getId());
	            if (srcLog != null) {
	
	                for(IdmAuditLogCustomEntity customLog : event.getCustomRecords()) {
	                    if(!srcLog.getCustomRecords().contains(customLog)){
	                        srcLog.addCustomRecord(customLog.getKey(),customLog.getValue());
	                    }
	                }
	                for(IdmAuditLogEntity newChildren : event.getChildLogs()) {
	                    if(!srcLog.getChildLogs().contains(newChildren)) {
	                        srcLog.addChild(newChildren);
	                    }
	                }
	                for(AuditLogTargetEntity newTarget : event.getTargets()) {
	                    if(!srcLog.getTargets().contains(newTarget)) {
	                        srcLog.addTarget(newTarget.getId(), newTarget.getTargetType(), newTarget.getObjectPrincipal());
	                    }
	                }
	
	                event =auditLogService.save(srcLog);
	            }
	        } else {
	            event =auditLogService.save(event);
	        }
    	}
        return event;
    }

    protected RequestProcessor<AuditLogAPI, BaseSearchServiceRequest> getSearchRequestProcessor(){
        return new RequestProcessor<AuditLogAPI, BaseSearchServiceRequest>(){
            @Override
            public Response doProcess(AuditLogAPI api, BaseSearchServiceRequest request) throws BasicDataServiceException {
                BaseSearchServiceRequest<AuditLogSearchBean> req = (BaseSearchServiceRequest<AuditLogSearchBean>)request;
                Response response;
                switch (api){
                    case FindBeans:
                        response =  new IdmAuditLogListResponse();
                        ((IdmAuditLogListResponse)response).setList(auditLogService.findBeans(req.getSearchBean(), req.getFrom(), req.getSize()));
                        break;
                    case GetIds:
                        response =  new StringListResponse();
                        ((StringListResponse)response).setList(auditLogService.findIDs(req.getSearchBean(), req.getFrom(), req.getSize()));
                        break;
                    case Count:
                        response =  new IntResponse();
                        ((IntResponse)response).setValue(auditLogService.count(req.getSearchBean()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }
    protected RequestProcessor<AuditLogAPI, IdServiceRequest> getGetRequestProcessor(){
        return new RequestProcessor<AuditLogAPI, IdServiceRequest>(){
            @Override
            public Response doProcess(AuditLogAPI api, IdServiceRequest request) throws BasicDataServiceException {
                IdmAuditLogResponse response =  new IdmAuditLogResponse();
                response.setValue(auditLogService.findById(request.getId()));
                return response;
            }
        };
    }
}
