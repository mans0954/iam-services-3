package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.OAuthCodeResponse;
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
public class GetOAuthCodeDispatcher extends AbstractAPIDispatcher<IdServiceRequest, OAuthCodeResponse, OAuthAPI> {
    @Autowired
    private AuthProviderService authProviderService;

    public GetOAuthCodeDispatcher() {
        super(OAuthCodeResponse.class);
    }

    @Override
    protected OAuthCodeResponse processingApiRequest(OAuthAPI openIAMAPI, IdServiceRequest request) throws BasicDataServiceException {
        OAuthCodeResponse response = new OAuthCodeResponse();
        response.setValue(authProviderService.getOAuthCode(request.getId()));
        return response;
    }
}
