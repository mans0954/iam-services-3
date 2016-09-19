package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.searchbean.AuthAttributeSearchBean;
import org.openiam.am.srvc.searchbean.AuthProviderSearchBean;
import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.AuthAttributeListResponse;
import org.openiam.base.response.AuthProviderListResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AuthProviderAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 19/09/16.
 */
@Component
public class FindAuthProvidersDispatcher extends AbstractAPIDispatcher<BaseSearchServiceRequest<AuthProviderSearchBean>, AuthProviderListResponse, AuthProviderAPI> {
    @Autowired
    private AuthProviderService authProviderService;

    public FindAuthProvidersDispatcher() {
        super(AuthProviderListResponse.class);
    }

    @Override
    protected AuthProviderListResponse processingApiRequest(AuthProviderAPI openIAMAPI, BaseSearchServiceRequest<AuthProviderSearchBean> request) throws BasicDataServiceException {
        AuthProviderListResponse response = new AuthProviderListResponse();
        response.setAuthProviderList(authProviderService.findAuthProviderBeans(request.getSearchBean(), request.getFrom(), request.getSize()));
        return response;
    }
}
