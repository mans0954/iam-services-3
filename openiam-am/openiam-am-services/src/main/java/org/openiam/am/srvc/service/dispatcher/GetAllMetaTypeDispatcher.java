package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.ContentProviderService;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.response.URIPatternMetaTypeListResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.ContentProviderAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 23/09/16.
 */
@Component
public class GetAllMetaTypeDispatcher extends AbstractAPIDispatcher<BaseServiceRequest, URIPatternMetaTypeListResponse, ContentProviderAPI> {
    @Autowired
    private ContentProviderService contentProviderService;

    public GetAllMetaTypeDispatcher() {
        super(URIPatternMetaTypeListResponse.class);
    }

    @Override
    protected URIPatternMetaTypeListResponse processingApiRequest(ContentProviderAPI openIAMAPI, BaseServiceRequest request) throws BasicDataServiceException {
        URIPatternMetaTypeListResponse response = new URIPatternMetaTypeListResponse();
        response.setList(contentProviderService.getAllMetaType());
        return response;
    }
}
