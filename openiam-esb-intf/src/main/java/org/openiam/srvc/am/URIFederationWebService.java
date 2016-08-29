package org.openiam.srvc.am;

import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.base.response.URIFederationResponse;

import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Created by alexander on 09/08/16.
 */
@WebService(targetNamespace = "urn:idm.openiam.org/srvc/am/service", name = "URIFederationWebService")
public interface URIFederationWebService {
    URIFederationResponse getMetadata(@WebParam(name = "proxyURI", targetNamespace = "") final String proxyURI,
                                      @WebParam(name = "method", targetNamespace = "") final String method);

    ContentProvider getCachedContentProvider(@WebParam(name = "providerId", targetNamespace = "") final String providerId);

    URIPattern getCachedURIPattern(@WebParam(name = "patternId", targetNamespace = "") final String patternId);
}
