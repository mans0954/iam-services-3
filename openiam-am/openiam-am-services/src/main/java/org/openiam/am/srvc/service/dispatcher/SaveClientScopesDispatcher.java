package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.request.BaseCrudServiceRequest;
import org.openiam.base.request.model.OAuthClientScopeModel;
import org.openiam.base.response.StringResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.OAuthAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 28/09/16.
 */
@Component
public class SaveClientScopesDispatcher extends AbstractAPIDispatcher<BaseCrudServiceRequest<OAuthClientScopeModel>, StringResponse, OAuthAPI> {
    @Autowired
    private AuthProviderService authProviderService;

    public SaveClientScopesDispatcher() {
        super(StringResponse.class);
    }

    @Override
    protected StringResponse processingApiRequest(OAuthAPI openIAMAPI, BaseCrudServiceRequest<OAuthClientScopeModel> request) throws BasicDataServiceException {
        StringResponse response = new StringResponse();
        authProviderService.saveClientScopeAuthorization(request.getObject().getId(), request.getObject().getUserId(), request.getObject().getOauthUserClientXrefList());
        return response;
    }
}
