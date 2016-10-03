package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.URIFederationService;
import org.openiam.am.srvc.uriauth.dto.SSOLoginResponse;
import org.openiam.base.request.URIFederationServiceRequest;
import org.openiam.base.request.model.CertificateLoginServiceRequest;
import org.openiam.base.response.LoginResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.URIFederationAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 10/08/16.
 */
@Component
public class SSOLoginDispatcher extends AbstractAPIDispatcher<URIFederationServiceRequest, SSOLoginResponse, URIFederationAPI> {
    @Autowired
    private URIFederationService uriFederationService;

    public SSOLoginDispatcher() {
        super(SSOLoginResponse.class);
    }

    @Override
    protected SSOLoginResponse processingApiRequest(URIFederationAPI openIAMAPI, URIFederationServiceRequest request) throws BasicDataServiceException {
        return uriFederationService.getCookieFromProxyURIAndPrincipal(request.getProxyURI(), request.getMethod(), request.getPrincipal());
    }
}
