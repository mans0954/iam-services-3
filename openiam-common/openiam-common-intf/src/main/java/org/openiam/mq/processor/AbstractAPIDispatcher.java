package org.openiam.mq.processor;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.concurrent.AbstractBaseRunnableBackgroundTask;
import org.openiam.concurrent.IBaseRunnableBackgroundTask;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.dto.MQResponse;
import org.openiam.mq.gateway.ResponseServiceGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by alexander on 07/07/16.
 */
public abstract class AbstractAPIDispatcher<RequestBody extends BaseServiceRequest, ResponseBody extends Response> extends AbstractBaseRunnableBackgroundTask implements IBaseRunnableBackgroundTask,APIProcessor<RequestBody, ResponseBody> {

    @Autowired
    @Qualifier("rabbitResponseServiceGateway")
    private ResponseServiceGateway responseServiceGateway;
    private OpenIAMAPI apiName;
    private boolean isRunning = false;

    private Class<ResponseBody> responseBodyClass;

    private BlockingQueue<MQRequest<RequestBody>> requestQueue = new LinkedBlockingQueue<MQRequest<RequestBody>>();

    public AbstractAPIDispatcher(Class<ResponseBody> responseBodyClass) {
        this.responseBodyClass = responseBodyClass;
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }

    @Override
    public void pushToQueue(MQRequest<RequestBody> apiRequest) {
        log.debug("adding API Request {} to queue - starting", apiRequest);
        requestQueue.add(apiRequest);
        log.debug("adding API Request {} to queue - finished", apiRequest);
    }
    @Override
    public MQRequest<RequestBody> pullFromQueue() throws InterruptedException {
        return requestQueue.take();
    }

    @Override
    public void run() {
        try {
            isRunning = true;
            MQRequest<RequestBody> apiRequest = null;
            while ((apiRequest = pullFromQueue()) != null) {
                log.debug("processing API Request {} - starting", apiRequest);
                processRequest(apiRequest);
                log.debug("processing API Request {} - finished", apiRequest);
            }
        } catch (Exception ex) {
            log.debug("API Processor for {} API stoped due to error", apiName);
            log.error(ex.getMessage(), ex);
        } finally {
            isRunning = false;
        }
    }

    public void processRequest(MQRequest<RequestBody> apiRequest){
        try {
            ResponseBody apiResponse = this.getResponseInstance();

            byte[] correlationId = apiRequest.getCorrelationId();

            MQResponse<ResponseBody> response = new MQResponse<ResponseBody>();
            long startTime = System.currentTimeMillis();
            log.debug("Processing {} API ...", apiRequest.getRequestApi().name());
            try {
                apiResponse = processingApiRequest(apiRequest.getRequestApi(),apiRequest.getRequestBody());
            } catch (BasicDataServiceException ex) {
                log.error(ex.getCode().name(), ex);
                apiResponse.setErrorCode(ex.getCode());
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                apiResponse.setErrorCode(ResponseCode.INTERNAL_ERROR);
                rollbackTaransaction();
            } finally {
                response.setResponseBody(apiResponse);
                long totalTime = System.currentTimeMillis() - startTime;
                log.debug("Processing {} API ends. Total time: {}", apiRequest
                        .getRequestApi().name(), totalTime / 1000.0f);
                this.sendResponse(apiRequest.getReplyTo(), response, correlationId);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    protected void sendResponse(String getReplyTo, MQResponse<ResponseBody> response, byte[] correlationId) {
        if (StringUtils.hasText(getReplyTo)) {
            response.setCorrelationId(correlationId);
            responseServiceGateway.send(getReplyTo, response, correlationId);
        }
    }

    private ResponseBody getResponseInstance() throws IllegalAccessException, InstantiationException {
        return responseBodyClass.newInstance();
    }

    protected abstract ResponseBody processingApiRequest(final OpenIAMAPI openIAMAPI, final RequestBody requestBody) throws BasicDataServiceException;
    protected void rollbackTaransaction() {
        log.debug("There is no data which should be rollbacked");
    }
}
