package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.request.OAuthScopesRequest;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.OAuthAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 29/09/16.
 */
@Component
public class DeAuthorizeClientDispatcher extends AbstractAPIDispatcher<OAuthScopesRequest, Response, OAuthAPI> {
    @Autowired
    private AuthProviderService authProviderService;
    public DeAuthorizeClientDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(OAuthAPI openIAMAPI, OAuthScopesRequest request) throws BasicDataServiceException {
        authProviderService.deAuthorizeClient(request.getClientId(), request.getUserId());
        return new Response();
    }
}


