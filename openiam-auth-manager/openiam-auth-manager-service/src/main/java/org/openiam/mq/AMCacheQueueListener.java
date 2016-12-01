package org.openiam.mq;

import org.openiam.authmanager.service.AuthorizationManagerMenuService;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.request.EmptyServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.api.AMCacheAPI;
import org.openiam.mq.constants.queue.am.AMCacheQueue;
import org.openiam.mq.listener.AbstractListener;
import org.openiam.thread.Sweepable;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 10/08/16.
 */
@Component
@RabbitListener(id="amCacheQueueListener",
        queues = "#{AMCacheQueue.name}",
        containerFactory = "amRabbitListenerContainerFactory")
public class AMCacheQueueListener extends AbstractListener<AMCacheAPI> {
    @Autowired
    private AuthorizationManagerMenuService menuService;
    @Autowired
    private AuthorizationManagerService authorizationManagerService;
    @Autowired
    public AMCacheQueueListener(AMCacheQueue queue) {
        super(queue);
    }

    protected RequestProcessor<AMCacheAPI, EmptyServiceRequest> getEmptyRequestProcessor(){
        return new RequestProcessor<AMCacheAPI, EmptyServiceRequest>(){
            @Override
            public Response doProcess(AMCacheAPI api, EmptyServiceRequest request) throws BasicDataServiceException {
                Response response = new Response();
                switch (api){
                    case RefreshAMManager:
                        ((Sweepable)authorizationManagerService).sweep();
                        break;
                    case RefreshAMMenu:
                        menuService.sweep();
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }
}
