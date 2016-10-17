package org.openiam.idm.srvc.org.service.dispatcher;

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.IntResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.OrganizationTypeSearchBean;
import org.openiam.mq.constants.OrganizationTypeAPI;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 17/10/16.
 */
@Component
public class OrgTypeCountDispatcher extends AbstractOrganizationTypeDispatcher<BaseSearchServiceRequest<OrganizationTypeSearchBean>, IntResponse> {

    public OrgTypeCountDispatcher() {
        super(IntResponse.class);
    }

    @Override
    protected IntResponse processingApiRequest(OrganizationTypeAPI openIAMAPI, BaseSearchServiceRequest<OrganizationTypeSearchBean> request) throws BasicDataServiceException {
        IntResponse response = new IntResponse();
        response.setValue(organizationTypeService.count(request.getSearchBean()));
        return response;
    }
}
