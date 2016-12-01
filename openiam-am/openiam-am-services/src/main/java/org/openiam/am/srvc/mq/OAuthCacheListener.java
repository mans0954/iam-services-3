package org.openiam.am.srvc.mq;

import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.request.EmptyServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.api.OAuthAPI;
import org.openiam.mq.constants.queue.am.RefreshOAuthCache;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 19/09/16.
 */
@Component
@RabbitListener(id="oauthCacheListener",
        queues = "#{RefreshOAuthCache.name}",
        containerFactory = "amRabbitListenerContainerFactory")
public class OAuthCacheListener extends AbstractListener<OAuthAPI> {

    @Autowired
    private AuthProviderService authProviderService;

    @Autowired
    public OAuthCacheListener(RefreshOAuthCache queue) {
        super(queue);
    }


    @Override
    protected RequestProcessor<OAuthAPI, EmptyServiceRequest> getEmptyRequestProcessor() {
        return new RequestProcessor<OAuthAPI, EmptyServiceRequest>(){
            @Override
            public Response doProcess(OAuthAPI api, EmptyServiceRequest request) throws BasicDataServiceException {
                authProviderService.sweepOAuthProvider();
                return new Response();
            }
        };
    }
}
