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
 * Created by alexander on 26/09/16.
 */
@Component
public class DeleteContentProviderDispatcher extends AbstractAPIDispatcher<IdServiceRequest, Response, ContentProviderAPI> {
    @Autowired
    private ContentProviderService contentProviderService;

    public DeleteContentProviderDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(ContentProviderAPI openIAMAPI, IdServiceRequest request) throws BasicDataServiceException {
        switch (openIAMAPI){
            case DeleteAuthLevelAttribute:
                contentProviderService.deleteAuthLevelAttribute(request.getId());
                break;
            case DeleteAuthLevelGrouping:
                contentProviderService.deleteAuthLevelGrouping(request.getId());
                break;
            case DeleteContentProvider:
                contentProviderService.deleteContentProvider(request.getId());
                break;
            case DeleteProviderPattern:
                contentProviderService.deleteProviderPattern(request.getId());
                break;
        }
        return new Response();
    }
}
