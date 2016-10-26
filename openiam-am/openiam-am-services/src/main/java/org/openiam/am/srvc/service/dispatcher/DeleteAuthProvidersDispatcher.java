package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AuthProviderAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 19/09/16.
 */
@Component
public class DeleteAuthProvidersDispatcher extends AbstractAPIDispatcher<IdServiceRequest, Response, AuthProviderAPI> {
    @Autowired
    private AuthProviderService authProviderService;

    public DeleteAuthProvidersDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(AuthProviderAPI openIAMAPI, IdServiceRequest request) throws BasicDataServiceException {
        Response response = new Response();
        authProviderService.deleteAuthProvider(request.getId());
        return response;
    }
}
