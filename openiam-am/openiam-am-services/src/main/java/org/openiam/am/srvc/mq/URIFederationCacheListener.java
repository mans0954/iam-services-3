package org.openiam.am.srvc.mq;

import org.openiam.am.srvc.service.URIFederationService;
import org.openiam.base.request.*;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.URIFederationAPI;
import org.openiam.mq.constants.queue.am.RefreshUriFederationCache;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 10/08/16.
 */
@Component
@RabbitListener(id="uriFederationCacheListener",
        queues = "#{RefreshUriFederationCache.name}",
        containerFactory = "amRabbitListenerContainerFactory")
public class URIFederationCacheListener extends AbstractListener<URIFederationAPI> {
    @Autowired
    private URIFederationService uriFederationService;
    @Autowired
    public URIFederationCacheListener(RefreshUriFederationCache queue) {
        super(queue);
    }

    @Override
    protected RequestProcessor<URIFederationAPI, EmptyServiceRequest> getEmptyRequestProcessor() {
        return new RequestProcessor<URIFederationAPI, EmptyServiceRequest>(){
            @Override
            public Response doProcess(URIFederationAPI api, EmptyServiceRequest request) throws BasicDataServiceException {
                uriFederationService.sweep();
                return new Response();
            }
        };
    }
}
