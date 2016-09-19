package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.searchbean.AuthProviderSearchBean;
import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.AuthProviderListResponse;
import org.openiam.base.response.IntResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AuthProviderAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 19/09/16.
 */
@Component
public class CountAuthProvidersDispatcher extends AbstractAPIDispatcher<BaseSearchServiceRequest<AuthProviderSearchBean>, IntResponse, AuthProviderAPI> {
    @Autowired
    private AuthProviderService authProviderService;

    public CountAuthProvidersDispatcher() {
        super(IntResponse.class);
    }

    @Override
    protected IntResponse processingApiRequest(AuthProviderAPI openIAMAPI, BaseSearchServiceRequest<AuthProviderSearchBean> request) throws BasicDataServiceException {
        IntResponse response = new IntResponse();
        response.setValue(authProviderService.countAuthProviderBeans(request.getSearchBean()));
        return response;
    }
}
