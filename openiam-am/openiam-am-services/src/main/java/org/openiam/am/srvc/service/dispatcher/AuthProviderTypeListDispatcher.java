package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.AuthProviderTypeListResponse;
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
public class AuthProviderTypeListDispatcher extends AbstractAPIDispatcher<BaseServiceRequest, AuthProviderTypeListResponse, AuthProviderAPI> {
    @Autowired
    private AuthProviderService authProviderService;

    public AuthProviderTypeListDispatcher() {
        super(AuthProviderTypeListResponse.class);
    }

    @Override
    protected AuthProviderTypeListResponse processingApiRequest(AuthProviderAPI openIAMAPI, BaseServiceRequest request) throws BasicDataServiceException {
        AuthProviderTypeListResponse response = new AuthProviderTypeListResponse();
        switch (openIAMAPI){
            case GetAuthProviderTypeList:
                response.setAuthProviderTypeList(authProviderService.getAuthProviderTypeList());
                break;
            case GetSocialAuthProviderTypeList:
                response.setAuthProviderTypeList(authProviderService.getSocialAuthProviderTypeList());
                break;
        }
        return response;
    }
}
