package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.request.BaseGrudServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.AuthProviderResponse;
import org.openiam.base.response.StringResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AuthProviderAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 19/09/16.
 */
@Component
public class SaveAuthProvidersDispatcher extends AbstractAPIDispatcher<BaseGrudServiceRequest<AuthProvider>, StringResponse, AuthProviderAPI> {
    @Autowired
    private AuthProviderService authProviderService;

    public SaveAuthProvidersDispatcher() {
        super(StringResponse.class);
    }

    @Override
    protected StringResponse processingApiRequest(AuthProviderAPI openIAMAPI, BaseGrudServiceRequest<AuthProvider> request) throws BasicDataServiceException {
        StringResponse response = new StringResponse();
        response.setValue(authProviderService.saveAuthProvider(request.getObject(), request.getRequesterId()));
        return response;
    }
}
