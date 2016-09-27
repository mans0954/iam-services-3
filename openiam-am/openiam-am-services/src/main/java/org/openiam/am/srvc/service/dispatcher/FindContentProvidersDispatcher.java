package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.searchbean.ContentProviderSearchBean;
import org.openiam.am.srvc.service.ContentProviderService;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.response.AuthLevelListResponse;
import org.openiam.base.response.ContentProviderListResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.ContentProviderAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 23/09/16.
 */
@Component
public class FindContentProvidersDispatcher extends AbstractAPIDispatcher<BaseSearchServiceRequest<ContentProviderSearchBean>, ContentProviderListResponse, ContentProviderAPI> {
    @Autowired
    private ContentProviderService contentProviderService;

    public FindContentProvidersDispatcher() {
        super(ContentProviderListResponse.class);
    }

    @Override
    protected ContentProviderListResponse processingApiRequest(ContentProviderAPI openIAMAPI, BaseSearchServiceRequest<ContentProviderSearchBean> request) throws BasicDataServiceException {
        ContentProviderListResponse response = new ContentProviderListResponse();
        response.setList(contentProviderService.findBeans(request.getSearchBean(), request.getFrom(), request.getSize()));
        return response;
    }
}
