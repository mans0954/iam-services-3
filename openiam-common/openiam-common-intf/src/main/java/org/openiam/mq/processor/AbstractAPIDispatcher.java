package org.openiam.mq.processor;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.request.IdmAuditLogRequest;
import org.openiam.base.response.MenuSaveResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.concurrent.AbstractBaseRunnableBackgroundTask;
import org.openiam.concurrent.AuditLogHolder;
import org.openiam.concurrent.IBaseRunnableBackgroundTask;
import org.openiam.exception.AuthorizationMenuException;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.exception.PageTemplateException;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMAPICommon;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.dto.MQResponse;
import org.openiam.mq.gateway.RequestServiceGateway;
import org.openiam.mq.gateway.ResponseServiceGateway;
import org.openiam.util.AuditLogHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by alexander on 07/07/16.
 */
public abstract class AbstractAPIDispatcher<RequestBody extends BaseServiceRequest, ResponseBody extends Response, API extends OpenIAMAPI> extends AbstractBaseRunnableBackgroundTask implements IBaseRunnableBackgroundTask,APIProcessor<RequestBody, ResponseBody, API> {

    @Autowired
    @Qualifier("rabbitResponseServiceGateway")
    private ResponseServiceGateway responseServiceGateway;
    @Autowired
    private RequestServiceGateway requestServiceGateway;
    @Autowired
    private AuditLogHelper auditLogHelper;

    private OpenIAMAPI apiName;
    private boolean isRunning = false;

    private Class<ResponseBody> responseBodyClass;

    private BlockingQueue<MQRequest<RequestBody, API>> requestQueue = new LinkedBlockingQueue<MQRequest<RequestBody, API>>();

    public AbstractAPIDispatcher(Class<ResponseBody> responseBodyClass) {
        this.responseBodyClass = responseBodyClass;
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }

    @Override
    public void pushToQueue(MQRequest<RequestBody, API> apiRequest) {
        log.debug("adding API Request {} to queue - starting", apiRequest);
        requestQueue.add(apiRequest);
        log.debug("adding API Request {} to queue - finished", apiRequest);
    }
    @Override
    public MQRequest<RequestBody, API> pullFromQueue() throws InterruptedException {
        return requestQueue.take();
    }

    @Override
    public void run() {
        try {
            isRunning = true;
            MQRequest<RequestBody, API> apiRequest = null;
            while ((apiRequest = pullFromQueue()) != null) {

                processRequest(apiRequest);

            }
        } catch (Exception ex) {
            log.debug("API Processor for {} API stoped due to error", apiName);
            log.error(ex.getMessage(), ex);
        } finally {
            isRunning = false;
        }
    }

    public void processRequest(MQRequest<RequestBody, API> apiRequest){
        try {
            // init AuditLog event for this call
            AuditLogHolder.getInstance().setEvent(new IdmAuditLogEntity());
            IdmAuditLogEntity auditEvent = AuditLogHolder.getInstance().getEvent();

            ResponseBody apiResponse = this.getResponseInstance();

            byte[] correlationId = apiRequest.getCorrelationId();

            MQResponse<ResponseBody> response = new MQResponse<ResponseBody>();
            long startTime = System.currentTimeMillis();
            log.debug("processing {} API Request {} - starting", apiRequest.getRequestApi().name(), apiRequest);
            try {
                apiResponse = processingApiRequest(apiRequest.getRequestApi(), apiRequest.getRequestBody());
                apiResponse.succeed();
                auditEvent.succeed();
            } catch (PageTemplateException e) {
                handleTemplateException(e, apiResponse);
		    } catch (BasicDataServiceException ex) {
                log.error(ex.getCode().name(), ex);
                apiResponse.setErrorCode(ex.getCode());
                apiResponse.setErrorText(ex.getResponseValue());
                apiResponse.setErrorTokenList(ex.getErrorTokenList());
                apiResponse.fail();

                auditEvent.fail();
                auditEvent.setFailureReason(ex.getResponseValue());
                auditEvent.setException(ex);
                auditEvent.setFailureReason(ex.getCode());

                rollbackTaransaction();
            } catch (Exception ex ) {
                log.error(ex.getMessage(), ex);
                apiResponse.setErrorCode(ResponseCode.INTERNAL_ERROR);
                apiResponse.setErrorText(ex.getMessage());
                apiResponse.fail();

                auditEvent.fail();
                auditEvent.setFailureReason(ex.getMessage());
                auditEvent.setException(ex);

                rollbackTaransaction();
            } finally {
                response.setResponseBody(apiResponse);
                long totalTime = System.currentTimeMillis() - startTime;
                log.debug("processing {} API Request {} - finished", apiRequest.getRequestApi().name(), apiRequest);
                log.debug("Processing {} API ends. Total time: {}", apiRequest.getRequestApi().name(), totalTime / 1000.0f);
                this.sendResponse(apiRequest.getReplyTo(), response, correlationId);
                // save audit log and remove threadlocal instance
                this.submitAuditLog();
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
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

    protected void sendResponse(String getReplyTo, MQResponse<ResponseBody> response, byte[] correlationId) {
        if (StringUtils.isNotBlank(getReplyTo)) {
            response.setCorrelationId(correlationId);
            responseServiceGateway.send(getReplyTo, response, correlationId);
        }
    }

    private ResponseBody getResponseInstance() throws IllegalAccessException, InstantiationException {
        return responseBodyClass.newInstance();
    }

    protected abstract ResponseBody processingApiRequest(final API openIAMAPI, final RequestBody requestBody) throws BasicDataServiceException;
    protected void rollbackTaransaction() {
        log.debug("There is no data which should be rollbacked");
    }

    protected void handleTemplateException(PageTemplateException e, final ResponseBody responseBody){
        IdmAuditLogEntity auditEvent = AuditLogHolder.getInstance().getEvent();
        auditEvent.fail();
        auditEvent.setFailureReason(e.getCode());
        auditEvent.setException(e);
        responseBody.setErrorCode(e.getCode());
        responseBody.setStatus(ResponseStatus.FAILURE);
    }
}
