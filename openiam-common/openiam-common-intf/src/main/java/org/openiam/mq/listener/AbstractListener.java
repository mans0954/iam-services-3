package org.openiam.mq.listener;

import com.rabbitmq.client.Channel;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.request.*;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.concurrent.AuditLogHolder;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.exception.FieldMappingDataServiceException;
import org.openiam.exception.PageTemplateException;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.mq.constants.*;
import org.openiam.mq.constants.api.OpenIAMAPI;
import org.openiam.mq.constants.queue.MqQueue;
import org.openiam.util.AuditLogHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;

/**
 * Created by alexander on 27/07/16.
 */
public abstract class AbstractListener<API extends OpenIAMAPI> {
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    private MqQueue queueToListen;
    @Autowired
    private AuditLogHelper auditLogHelper;

    public AbstractListener(MqQueue queueToListen) {
        this.queueToListen=queueToListen;
    }

    public void onMessage(Message message, Channel channel) throws Exception {
    }

    public MqQueue getQueueToListen(){
        return this.queueToListen;
    }


    public Response processRequest(API api, BaseServiceRequest request, RequestProcessor processor){
        // init AuditLog event for this call
        AuditLogHolder.getInstance().setEvent(new IdmAuditLogEntity());
        IdmAuditLogEntity auditEvent = AuditLogHolder.getInstance().getEvent();

        Response apiResponse = new Response();

        long startTime = System.currentTimeMillis();
        log.debug("processing {} API Request {} - starting", api.name(), request);
        try {

            if(processor==null){
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown processor instance for " + api.name());
            }

            apiResponse = processor.doProcess(api, request);

            apiResponse.succeed();
            auditEvent.succeed();
        } catch (PageTemplateException e) {
            processor.handleTemplateException(e, apiResponse);
        } catch (BasicDataServiceException ex) {
            log.error(ex.getCode().name(), ex);
            apiResponse.setErrorCode(ex.getCode());
            apiResponse.setErrorText(ex.getResponseValue());
            apiResponse.setErrorTokenList(ex.getErrorTokenList());
            if(ex instanceof FieldMappingDataServiceException){
                apiResponse.setFieldMappings(((FieldMappingDataServiceException)ex).getFieldMappings());
            }
            apiResponse.fail();

            auditEvent.fail();
            auditEvent.setFailureReason(ex.getResponseValue());
            auditEvent.setException(ex);
            auditEvent.setFailureReason(ex.getCode());

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            apiResponse.setErrorCode(ResponseCode.INTERNAL_ERROR);
            apiResponse.setErrorText(ex.getMessage());
            apiResponse.fail();

            auditEvent.fail();
            auditEvent.setFailureReason(ex.getMessage());
            auditEvent.setException(ex);

        } finally {
            long totalTime = System.currentTimeMillis() - startTime;
            log.debug("Processing {} API ends. Request {}. Total time: {}", api.name(), request, totalTime / 1000.0f);
            // save audit log and remove threadlocal instance
            if(apiResponse.isFailure()){
                processor.rollbackTaransaction();
            }
            this.submitAuditLog();

            if(request.isAsych()){
                // not necessary to return response
                return null;
            }
            return apiResponse;
        }
    }

    protected void submitAuditLog(){
        IdmAuditLogEntity event = AuditLogHolder.getInstance().getEvent();
        if(event!=null && StringUtils.isNotBlank(event.getAction())){
            auditLogHelper.enqueue(event);
        }
        //remove auditLog event reference from this thread
        AuditLogHolder.remove();
    }

    public static abstract class RequestProcessor<API, RequestBody extends BaseServiceRequest>{
        protected Logger log = LoggerFactory.getLogger(this.getClass());
        public abstract Response doProcess(API api, RequestBody request) throws BasicDataServiceException;

        protected void rollbackTaransaction() {
            log.debug("There is no data which should be rollbacked");
        }
        protected void handleTemplateException(PageTemplateException e, final Response responseBody){
            IdmAuditLogEntity auditEvent = AuditLogHolder.getInstance().getEvent();
            auditEvent.fail();
            auditEvent.setFailureReason(e.getCode());
            auditEvent.setException(e);
            responseBody.setErrorCode(e.getCode());
            responseBody.setStatus(ResponseStatus.FAILURE);
        }
    }


    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) API api, EmptyServiceRequest request)  throws BasicDataServiceException{
        log.debug("Got message in Listener:  {} API. Message:  {}", api, request);
        return  this.processRequest(api, request, getEmptyRequestProcessor());
    }
    @RabbitHandler
    public Response processingSearchRequest(@Header(MQConstant.API_NAME) API api, BaseSearchServiceRequest request)  throws BasicDataServiceException{
        log.debug("Got message in Listener:  {} API. Message:  {}", api, request);
        return  this.processRequest(api, request, getSearchRequestProcessor());
    }
    @RabbitHandler
    public Response processingGetRequest(@Header(MQConstant.API_NAME) API api, IdServiceRequest request)  throws BasicDataServiceException{
        log.debug("Got message in Listener:  {} API. Message:  {}", api, request);
        return  this.processRequest(api, request, getGetRequestProcessor());
    }
    @RabbitHandler
    public Response processingCrudRequest(@Header(MQConstant.API_NAME) API api, BaseCrudServiceRequest request)  throws BasicDataServiceException{
        log.debug("Got message in Listener:  {} API. Message:  {}", api, request);
        return  this.processRequest(api, request, getCrudRequestProcessor());
    }


    protected RequestProcessor<API, EmptyServiceRequest> getEmptyRequestProcessor(){
        return null;
    }
    protected RequestProcessor<API, BaseSearchServiceRequest> getSearchRequestProcessor(){
        return null;
    }
    protected RequestProcessor<API, IdServiceRequest> getGetRequestProcessor(){
        return null;
    }
    protected RequestProcessor<API, BaseCrudServiceRequest> getCrudRequestProcessor(){
        return null;
    }
}
