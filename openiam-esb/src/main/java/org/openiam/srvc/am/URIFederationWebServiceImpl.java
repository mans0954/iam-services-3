package org.openiam.srvc.am;

import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.request.URIFederationServiceRequest;
import org.openiam.base.response.ContentProviderResponse;
import org.openiam.base.response.URIFederationResponse;
import org.openiam.base.response.URIPatternResponse;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.jws.WebService;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexander on 10/08/16.
 */
@WebService(endpointInterface = "org.openiam.srvc.am.URIFederationWebService",
            targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "URIFederationWebServicePort", serviceName = "URIFederationWebService")
@Service("uriFederationWebServiceComponent")
public class URIFederationWebServiceImpl extends AbstractApiService implements URIFederationWebService {
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

    public URIFederationWebServiceImpl() {
        super(OpenIAMQueue.URIFederationQueue);
    }

    @Override
    public URIFederationResponse getMetadata(String proxyURI, String method) {
        URIFederationServiceRequest request = new URIFederationServiceRequest();
        request.setProxyURI(proxyURI);
        request.setMethod(getMethod(method));
        URIFederationResponse response = this.manageApiRequest(OpenIAMAPI.URIFederationMetadata, request, URIFederationResponse.class);
        return response;
    }

    @Override
    public ContentProvider getCachedContentProvider(String providerId) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(providerId);
        ContentProviderResponse response = this.manageApiRequest(OpenIAMAPI.CachedContentProviderGet, request, ContentProviderResponse.class);
        if(response.isFailure()){
            return null;
        }
        return response.getProvider();
    }

    @Override
    public URIPattern getCachedURIPattern(String patternId) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(patternId);
        URIPatternResponse response = this.manageApiRequest(OpenIAMAPI.CachedURIPatternGet, request, URIPatternResponse.class);
        if(response.isFailure()){
            return null;
        }
        return response.getUriPattern();
    }
}
