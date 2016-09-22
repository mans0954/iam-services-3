package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.AuthResourceAttributeService;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.request.SSOAttributesRequest;
import org.openiam.base.response.AuthResourceAMAttributeListResponse;
import org.openiam.base.response.SSOAttributeListResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AuthResourceAttributeAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 20/09/16.
 */
@Component
public class GetSSOAttributeListDispatcher extends AbstractAPIDispatcher<SSOAttributesRequest, SSOAttributeListResponse, AuthResourceAttributeAPI> {
    @Autowired
    private AuthResourceAttributeService authResourceAttributeService;

    public GetSSOAttributeListDispatcher() {
        super(SSOAttributeListResponse.class);
    }

    @Override
    protected SSOAttributeListResponse processingApiRequest(AuthResourceAttributeAPI openIAMAPI, SSOAttributesRequest request) throws BasicDataServiceException {
        SSOAttributeListResponse response = new SSOAttributeListResponse();
        response.setSsoAttributeList(authResourceAttributeService.getSSOAttributes(request.getProviderId(), request.getUserId()));
        return response;
    }
}
