package org.openiam.am.srvc.ws;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.jws.WebService;

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
import org.openiam.thread.Sweepable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@WebService(endpointInterface = "org.openiam.am.srvc.ws.URIFederationWebService",
            targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "URIFederationWebServicePort",
            serviceName = "URIFederationWebService")
@Service("uriFederationWebServiceComponent")
public class URIFederationWebServiceImpl implements URIFederationWebService {
	
	private static final Log LOG = LogFactory.getLog(URIFederationWebServiceImpl.class);

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
	public URIFederationResponse getMetadata(String proxyURI, final String method) {
		return uriFederationService.getMetadata(proxyURI, getMethod(method));
    }

	@Override
	public ContentProvider getCachedContentProvider(String providerId) {
		return uriFederationService.getCachedContentProvider(providerId);
	}

	@Override
	public URIPattern getCachedURIPattern(String patternId) {
		return uriFederationService.getCachedURIPattern(patternId);
	}
}
