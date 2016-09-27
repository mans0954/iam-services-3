package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.ContentProviderService;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.ContentProviderAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 23/09/16.
 */
@Component
public class CreateDefaultURIPatternsDispatcher extends AbstractAPIDispatcher<IdServiceRequest, Response, ContentProviderAPI> {
    @Autowired
    private ContentProviderService contentProviderService;

    public CreateDefaultURIPatternsDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(ContentProviderAPI openIAMAPI, IdServiceRequest request) throws BasicDataServiceException {
        contentProviderService.createDefaultURIPatterns(request.getId());
        return new Response();
    }
}
