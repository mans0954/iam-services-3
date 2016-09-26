package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.ContentProviderService;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.ContentProviderResponse;
import org.openiam.base.response.URIPatternResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.ContentProviderAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 23/09/16.
 */
@Component
public class GetUriPatternProviderDispatcher extends AbstractAPIDispatcher<IdServiceRequest, URIPatternResponse, ContentProviderAPI> {
    @Autowired
    private ContentProviderService contentProviderService;

    public GetUriPatternProviderDispatcher() {
        super(URIPatternResponse.class);
    }

    @Override
    protected URIPatternResponse processingApiRequest(ContentProviderAPI openIAMAPI, IdServiceRequest request) throws BasicDataServiceException {
        URIPatternResponse response = new URIPatternResponse();
        response.setValue(contentProviderService.getURIPattern(request.getId()));
        return response;
    }
}
