package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.ContentProviderService;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.AuthLevelGroupingListResponse;
import org.openiam.base.response.AuthLevelGroupingResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.ContentProviderAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 23/09/16.
 */
@Component
public class GetAuthLevelGroupingListDispatcher extends AbstractAPIDispatcher<BaseServiceRequest, AuthLevelGroupingListResponse, ContentProviderAPI> {
    @Autowired
    private ContentProviderService contentProviderService;

    public GetAuthLevelGroupingListDispatcher() {
        super(AuthLevelGroupingListResponse.class);
    }

    @Override
    protected AuthLevelGroupingListResponse processingApiRequest(ContentProviderAPI openIAMAPI, BaseServiceRequest request) throws BasicDataServiceException {
        AuthLevelGroupingListResponse response = new AuthLevelGroupingListResponse();
        response.setList(contentProviderService.getAuthLevelGroupingList());
        return response;
    }
}
