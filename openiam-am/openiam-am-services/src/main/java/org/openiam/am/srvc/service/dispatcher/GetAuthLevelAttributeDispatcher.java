package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.ContentProviderService;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.AuthLevelAttributeResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.ContentProviderAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 23/09/16.
 */
@Component
public class GetAuthLevelAttributeDispatcher extends AbstractAPIDispatcher<IdServiceRequest, AuthLevelAttributeResponse, ContentProviderAPI> {
    @Autowired
    private ContentProviderService contentProviderService;

    public GetAuthLevelAttributeDispatcher() {
        super(AuthLevelAttributeResponse.class);
    }

    @Override
    protected AuthLevelAttributeResponse processingApiRequest(ContentProviderAPI openIAMAPI, IdServiceRequest request) throws BasicDataServiceException {
        AuthLevelAttributeResponse response = new AuthLevelAttributeResponse();
        response.setValue(contentProviderService.getAuthLevelAttribute(request.getId()));
        return response;
    }
}
