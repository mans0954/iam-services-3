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
public class OTPDispatcher  extends AbstractAPIDispatcher<OTPServiceRequest, Response> {
    @Autowired
    private AuthenticationServiceService authenticationServiceService;

    public OTPDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(OpenIAMAPI openIAMAPI, OTPServiceRequest otpServiceRequest) throws BasicDataServiceException {
        switch (openIAMAPI){
            case ClearOTPActiveStatus:
                return authenticationServiceService.clearOTPActiveStatus(otpServiceRequest);
            case SendOTPToken:
                return authenticationServiceService.sendOTPToken(otpServiceRequest);
            case ConfirmOTPToken:
                return authenticationServiceService.confirmOTPToken(otpServiceRequest);
            default:
                break;
        }

        return null;
    }
}
