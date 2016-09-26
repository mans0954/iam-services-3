package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.ContentProviderService;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.AuthLevelAttributeResponse;
import org.openiam.base.response.AuthLevelListResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.ContentProviderAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 23/09/16.
 */
@Component
public class GetAuthLevelListDispatcher extends AbstractAPIDispatcher<BaseServiceRequest, AuthLevelListResponse, ContentProviderAPI> {
    @Autowired
    private ContentProviderService contentProviderService;

    public GetAuthLevelListDispatcher() {
        super(AuthLevelListResponse.class);
    }

    @Override
    protected AuthLevelListResponse processingApiRequest(ContentProviderAPI openIAMAPI, BaseServiceRequest request) throws BasicDataServiceException {
        AuthLevelListResponse response = new AuthLevelListResponse();
        response.setList(contentProviderService.getAuthLevelList());
        return response;
    }
}
