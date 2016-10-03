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
import org.openiam.mq.constants.OpenIAMAPICommon;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.constants.URIFederationAPI;
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
public class URIFederationWebServiceImpl extends AbstractURIFederationAPIService implements URIFederationWebService {

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
