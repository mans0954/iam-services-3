package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.AuthResourceAttributeService;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.request.SSOAttributesRequest;
import org.openiam.base.response.AuthResourceAttributeMapResponse;
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
public class GetAttributeDispatcher extends AbstractAPIDispatcher<IdServiceRequest, AuthResourceAttributeMapResponse, AuthResourceAttributeAPI> {
    @Autowired
    private AuthResourceAttributeService authResourceAttributeService;

    public GetAttributeDispatcher() {
        super(AuthResourceAttributeMapResponse.class);
    }

    @Override
    protected AuthResourceAttributeMapResponse processingApiRequest(AuthResourceAttributeAPI openIAMAPI, IdServiceRequest request) throws BasicDataServiceException {
        AuthResourceAttributeMapResponse response = new AuthResourceAttributeMapResponse();
        response.setAttributeMap(authResourceAttributeService.getAttribute(request.getId()));
        return response;
    }
}
