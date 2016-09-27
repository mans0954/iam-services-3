package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.ContentProviderService;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.AuthLevelAttributeResponse;
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
public class GetAuthLevelGroupingDispatcher extends AbstractAPIDispatcher<IdServiceRequest, AuthLevelGroupingResponse, ContentProviderAPI> {
    @Autowired
    private ContentProviderService contentProviderService;

    public GetAuthLevelGroupingDispatcher() {
        super(AuthLevelGroupingResponse.class);
    }

    @Override
    protected AuthLevelGroupingResponse processingApiRequest(ContentProviderAPI openIAMAPI, IdServiceRequest request) throws BasicDataServiceException {
        AuthLevelGroupingResponse response = new AuthLevelGroupingResponse();
        response.setValue(contentProviderService.getAuthLevelGrouping(request.getId()));
        return response;
    }
}
