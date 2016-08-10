package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.URIFederationService;
import org.openiam.base.request.URIFederationServiceRequest;
import org.openiam.base.response.URIFederationResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 10/08/16.
 */
@Component
public class URIFederationMetadataDispatcher extends AbstractAPIDispatcher<URIFederationServiceRequest, URIFederationResponse> {
    @Autowired
    private URIFederationService uriFederationService;

    public URIFederationMetadataDispatcher() {
        super(URIFederationResponse.class);
    }

    @Override
    protected void processingApiRequest(OpenIAMAPI openIAMAPI, URIFederationServiceRequest uriFederationServiceRequest, URIFederationResponse uriFederationResponse) throws BasicDataServiceException {
        URIFederationResponse response = uriFederationService.getMetadata(uriFederationServiceRequest.getProxyURI(), uriFederationServiceRequest.getMethod());

        uriFederationResponse.setErrorMappingList(response.getErrorMappingList());
        uriFederationResponse.setSubstitutionList(response.getSubstitutionList());
        uriFederationResponse.setAuthLevelTokenList(response.getAuthLevelTokenList());
        uriFederationResponse.setRuleTokenList(response.getRuleTokenList());
        uriFederationResponse.setServer(response.getServer());
        uriFederationResponse.setPatternId(response.getPatternId());
        uriFederationResponse.setCpId(response.getCpId());
        uriFederationResponse.setLoginURL(response.getLoginURL());
        uriFederationResponse.setPostbackURLParamName(response.getPostbackURLParamName());
        uriFederationResponse.setAuthProviderId(response.getAuthProviderId());
        uriFederationResponse.setAuthCookieName(response.getAuthCookieName());
        uriFederationResponse.setAuthCookieDomain(response.getAuthCookieDomain());
        uriFederationResponse.setMethodId(response.getMethodId());
        uriFederationResponse.setRedirectTo(response.getRedirectTo());
        uriFederationResponse.setCacheable(response.isCacheable());
        uriFederationResponse.setCacheTTL(response.getCacheTTL());
        uriFederationResponse.setConfigured(response.isConfigured());

    }
}
