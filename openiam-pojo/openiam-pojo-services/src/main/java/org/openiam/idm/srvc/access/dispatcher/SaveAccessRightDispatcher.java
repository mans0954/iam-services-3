package org.openiam.idm.srvc.access.dispatcher;

import org.openiam.base.request.BaseCrudServiceRequest;
import org.openiam.base.response.StringResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.idm.srvc.access.service.AccessRightService;
import org.openiam.mq.constants.AccessRightAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 08/09/16.
 */
@Component
public class SaveAccessRightDispatcher extends AbstractAPIDispatcher<BaseCrudServiceRequest<AccessRight>, StringResponse, AccessRightAPI> {
    @Autowired
    private AccessRightService accessRightService;

    public SaveAccessRightDispatcher() {
        super(StringResponse.class);
    }

    @Override
    protected StringResponse processingApiRequest(AccessRightAPI openIAMAPI, BaseCrudServiceRequest<AccessRight> request) throws BasicDataServiceException {
        StringResponse response = new StringResponse();
        response.setValue(accessRightService.save(request.getObject()));
        return response;
    }
}
