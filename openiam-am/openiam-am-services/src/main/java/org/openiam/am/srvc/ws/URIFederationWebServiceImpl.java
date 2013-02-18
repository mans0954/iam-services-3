package org.openiam.am.srvc.ws;

import javax.jws.WebService;

import org.openiam.am.srvc.service.URIFederationService;
import org.openiam.am.srvc.uriauth.dto.URIFederationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("uriFederationWS")
@WebService(endpointInterface = "org.openiam.am.srvc.ws.URIFederationWebService",
            targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "URIFederationWebServicePort",
            serviceName = "URIFederationWebService")
public class URIFederationWebServiceImpl implements URIFederationWebService {

	@Autowired
	private URIFederationService uriFederationService;
	
	@Override
	public URIFederationResponse federateProxyURI(final String userId, final int authLevel, final String proxyURI) {
		return uriFederationService.federateProxyURI(userId, authLevel, proxyURI);
	}

}
