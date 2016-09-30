package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.AuthProviderResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AuthProviderAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 19/09/16.
 */
@Component
public class GetAuthProvidersDispatcher extends AbstractAPIDispatcher<IdServiceRequest, AuthProviderResponse, AuthProviderAPI> {
    @Autowired
    private AuthProviderService authProviderService;

    public GetAuthProvidersDispatcher() {
        super(AuthProviderResponse.class);
    }

    @Override
    protected AuthProviderResponse processingApiRequest(AuthProviderAPI openIAMAPI, IdServiceRequest request) throws BasicDataServiceException {
        AuthProviderResponse response = new AuthProviderResponse();
        response.setValue(authProviderService.getProvider(request.getId()));
        return response;
    }
}
