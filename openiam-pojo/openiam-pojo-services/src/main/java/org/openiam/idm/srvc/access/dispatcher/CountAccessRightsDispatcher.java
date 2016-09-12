package org.openiam.idm.srvc.access.dispatcher;

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.IntResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AccessRightSearchBean;
import org.openiam.idm.srvc.access.service.AccessRightService;
import org.openiam.mq.constants.AccessRightAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 08/09/16.
 */
@Component
public class CountAccessRightsDispatcher extends AbstractAPIDispatcher<BaseSearchServiceRequest<AccessRightSearchBean>, IntResponse, AccessRightAPI> {
    @Autowired
    private AccessRightService accessRightService;

    public CountAccessRightsDispatcher() {
        super(IntResponse.class);
    }

    @Override
    protected IntResponse processingApiRequest(AccessRightAPI openIAMAPI, BaseSearchServiceRequest<AccessRightSearchBean> request) throws BasicDataServiceException {
        IntResponse response = new IntResponse();
        response.setValue(accessRightService.count(request.getSearchBean()));
        return response;
    }
}
