package org.openiam.am.srvc.mq;

import org.openiam.am.srvc.service.AccessReviewService;
import org.openiam.base.request.*;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.model.AccessViewResponse;
import org.openiam.mq.constants.api.AccessReviewAPI;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.queue.am.AccessReviewQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 10/08/16.
 */
@Component
@RabbitListener(id="accessReviewListener",
        queues = "#{AccessReviewQueue.name}",
        containerFactory = "amRabbitListenerContainerFactory")
public class AccessReviewListener extends AbstractListener<AccessReviewAPI> {

    @Autowired
    private AccessReviewService accessReviewService;

    @Autowired
    public AccessReviewListener(AccessReviewQueue queue) {
        super(queue);
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) AccessReviewAPI api, AccessReviewRequest request)  throws BasicDataServiceException {
        return  this.processRequest(api, request, new AbstractListener.RequestProcessor<AccessReviewAPI, AccessReviewRequest>(){
            @Override
            public Response doProcess(AccessReviewAPI api, AccessReviewRequest request) throws BasicDataServiceException {
                AccessViewResponse response;
                switch (api){
                    case AccessReviewTree:
                        response = accessReviewService.getAccessReviewTree(request.getFilterBean(), request.getViewType(), request.getDate(), request.getLanguage());
                        break;
                    case AccessReviewSubTree:
                        response = accessReviewService.getAccessReviewSubTree(request.getParentId(), request.getParentBeanType(), request.isRootOnly(),
                                request.getFilterBean(), request.getViewType(), request.getDate(), request.getLanguage());
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        });
    }
}
