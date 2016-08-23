package org.openiam.idm.srvc.auth.service.dispatcher;

import org.openiam.base.request.OTPServiceRequest;
import org.openiam.base.response.BooleanResponse;
import org.openiam.base.response.StringResponse;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.auth.service.AuthenticationServiceService;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 11/08/16.
 */
@Component
public class GetOTPTokenDispatcher extends AbstractAPIDispatcher<OTPServiceRequest, StringResponse> {
    @Autowired
    private AuthenticationServiceService authenticationServiceService;

    public GetOTPTokenDispatcher() {
        super(StringResponse.class);
    }

    @Override
    protected StringResponse processingApiRequest(OpenIAMAPI openIAMAPI, OTPServiceRequest otpServiceRequest) throws BasicDataServiceException {
        StringResponse resp = new StringResponse();
        String secret =  authenticationServiceService.getOTPSecretKey(otpServiceRequest);
        resp.setValue(secret);
        return resp;
    }
}
