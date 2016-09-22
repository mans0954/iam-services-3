package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.AuthResourceAttributeService;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.response.AuthResourceAMAttributeListResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AuthResourceAttributeAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 20/09/16.
 */
@Component
public class GetAmAttributeListDispatcher extends AbstractAPIDispatcher<BaseServiceRequest, AuthResourceAMAttributeListResponse, AuthResourceAttributeAPI> {
    @Autowired
    private AuthResourceAttributeService authResourceAttributeService;

    public GetAmAttributeListDispatcher() {
        super(AuthResourceAMAttributeListResponse.class);
    }

    @Override
    protected AuthResourceAMAttributeListResponse processingApiRequest(AuthResourceAttributeAPI openIAMAPI, BaseServiceRequest request) throws BasicDataServiceException {
        AuthResourceAMAttributeListResponse response = new AuthResourceAMAttributeListResponse();
        response.setAmAttributeList(authResourceAttributeService.getAmAttributeList());
        return response;
    }
}
