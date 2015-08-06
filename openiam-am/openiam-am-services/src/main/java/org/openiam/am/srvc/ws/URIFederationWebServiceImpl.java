package org.openiam.am.srvc.ws;

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.openiam.am.srvc.service.URIFederationService;
import org.openiam.am.srvc.uriauth.dto.SSOLoginResponse;
import org.openiam.am.srvc.uriauth.dto.URIFederationResponse;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.dto.SSOToken;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.auth.service.AuthenticationWebService;
import org.openiam.idm.srvc.auth.ws.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("uriFederationWS")
@WebService(endpointInterface = "org.openiam.am.srvc.ws.URIFederationWebService",
            targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "URIFederationWebServicePort",
            serviceName = "URIFederationWebService")
public class URIFederationWebServiceImpl implements URIFederationWebService {
	
	private static Logger LOG = Logger.getLogger(URIFederationWebServiceImpl.class);

	@Autowired
	private URIFederationService uriFederationService;
	
	@Autowired
	private AuthenticationWebService authenticationService;
	
	@Override
	public URIFederationResponse federateProxyURI(final String userId, final int authLevel, final String proxyURI) {
		return uriFederationService.federateProxyURI(userId, authLevel, proxyURI);
	}

	@Override
	public SSOLoginResponse getCookieFromProxyURIAndPrincipal(final String proxyURI, final String principal) {
		final SSOLoginResponse wsResponse = new SSOLoginResponse(ResponseStatus.SUCCESS);
		try {
			final AuthenticationRequest loginRequest = uriFederationService.createAuthenticationRequest(principal, proxyURI);
			final AuthenticationResponse loginResponse = authenticationService.login(loginRequest);
			if(ResponseStatus.SUCCESS.equals(loginResponse.getStatus())) {
				final Subject subject = loginResponse.getSubject();
				if(subject == null) {
					throw new BasicDataServiceException(ResponseCode.NO_SUBJECT);
				}
				final SSOToken ssoToken = subject.getSsoToken();
				if(ssoToken == null) {
					throw new BasicDataServiceException(ResponseCode.NO_SSO_TOKEN);
				}
				wsResponse.setSsoToken(ssoToken);
			} else {
				wsResponse.fail();
				wsResponse.setLoginError(loginResponse.getAuthErrorCode());
				LOG.warn(String.format("Login attempt unsuccessful for principal '%s', proxyURI '%s', loginRequest: '%s', loginResponse: '%s'", 
										principal, proxyURI, loginRequest, loginResponse));
			}
			wsResponse.setOpeniamPrincipal(loginRequest.getPrincipal());
		} catch(BasicDataServiceException e) {
			wsResponse.fail();
			wsResponse.setErrorText(e.getMessage());
			wsResponse.setErrorCode(e.getCode());
			LOG.warn("Cannot getCookieFromProxyURIAndPrincipal()", e);
		} catch(Throwable e) {
			wsResponse.fail();
			wsResponse.setErrorText(e.getMessage());
			LOG.error("Cannot getCookieFromProxyURIAndPrincipal()", e);
		}
		return wsResponse;
	}

	@Override
	public URIFederationResponse getMetadata(String proxyURI) {
		return uriFederationService.getMetadata(proxyURI);
	}

}
