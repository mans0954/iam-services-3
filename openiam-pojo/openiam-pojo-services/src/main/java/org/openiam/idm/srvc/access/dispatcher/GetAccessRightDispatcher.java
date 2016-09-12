package org.openiam.idm.srvc.access.dispatcher;

import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.AccessRightResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.access.service.AccessRightService;
import org.openiam.mq.constants.AccessRightAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 08/09/16.
 */
@Component
public class GetAccessRightDispatcher extends AbstractAPIDispatcher<IdServiceRequest, AccessRightResponse, AccessRightAPI> {
    @Autowired
    private AccessRightService accessRightService;

    public GetAccessRightDispatcher() {
        super(AccessRightResponse.class);
    }

    @Override
    protected AccessRightResponse processingApiRequest(AccessRightAPI openIAMAPI, IdServiceRequest request) throws BasicDataServiceException {
        AccessRightResponse response = new AccessRightResponse();
        response.setAccessRight(accessRightService.get(request.getId()));
        return response;
    }
}
