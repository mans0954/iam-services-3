package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.dto.AuthResourceAttributeMap;
import org.openiam.am.srvc.service.AuthResourceAttributeService;
import org.openiam.base.request.BaseGrudServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.AuthResourceAttributeMapResponse;
import org.openiam.base.response.StringResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.AuthResourceAttributeAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 20/09/16.
 */
@Component
public class SaveAttributeMapDispatcher extends AbstractAPIDispatcher<BaseGrudServiceRequest<AuthResourceAttributeMap>, StringResponse, AuthResourceAttributeAPI> {
    @Autowired
    private AuthResourceAttributeService authResourceAttributeService;

    public SaveAttributeMapDispatcher() {
        super(StringResponse.class);
    }

    @Override
    protected StringResponse processingApiRequest(AuthResourceAttributeAPI openIAMAPI, BaseGrudServiceRequest<AuthResourceAttributeMap> request) throws BasicDataServiceException {
        StringResponse response = new StringResponse();
        response.setValue(authResourceAttributeService.saveAttributeMap(request.getObject()));
        return response;
    }
}
