package org.openiam.idm.srvc.auth.service.dispatcher;

import org.openiam.base.request.OTPServiceRequest;
import org.openiam.base.response.BooleanResponse;
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
public class CheckOTPDispatcher extends AbstractAPIDispatcher<OTPServiceRequest, BooleanResponse> {
    @Autowired
    private AuthenticationServiceService authenticationServiceService;

    public CheckOTPDispatcher() {
        super(BooleanResponse.class);
    }

    @Override
    protected BooleanResponse processingApiRequest(OpenIAMAPI openIAMAPI, OTPServiceRequest otpServiceRequest) throws BasicDataServiceException {
        BooleanResponse response = new BooleanResponse();
        response.setValue(authenticationServiceService.isOTPActive(otpServiceRequest));
        return response;
    }
}
