package org.openiam.idm.srvc.org.service.dispatcher;

import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.OrganizationTypeResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.api.OrganizationTypeAPI;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 17/10/16.
 */
@Component
public class OrgTypeGetByIdDispatcher extends AbstractOrganizationTypeDispatcher<IdServiceRequest, OrganizationTypeResponse> {

    public OrgTypeGetByIdDispatcher() {
        super(OrganizationTypeResponse.class);
    }

    @Override
    protected OrganizationTypeResponse processingApiRequest(OrganizationTypeAPI openIAMAPI, IdServiceRequest request) throws BasicDataServiceException {
        OrganizationTypeResponse response = new OrganizationTypeResponse();
        response.setValue(organizationTypeService.findById(request.getId(), request.getLanguage()));
        return response;
    }
}
