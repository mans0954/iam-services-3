package org.openiam.srvc.am;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.request.URIFederationServiceRequest;
import org.openiam.base.response.URIFederationResponse;
import org.openiam.mq.constants.queue.am.AMQueue;
import org.openiam.mq.constants.URIFederationAPI;
import org.openiam.mq.constants.queue.am.URIFederationQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexander on 30/09/16.
 */
public abstract class AbstractURIFederationAPIService extends AbstractApiService {
    private Map<String, HttpMethod> httpMethodMap = new HashMap<String, HttpMethod>();

    @PostConstruct
    public void init() {
        for(final HttpMethod method : HttpMethod.values()) {
            httpMethodMap.put(method.name().toLowerCase(), method);
        }
    }

    protected HttpMethod getMethod(final String method) {
        return StringUtils.isNotBlank(method) ? httpMethodMap.get(method.toLowerCase()) : null;
    }

    public AbstractURIFederationAPIService(URIFederationQueue queue) {
        super(queue);
    }

    protected URIFederationResponse getURIFederationMetadata(String proxyURI, String method) {
        URIFederationServiceRequest request = new URIFederationServiceRequest();
        request.setProxyURI(proxyURI);
        request.setMethod(getMethod(method));
        URIFederationResponse response = this.manageApiRequest(URIFederationAPI.URIFederationMetadata, request, URIFederationResponse.class);
        return response;
    }
}
