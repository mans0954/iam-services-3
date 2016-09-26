package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.searchbean.ContentProviderSearchBean;
import org.openiam.am.srvc.searchbean.URIPatternSearchBean;
import org.openiam.am.srvc.service.ContentProviderService;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.IntResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.ContentProviderAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 23/09/16.
 */
@Component
public class GetNumOfUriPatternDispatcher extends AbstractAPIDispatcher<BaseSearchServiceRequest<URIPatternSearchBean>, IntResponse, ContentProviderAPI> {
    @Autowired
    private ContentProviderService contentProviderService;

    public GetNumOfUriPatternDispatcher() {
        super(IntResponse.class);
    }

    @Override
    protected IntResponse processingApiRequest(ContentProviderAPI openIAMAPI, BaseSearchServiceRequest<URIPatternSearchBean> request) throws BasicDataServiceException {
        IntResponse response = new IntResponse();
        response.setValue(contentProviderService.getNumOfUriPatterns(request.getSearchBean()));
        return response;
    }
}
