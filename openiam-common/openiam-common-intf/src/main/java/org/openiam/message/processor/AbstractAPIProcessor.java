package org.openiam.message.processor;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.concurrent.AbstractBaseDaemonBackgroundTask;
import org.openiam.concurrent.IBaseRunnableBackgroundTask;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.message.gateway.ServiceGateway;
import org.openiam.message.constants.OpenIAMAPI;
import org.openiam.message.dto.OpenIAMMQRequest;
import org.openiam.message.dto.OpenIAMMQResponse;
import org.springframework.util.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by alexander on 07/07/16.
 */
public abstract class AbstractAPIProcessor<RequestBody, ResponseBody extends Response> extends AbstractBaseDaemonBackgroundTask implements IBaseRunnableBackgroundTask,APIProcessor<RequestBody, ResponseBody> {
    protected ServiceGateway serviceGateway;
    protected ResponseBody apiResponse;
    private OpenIAMAPI apiName;
    private boolean isRunning = false;

    private BlockingQueue<OpenIAMMQRequest<RequestBody>> requestQueue = new LinkedBlockingQueue<OpenIAMMQRequest<RequestBody>>();

    public AbstractAPIProcessor() {
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }

    @Override
    public void pushToQueue(OpenIAMMQRequest<RequestBody> apiRequest) {
        log.debug("adding API Request {} to queue - starting", apiRequest);
        requestQueue.add(apiRequest);
        log.debug("adding API Request {} to queue - finished", apiRequest);
    }
    @Override
    public OpenIAMMQRequest<RequestBody> pullFromQueue() throws InterruptedException {
        return requestQueue.take();
    }

    @Override
    public void run() {
        try {
            isRunning = true;
            OpenIAMMQRequest<RequestBody> apiRequest = null;
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

    public void processRequest(OpenIAMMQRequest<RequestBody> apiRequest){
        try {
            ResponseBody apiResponse = this.getResponseInstance();

            String correlationId = apiRequest.getCorrelationID();

            OpenIAMMQResponse<ResponseBody> response = new OpenIAMMQResponse<ResponseBody>();
            long startTime = System.currentTimeMillis();
            log.debug("Processing {} API ...", apiRequest.getRequestApi().name());
            try {
                processingApiRequest(apiRequest.getRequestBody(),apiRequest.getLanguageId(), apiResponse);
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

    protected void sendResponse(String getReplyTo, OpenIAMMQResponse<ResponseBody> response,  String correlationId) {
        if (StringUtils.hasText(getReplyTo))
            response.setCorrelationID(correlationId);
        serviceGateway.send(getReplyTo, response);
    }

    private ResponseBody getResponseInstance() throws IllegalAccessException, InstantiationException {
        Type type = getClass().getGenericSuperclass();
        ParameterizedType paramType = (ParameterizedType) type;
        Class<ResponseBody> result = null;
        if (paramType.getActualTypeArguments()[paramType
                .getActualTypeArguments().length - 1] instanceof Class) {
            result = (Class<ResponseBody>) paramType.getActualTypeArguments()[paramType
                    .getActualTypeArguments().length - 1];

        } else if (paramType.getActualTypeArguments()[paramType
                .getActualTypeArguments().length - 1] instanceof ParameterizedType) {
            result = (Class<ResponseBody>) ((ParameterizedType) paramType
                    .getActualTypeArguments()[paramType
                    .getActualTypeArguments().length - 1]).getRawType();
        }
        return result.newInstance();
    }
    protected abstract void processingApiRequest(RequestBody requestBody, String languageId, ResponseBody responseBody) throws BasicDataServiceException;
    protected void rollbackTaransaction() {
        log.debug("There is no data which should be rollbacked");
    }
}
