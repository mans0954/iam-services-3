package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.searchbean.AuthAttributeSearchBean;
import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.AuthAttributeListResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AuthProviderAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 19/09/16.
 */
@Component
public class FindAuthAttributesDispatcher   extends AbstractAPIDispatcher<BaseSearchServiceRequest<AuthAttributeSearchBean>, AuthAttributeListResponse, AuthProviderAPI> {
    @Autowired
    private AuthProviderService authProviderService;

    public FindAuthAttributesDispatcher() {
        super(AuthAttributeListResponse.class);
    }

    @Override
    protected AuthAttributeListResponse processingApiRequest(AuthProviderAPI openIAMAPI, BaseSearchServiceRequest<AuthAttributeSearchBean> request) throws BasicDataServiceException {
        AuthAttributeListResponse response = new AuthAttributeListResponse();
        response.setAttributeList(authProviderService.findAuthAttributeBeans(request.getSearchBean(), request.getSize(), request.getFrom()));
        return response;
    }
}
