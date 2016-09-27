package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.ContentProviderService;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.AuthLevelGroupingResponse;
import org.openiam.base.response.ContentProviderResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.ContentProviderAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 23/09/16.
 */
@Component
public class GetContentProviderDispatcher extends AbstractAPIDispatcher<IdServiceRequest, ContentProviderResponse, ContentProviderAPI> {
    @Autowired
    private ContentProviderService contentProviderService;

    public GetContentProviderDispatcher() {
        super(ContentProviderResponse.class);
    }

    @Override
    protected ContentProviderResponse processingApiRequest(ContentProviderAPI openIAMAPI, IdServiceRequest request) throws BasicDataServiceException {
        ContentProviderResponse response = new ContentProviderResponse();
        response.setValue(contentProviderService.getContentProvider(request.getId()));
        return response;
    }
}
