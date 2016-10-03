package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.URIFederationService;
import org.openiam.base.request.URIFederationServiceRequest;
import org.openiam.base.request.model.CertificateLoginServiceRequest;
import org.openiam.base.response.LoginResponse;
import org.openiam.base.response.URIFederationResponse;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.URIFederationAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 10/08/16.
 */
@Component
public class CertificateLoginDispatcher extends AbstractAPIDispatcher<CertificateLoginServiceRequest, LoginResponse, URIFederationAPI> {
    @Autowired
    private URIFederationService uriFederationService;

    public CertificateLoginDispatcher() {
        super(LoginResponse.class);
    }

    @Override
    protected LoginResponse processingApiRequest(URIFederationAPI openIAMAPI, CertificateLoginServiceRequest request) throws BasicDataServiceException {
        return uriFederationService.getIdentityFromCert(request.getProxyURI(), request.getMethod(), request.getCertContents());

    }
}
