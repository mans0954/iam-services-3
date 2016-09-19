package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.searchbean.AuthAttributeSearchBean;
import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.AuthAttributeListResponse;
import org.openiam.base.response.AuthProviderTypeResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AuthProviderAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 19/09/16.
 */
@Component
public class GetAuthProviderTypeDispatcher extends AbstractAPIDispatcher<IdServiceRequest, AuthProviderTypeResponse, AuthProviderAPI> {
    @Autowired
    private AuthProviderService authProviderService;

    public GetAuthProviderTypeDispatcher() {
        super(AuthProviderTypeResponse.class);
    }

    @Override
    protected AuthProviderTypeResponse processingApiRequest(AuthProviderAPI openIAMAPI, IdServiceRequest request) throws BasicDataServiceException {
        AuthProviderTypeResponse response = new AuthProviderTypeResponse();
        response.setAuthProviderType(authProviderService.getAuthProviderType(request.getId()));
        return response;
    }
}
