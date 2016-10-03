package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.OAuthTokenResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.OAuthAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 29/09/16.
 */
@Component
public class GetOAuthTokenDispatcher extends AbstractAPIDispatcher<IdServiceRequest, OAuthTokenResponse, OAuthAPI> {
    @Autowired
    private AuthProviderService authProviderService;

    public GetOAuthTokenDispatcher() {
        super(OAuthTokenResponse.class);
    }

    @Override
    protected OAuthTokenResponse processingApiRequest(OAuthAPI openIAMAPI, IdServiceRequest request) throws BasicDataServiceException {
        OAuthTokenResponse response = new OAuthTokenResponse();
        switch (openIAMAPI){
            case GetOAuthToken:
                response.setValue(authProviderService.getOAuthToken(request.getId()));
                break;
            case GetOAuthTokenByRefreshToken:
                response.setValue(authProviderService.getOAuthTokenByRefreshToken(request.getId()));
                break;
        }
        return response;
    }
}
