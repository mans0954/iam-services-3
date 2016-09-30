package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.dto.OAuthScopesResponse;
import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.request.OAuthScopesRequest;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.OAuthAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 28/09/16.
 */
@Component
public class GetClientScopesDispatcher  extends AbstractAPIDispatcher<OAuthScopesRequest, OAuthScopesResponse, OAuthAPI> {
    @Autowired
    private AuthProviderService authProviderService;

    public GetClientScopesDispatcher() {
        super(OAuthScopesResponse.class);
    }

    @Override
    protected OAuthScopesResponse processingApiRequest(OAuthAPI openIAMAPI, OAuthScopesRequest request) throws BasicDataServiceException {
        OAuthScopesResponse response = new OAuthScopesResponse();
        response.setClientId(request.getClientId());
        switch (openIAMAPI){
            case GetAuthorizedScopes:
                response.setList(authProviderService.getAuthorizedScopes(request.getClientId(), request.getUserId(), request.getLanguage()));
                break;
            case GetScopesForAuthrorization:
                response.setList(authProviderService.getScopesForAuthrorization(request.getClientId(), request.getUserId(), request.getLanguage()));
                break;
        }
        return response;
    }
}
