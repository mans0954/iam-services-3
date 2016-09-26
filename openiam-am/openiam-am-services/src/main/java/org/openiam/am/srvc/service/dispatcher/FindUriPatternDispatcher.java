package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.searchbean.ContentProviderSearchBean;
import org.openiam.am.srvc.searchbean.URIPatternSearchBean;
import org.openiam.am.srvc.service.ContentProviderService;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.ContentProviderListResponse;
import org.openiam.base.response.URIPatternListResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.ContentProviderAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 23/09/16.
 */
@Component
public class FindUriPatternDispatcher extends AbstractAPIDispatcher<BaseSearchServiceRequest<URIPatternSearchBean>, URIPatternListResponse, ContentProviderAPI> {
    @Autowired
    private ContentProviderService contentProviderService;

    public FindUriPatternDispatcher() {
        super(URIPatternListResponse.class);
    }

    @Override
    protected URIPatternListResponse processingApiRequest(ContentProviderAPI openIAMAPI, BaseSearchServiceRequest<URIPatternSearchBean> request) throws BasicDataServiceException {
        URIPatternListResponse response = new URIPatternListResponse();
        response.setList(contentProviderService.getUriPatternsList(request.getSearchBean(), request.getFrom(), request.getSize()));
        return response;
    }
}
