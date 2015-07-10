package org.openiam.am.srvc.ws;

import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.dozer.converter.AuthProviderDozerConverter;
import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.exception.BasicDataServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

/**
 * Created by alexander on 06/07/15.
 */
@Service("OAuthWS")
@WebService(endpointInterface = "org.openiam.am.srvc.ws.OAuthWebService",
        targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "OAuthWebServicePort",
        serviceName = "OAuthWebService")
public class OAuthWebServiceImpl implements OAuthWebService {
    @Autowired
    private AuthProviderService authProviderService;
    @Autowired
    private AuthProviderDozerConverter authProviderDozerConverter;

    @Override
    public AuthProvider getClient(String clientId) {
        return authProviderDozerConverter.convertToDTO(authProviderService.getOAuthClient(clientId), true);
    }
}
