package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.dto.OAuthScopesResponse;
import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.AuthProviderResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.OAuthAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 28/09/16.
 */
@Component
public class GetOAuthClientDispatcher extends AbstractAPIDispatcher<IdServiceRequest, AuthProviderResponse, OAuthAPI> {
    @Autowired
    private AuthProviderService authProviderService;

    public GetOAuthClientDispatcher() {
        super(AuthProviderResponse.class);
    }

    @Override
    protected AuthProviderResponse processingApiRequest(OAuthAPI openIAMAPI, IdServiceRequest request) throws BasicDataServiceException {
        AuthProviderResponse response = new AuthProviderResponse();
        switch (openIAMAPI){
            case GetClient:
                response.setValue(authProviderService.getOAuthClient(request.getId()));
                break;
            case GetCachedOAuthProviderById:
                response.setValue(authProviderService.getCachedOAuthProviderById(request.getId()));
                break;
            case GetCachedOAuthProviderByName:
                response.setValue(authProviderService.getCachedOAuthProviderByName(request.getId()));
                break;
        }
        return response;
    }
}
