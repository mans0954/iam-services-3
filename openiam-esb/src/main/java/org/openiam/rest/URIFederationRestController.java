package org.openiam.rest;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.service.URIFederationService;
import org.openiam.am.srvc.uriauth.dto.SSOLoginResponse;
import org.openiam.am.srvc.uriauth.dto.URIFederationResponse;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.dto.SSOToken;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.auth.service.AuthenticationService;
import org.openiam.idm.srvc.auth.ws.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Used by the OpenIAM proxy for just about every single HTTP Request.
 * 
 * Do *not* modify without talking to the entire team
 * 
 * @author Lev Bornovalov
 *
 */
@RestController
@RequestMapping("/auth/proxy/")
public class URIFederationRestController {

	private static final Log LOG = LogFactory.getLog(URIFederationRestController.class);

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

	@RequestMapping(value="/federateUser", method=RequestMethod.GET)
	public @ResponseBody URIFederationResponse federateProxyURI(final @RequestParam(required=true, value="userId") String userId, 
																final @RequestParam(required=true, value="proxyURI") String proxyURI, 
																final @RequestParam(required=true, value="method") String method) {
		return uriFederationService.federateProxyURI(userId, proxyURI, getMethod(method));
	}
	
	/**
	 * Method called by Reverse Proxy via SOAP Request
	 * Calculates a user's cookie based on the principal and the proxyURI.  If the proxyURI matches an existing Content Provider, the system will look up the user's information based on the given
	 * principal and the Managed System of the Content Provider.
	 * @param proxyURI - the FULL <b>PROXY</b> URI being accessed.  i.e. http://www.openiam.com/appContext/index.html
	 * @param principal - the principal for this request.  Must correspond to the managed system of the Content Provider found from the <b>proxyURI</b> parameter
	 * @return a Response that contains the SSOToken
	 */
	@RequestMapping(value="/getCookieFromProxyURIAndPrincipal", method=RequestMethod.GET)
	public @ResponseBody SSOLoginResponse getCookieFromProxyURIAndPrincipal(final @RequestParam(required=true, value="proxyURI") String proxyURI, 
															  				final @RequestParam(required=true, value="principal") String principal, 
															  				final @RequestParam(required=true, value="method") String method) {
		final SSOLoginResponse wsResponse = new SSOLoginResponse(ResponseStatus.SUCCESS);
		try {
			final AuthenticationRequest loginRequest = uriFederationService.createAuthenticationRequest(principal, proxyURI, getMethod(method));
			loginRequest.setLanguageId("1"); //set default
			loginRequest.setKerberosAuth(true);
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

	@RequestMapping(value="/metadata", method=RequestMethod.GET)
	public @ResponseBody URIFederationResponse getMetadata(final @RequestParam(required=true, value="proxyURI") String proxyURI, 
											 final @RequestParam(required=true, value="method") String method) {
		return uriFederationService.getMetadata(proxyURI, getMethod(method));
    }
}
