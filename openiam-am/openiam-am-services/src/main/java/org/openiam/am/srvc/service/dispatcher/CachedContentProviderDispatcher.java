package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.URIFederationService;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.ContentProviderResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMAPICommon;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 10/08/16.
 */
@Component
public class CachedContentProviderDispatcher extends AbstractAPIDispatcher<IdServiceRequest, ContentProviderResponse, OpenIAMAPICommon> {
    @Autowired
    private URIFederationService uriFederationService;

    public CachedContentProviderDispatcher() {
        super(ContentProviderResponse.class);
    }

    @Override
    protected ContentProviderResponse processingApiRequest(OpenIAMAPICommon openIAMAPI, IdServiceRequest idServiceRequest) throws BasicDataServiceException {
        ContentProviderResponse contentProviderResponse = new ContentProviderResponse();
        contentProviderResponse.setProvider(uriFederationService.getCachedContentProvider(idServiceRequest.getId()));
        return contentProviderResponse;
    }
}
