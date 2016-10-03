package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.dto.OAuthToken;
import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.request.BaseGrudServiceRequest;
import org.openiam.base.response.OAuthTokenResponse;
import org.openiam.base.response.StringResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.OAuthAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 29/09/16.
 */
@Component
public class SaveOAuthTokenDispatcher extends AbstractAPIDispatcher<BaseGrudServiceRequest<OAuthToken>, OAuthTokenResponse, OAuthAPI> {
    @Autowired
    private AuthProviderService authProviderService;

    public SaveOAuthTokenDispatcher() {
        super(OAuthTokenResponse.class);
    }

    @Override
    protected OAuthTokenResponse processingApiRequest(OAuthAPI openIAMAPI, BaseGrudServiceRequest<OAuthToken> request) throws BasicDataServiceException {
        OAuthTokenResponse response = new OAuthTokenResponse();
        response.setValue(authProviderService.saveOAuthToken(request.getObject()));
        return response;
    }
}
