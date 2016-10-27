package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.dto.OAuthCode;
import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.request.BaseCrudServiceRequest;
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
public class SaveOAuthCodeDispatcher extends AbstractAPIDispatcher<BaseCrudServiceRequest<OAuthCode>, StringResponse, OAuthAPI> {
    @Autowired
    private AuthProviderService authProviderService;

    public SaveOAuthCodeDispatcher() {
        super(StringResponse.class);
    }

    @Override
    protected StringResponse processingApiRequest(OAuthAPI openIAMAPI, BaseCrudServiceRequest<OAuthCode> request) throws BasicDataServiceException {
        StringResponse response = new StringResponse();
        authProviderService.saveOAuthCode(request.getObject());
        return response;
    }
}
