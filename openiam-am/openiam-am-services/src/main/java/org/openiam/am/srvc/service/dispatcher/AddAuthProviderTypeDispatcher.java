package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.dto.AuthProviderType;
import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.request.BaseCrudServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AuthProviderAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 19/09/16.
 */
@Component
public class AddAuthProviderTypeDispatcher extends AbstractAPIDispatcher<BaseCrudServiceRequest<AuthProviderType>, Response, AuthProviderAPI> {
    @Autowired
    private AuthProviderService authProviderService;

    public AddAuthProviderTypeDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(AuthProviderAPI openIAMAPI, BaseCrudServiceRequest<AuthProviderType> request) throws BasicDataServiceException {
        Response response = new Response(ResponseStatus.SUCCESS);
        authProviderService.addProviderType(request.getObject());
        return response;
    }
}
