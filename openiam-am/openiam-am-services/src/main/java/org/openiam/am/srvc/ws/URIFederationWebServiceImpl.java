package org.openiam.am.srvc.ws;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.am.srvc.service.URIFederationService;
import org.openiam.am.srvc.uriauth.dto.SSOLoginResponse;
import org.openiam.am.srvc.uriauth.dto.URIFederationResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.dto.SSOToken;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.auth.service.AuthenticationService;
import org.openiam.idm.srvc.auth.ws.AuthenticationResponse;
import org.openiam.thread.Sweepable;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@WebService(endpointInterface = "org.openiam.am.srvc.ws.URIFederationWebService",
            targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "URIFederationWebServicePort",
            serviceName = "URIFederationWebService")
@Service("uriFederationWebServiceComponent")
public class URIFederationWebServiceImpl implements URIFederationWebService {
	
	private static Logger LOG = Logger.getLogger(URIFederationWebServiceImpl.class);

	@Autowired
	private URIFederationService uriFederationService;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	private Map<String, HttpMethod> httpMethodMap = new HashMap<String, HttpMethod>();
	
	@PostConstruct
	public void init() {
		for(final HttpMethod method : HttpMethod.values()) {
			httpMethodMap.put(method.name().toLowerCase(), method);
		}
	}

	private HttpMethod getMethod(final String method) {
		return StringUtils.isNotBlank(method) ? httpMethodMap.get(method.toLowerCase()) : null;
	}

    @Override
	public URIFederationResponse federateProxyURI(final String userId, final String proxyURI, final String method) {
		return uriFederationService.federateProxyURI(userId, proxyURI, getMethod(method));
	}

	@Override
	public SSOLoginResponse getCookieFromProxyURIAndPrincipal(final String proxyURI, final String principal, final String method) {
		final SSOLoginResponse wsResponse = new SSOLoginResponse(ResponseStatus.SUCCESS);
		try {
			final AuthenticationRequest loginRequest = uriFederationService.createAuthenticationRequest(principal, proxyURI, getMethod(method));
			loginRequest.setLanguageId("1"); //set default
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
				wsResponse.setLoginError(loginResponse.getErrorCode());
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
	public URIFederationResponse getMetadata(String proxyURI, final String method) {
		return uriFederationService.getMetadata(proxyURI, getMethod(method));
    }

	@Override
	public void sweep() {
		((Sweepable)uriFederationService).sweep();
	}
}
