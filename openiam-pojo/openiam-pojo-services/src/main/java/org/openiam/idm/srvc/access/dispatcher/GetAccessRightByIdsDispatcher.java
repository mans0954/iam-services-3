package org.openiam.idm.srvc.access.dispatcher;

import org.openiam.base.request.IdsServiceRequest;
import org.openiam.base.response.AccessRightListResponse;
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
public class GetAccessRightByIdsDispatcher extends AbstractAPIDispatcher<IdsServiceRequest, AccessRightListResponse, AccessRightAPI> {
    @Autowired
    private AccessRightService accessRightService;

    public GetAccessRightByIdsDispatcher() {
        super(AccessRightListResponse.class);
    }

    @Override
    protected AccessRightListResponse processingApiRequest(AccessRightAPI openIAMAPI, IdsServiceRequest request) throws BasicDataServiceException {
        AccessRightListResponse response = new AccessRightListResponse();
        response.setAccessRightList(accessRightService.findByIds(request.getIds()));
        return response;
    }
}
