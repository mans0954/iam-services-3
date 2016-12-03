package org.openiam.srvc.am;

import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.data.ContentProviderResponse;
import org.openiam.base.response.URIFederationResponse;
import org.openiam.base.response.data.URIPatternResponse;
import org.openiam.mq.constants.api.URIFederationAPI;
import org.openiam.mq.constants.queue.am.URIFederationQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

/**
 * Created by alexander on 10/08/16.
 */
@WebService(endpointInterface = "org.openiam.srvc.am.URIFederationWebService",
            targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "URIFederationWebServicePort", serviceName = "URIFederationWebService")
@Service("uriFederationWebServiceComponent")
public class URIFederationWebServiceImpl extends AbstractURIFederationAPIService implements URIFederationWebService {

    @Autowired
    public URIFederationWebServiceImpl(URIFederationQueue queue) {
        super(queue);
    }

    @Override
    public URIFederationResponse getMetadata(String proxyURI, String method) {
        return getURIFederationMetadata(proxyURI, method);
    }

    @Override
    public ContentProvider getCachedContentProvider(String providerId) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(providerId);
        return getValue(URIFederationAPI.CachedContentProviderGet, request, ContentProviderResponse.class);
    }

    @Override
    public URIPattern getCachedURIPattern(String patternId) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(patternId);
        return getValue(URIFederationAPI.CachedURIPatternGet, request, URIPatternResponse.class);
    }
}
